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
        fr.bibliotheque.model.User compte = daoUser.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return User.withUsername(compte.getUsername())
            .password(compte.getPassword())
            .authorities(compte.getRole())
            .build();
    }
}
