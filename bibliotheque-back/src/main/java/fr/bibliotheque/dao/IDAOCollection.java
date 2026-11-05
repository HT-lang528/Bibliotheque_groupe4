package fr.bibliotheque.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Collection;

public interface IDAOCollection extends JpaRepository<Collection, Integer>{

}
