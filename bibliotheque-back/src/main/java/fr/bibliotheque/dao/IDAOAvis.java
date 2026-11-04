package fr.bibliotheque.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Avis;


public interface IDAOAvis extends JpaRepository<Avis, Integer> {


}