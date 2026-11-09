package fr.bibliotheque.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.bibliotheque.dao.IDAOEditeur;
import fr.bibliotheque.model.Livre;
import fr.bibliotheque.service.LivreService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/livre", "/api/livres"})
public class LivreRestController {

    private final LivreService livreService;

    public LivreRestController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public List<Livre> findAll() {
        return livreService.findAll();
    }

//Permet d'afficher tout les livres qui sont concerné par l'éditeur sélectionné
   @GetMapping("/editeur/{id}")
    public List<Livre> findByEditeurId(@PathVariable Integer id) {
        return livreService.findByEditeurId(id);
    }

//Permet d'afficher tout les livres qui sont concerné par l'auteur sélectionné
   @GetMapping("/auteur/{id}")
    public List<Livre> findByAuteurId(@PathVariable Integer id) {
        return livreService.findByAuteurId(id);
    }

//Permet d'afficher tout les livres qui sont concerné par la collection sélectionnée
   @GetMapping("/collection/{id}")
    public List<Livre> findByCollectionId(@PathVariable Integer id) {
        return livreService.findByCollectionId(id);
    }

    @GetMapping("/{id}")
    public Livre findById(@PathVariable Integer id) {
        return livreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livre create(@Valid @RequestBody Livre livre) {
        return livreService.create(livre);
    }

    @PutMapping("/{id}")
    public Livre update(
            @PathVariable Integer id,
            @Valid @RequestBody Livre livre) {

        return livreService.update(id, livre);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        livreService.delete(id);
    }

    // Traite les erreurs « Livre introuvable » du service.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleLivreIntrouvable(
            IllegalArgumentException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
    // Une suppression ne doit pas effacer les avis ou les entités des collègues.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleConflitDeDonnees(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Opération impossible : vérifie les données et les liens du livre (notamment ses avis).");
    }
}
