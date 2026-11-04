package fr.bibliotheque.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.bibliotheque.entity.Livre;
import fr.bibliotheque.service.LivreService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public List<Livre> findAll() {
        return livreService.findAll();
    }

    @GetMapping("/{id}")
    public Livre findById(@PathVariable Long id) {
        return livreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livre create(@Valid @RequestBody Livre livre) {
        return livreService.create(livre);
    }

    @PutMapping("/{id}")
    public Livre update(
            @PathVariable Long id,
            @Valid @RequestBody Livre livre) {

        return livreService.update(id, livre);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        livreService.delete(id);
    }

    // Traite les erreurs « Livre introuvable » du service.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleLivreIntrouvable(
            IllegalArgumentException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}