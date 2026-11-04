package fr.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.bibliotheque.entity.Livre;

public interface LivreRepository extends JpaRepository<Livre, Long> {
}

