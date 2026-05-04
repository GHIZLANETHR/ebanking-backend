package org.example.ebankingbackend.security;

import org.example.ebankingbackend.entities.AppUser;
import org.example.ebankingbackend.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null)
            throw new UsernameNotFoundException("User not found: " + username);

        String[] roles = appUser.getRoles()
                .stream()
                .map(r -> r.getRoleName())
                .toArray(String[]::new);

        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(roles)
                .build();
    }
}