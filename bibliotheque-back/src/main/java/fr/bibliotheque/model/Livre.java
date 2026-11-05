package fr.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Première version : les relations seront ajoutées avec les entités du groupe

@Entity
@Table(name = "livre")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String resume;

    private Integer annee;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Auteur auteur;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "editeur_id", nullable = false)
    private Editeur editeur;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    // Constructeur sans argument nécessaire à JPA.
    public Livre() {
    }

    // L'identifiant n'est pas fourni : il sera généré par la base de données.
    
    public Livre(String titre, String resume, Integer annee) {
        this.titre = titre;
        this.resume = resume;
        this.annee = annee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }
    public Auteur getAuteur() { return auteur; }

    public void setAuteur(Auteur auteur) { this.auteur = auteur; }

    public Editeur getEditeur() { return editeur; }

    public void setEditeur(Editeur editeur) { this.editeur = editeur; }

    public Collection getCollection() { return collection; }

    public void setCollection(Collection collection) { this.collection = collection; }
}
