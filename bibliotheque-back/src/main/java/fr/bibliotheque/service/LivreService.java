package fr.bibliotheque.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fr.bibliotheque.dao.IDAOAuteur;
import fr.bibliotheque.dao.IDAOCollection;
import fr.bibliotheque.dao.IDAOEditeur;
import fr.bibliotheque.dao.IDAOLivre;
import fr.bibliotheque.model.Livre;

@Service
@Transactional
public class LivreService {

    private final IDAOLivre daoLivre;
    private final IDAOAuteur daoAuteur;
    private final IDAOEditeur daoEditeur;
    private final IDAOCollection daoCollection;

    public LivreService(IDAOLivre daoLivre, IDAOAuteur daoAuteur,
            IDAOEditeur daoEditeur, IDAOCollection daoCollection) {
        this.daoLivre = daoLivre;
        this.daoAuteur = daoAuteur;
        this.daoEditeur = daoEditeur;
        this.daoCollection = daoCollection;
    }

    @Transactional(readOnly = true)
    public List<Livre> findAll() {
        return daoLivre.findAll();
    }

//Permet d'afficher tout les livres qui sont concerné par l'éditeur sélectionné
    @Transactional(readOnly = true)    
    public List<Livre> findByEditeurId(Number id){
        return daoLivre.findByEditeurId(id);
    }

//Permet d'afficher tout les livres qui sont concerné par l'auteur sélectionné
    @Transactional(readOnly = true)    
    public List<Livre> findByAuteurId(Number id){
        return daoLivre.findByAuteurId(id);
    }

//Permet d'afficher tout les livres qui sont concerné par la collection sélectionnée
    @Transactional(readOnly = true)    
    public List<Livre> findByCollectionId(Number id){
        return daoLivre.findByCollectionId(id);
    }

    @Transactional(readOnly = true)
    public Livre findById(Integer id) {
        return daoLivre.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Livre introuvable : " + id));
    }

    public Livre create(Livre livre) {
        chargerRelations(livre);
        livre.setId(null);
        return daoLivre.save(livre);
    }

    public Livre update(Integer id, Livre modifications) {
        Livre existant = findById(id);
        chargerRelations(modifications);
        existant.setTitre(modifications.getTitre());
        existant.setResume(modifications.getResume());
        existant.setAnnee(modifications.getAnnee());
        existant.setAuteur(modifications.getAuteur());
        existant.setEditeur(modifications.getEditeur());
        existant.setCollection(modifications.getCollection());
        return daoLivre.save(existant);
    }

    public void delete(Integer id) {
        Livre livre = findById(id);
        daoLivre.delete(livre);
        // Force la vérification des clés étrangères avant le retour au contrôleur.
        daoLivre.flush();
    }

    // Les objets reçus en JSON ne doivent pas créer/modifier un auteur ou un éditeur.
    // On recharge les entités existantes avec leurs identifiants.
    private void chargerRelations(Livre livre) {
        if (livre.getAuteur() == null || livre.getAuteur().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant de l'auteur est obligatoire");
        }
        if (livre.getEditeur() == null || livre.getEditeur().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant de l'éditeur est obligatoire");
        }
        Integer auteurId = livre.getAuteur().getId();
        Integer editeurId = livre.getEditeur().getId();
        livre.setAuteur(daoAuteur.findById(auteurId).orElseThrow(
                () -> new IllegalArgumentException("Auteur introuvable : " + auteurId)));
        livre.setEditeur(daoEditeur.findById(editeurId).orElseThrow(
                () -> new IllegalArgumentException("Éditeur introuvable : " + editeurId)));

        // Une collection est facultative ; si elle est fournie, son id doit exister.
        if (livre.getCollection() != null) {
            Integer collectionId = livre.getCollection().getId();
            if (collectionId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant de la collection est obligatoire si elle est renseignée");
            }
            livre.setCollection(daoCollection.findById(collectionId).orElseThrow(
                    () -> new IllegalArgumentException("Collection introuvable : " + collectionId)));
        }
    }
}
