package fr.bibliotheque.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.model.Livre;

public interface IDAOLivre extends JpaRepository<Livre, Integer> {
    public List<Livre> findByEditeurId(Number id);

    public List<Livre> findByAuteurId(Number id);

    public List<Livre> findByCollectionId(Number id);
}
