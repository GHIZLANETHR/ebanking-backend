// web/DashboardRestController.java
package org.example.ebankingbackend.web;

import org.example.ebankingbackend.entities.AccountOperation;
import org.example.ebankingbackend.entities.BankAccount;
import org.example.ebankingbackend.enums.OperationType;
import org.example.ebankingbackend.repositories.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin("*")
public class DashboardRestController {
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    public DashboardRestController(CustomerRepository customerRepository,
                                   BankAccountRepository bankAccountRepository,
                                   AccountOperationRepository accountOperationRepository) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalAccounts", bankAccountRepository.count());

        double totalBalance = bankAccountRepository.findAll().stream()
                .mapToDouble(acc -> acc.getBalance()).sum();
        stats.put("totalBalance", totalBalance);

        // Nombre d'opérations par type
        List<AccountOperation> allOps = accountOperationRepository.findAll();
        long debitCount = allOps.stream().filter(op -> op.getType() == OperationType.DEBIT).count();
        long creditCount = allOps.stream().filter(op -> op.getType() == OperationType.CREDIT).count();
        stats.put("debitCount", debitCount);
        stats.put("creditCount", creditCount);
        return stats;
    }

    @GetMapping("/balance-per-account-type")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, Double> getBalancePerAccountType() {
        // Suppose que BankAccount a une méthode getType() via le discriminateur
        double currentTotal = bankAccountRepository.findAll().stream()
                .filter(acc -> acc.getClass().getSimpleName().equals("CurrentAccount"))
                .mapToDouble(BankAccount::getBalance).sum();
        double savingTotal = bankAccountRepository.findAll().stream()
                .filter(acc -> acc.getClass().getSimpleName().equals("SavingAccount"))
                .mapToDouble(BankAccount::getBalance).sum();
        return Map.of("CurrentAccount", currentTotal, "SavingAccount", savingTotal);
    }

    @GetMapping("/last-12-months-operations")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, Map<String, Long>> getLast12MonthsOperations() {
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusMonths(12);
        List<AccountOperation> operations = accountOperationRepository.findAll().stream()
                .filter(op -> op.getOperationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(oneYearAgo))
                .collect(Collectors.toList());

        Map<String, Map<String, Long>> result = new HashMap<>();
        result.put("debits", new LinkedHashMap<>());
        result.put("credits", new LinkedHashMap<>());

        for (int i = 0; i < 12; i++) {
            LocalDate monthDate = oneYearAgo.plusMonths(i);
            String monthKey = monthDate.getYear() + "-" + String.format("%02d", monthDate.getMonthValue());
            result.get("debits").put(monthKey, 0L);
            result.get("credits").put(monthKey, 0L);
        }

        for (AccountOperation op : operations) {
            LocalDate date = op.getOperationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String monthKey = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            if (op.getType() == OperationType.DEBIT) {
                result.get("debits").put(monthKey, result.get("debits").getOrDefault(monthKey, 0L) + 1);
            } else {
                result.get("credits").put(monthKey, result.get("credits").getOrDefault(monthKey, 0L) + 1);
            }
        }
        return result;
    }
}