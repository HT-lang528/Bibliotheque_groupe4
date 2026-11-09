package fr.bibliotheque.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.User;

public interface IDAOUser extends JpaRepository<User, Integer>{
    public Optional<User> findByUsername(String username);

}
