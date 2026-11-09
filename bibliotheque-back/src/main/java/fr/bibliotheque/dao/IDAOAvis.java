package fr.bibliotheque.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Avis;


public interface IDAOAvis extends JpaRepository<Avis, Integer> {
    public List<Avis> findByLivreId(Integer id);
    
}