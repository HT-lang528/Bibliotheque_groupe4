package fr.bibliotheque.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.bibliotheque.entity.Livre;
import fr.bibliotheque.repository.LivreRepository;

@Service
public class LivreService {

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<Livre> findAll() {
        return livreRepository.findAll();
    }

    public Livre findById(Long id) {
        return livreRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Livre introuvable : " + id));
    }

    public Livre create(Livre livre) {
        livre.setId(null);
        return livreRepository.save(livre);
    }

    public Livre update(Long id, Livre livre) {
        Livre livreExistant = findById(id);

        livreExistant.setTitre(livre.getTitre());
        livreExistant.setResume(livre.getResume());
        livreExistant.setAnnee(livre.getAnnee());

        return livreRepository.save(livreExistant);
    }

    public void delete(Long id) {
        Livre livre = findById(id);
        livreRepository.delete(livre);
    }
}