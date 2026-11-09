package fr.bibliotheque.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.bibliotheque.dao.IDAOUser;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private IDAOUser daoUser;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User
            .withUsername(username)
            .password("$2a$10$7LUTWk1B5nqyykF5vUPcF.QD4/8r77GU/BUgAzGUrGC5LY9Ypa7su")
            .authorities("USER")
            .build()
        ;
    }
}
