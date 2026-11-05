package fr.bibliotheque.dao;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Auteur;



public interface IDAOAuteur extends JpaRepository<Auteur, Integer> {
    
}
