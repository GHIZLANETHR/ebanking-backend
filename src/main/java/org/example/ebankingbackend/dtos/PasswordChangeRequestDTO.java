// dtos/PasswordChangeRequestDTO.java
package org.example.ebankingbackend.dtos;
import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    private String oldPassword;
    private String newPassword;
}