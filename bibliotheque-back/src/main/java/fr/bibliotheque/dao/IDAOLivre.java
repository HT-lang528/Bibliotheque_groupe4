package fr.bibliotheque.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Livre;

public interface IDAOLivre extends JpaRepository<Livre, Integer> {
}
