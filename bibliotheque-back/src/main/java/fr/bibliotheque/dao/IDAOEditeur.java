package fr.bibliotheque.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Editeur;


public interface IDAOEditeur extends JpaRepository<Editeur, Integer> {

}
