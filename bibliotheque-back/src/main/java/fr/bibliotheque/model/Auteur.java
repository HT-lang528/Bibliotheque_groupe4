package fr.bibliotheque.model;

import org.hibernate.annotations.Audited.Table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "auteur")
public class Auteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "nom",length = 50, nullable = false)
    private String nom;

    @Column (name = "prenom", length = 50, nullable = false)
    private String prenom;

    @Column (name = "nationalite", length = 50,  nullable = false)
    private String nationalite;

    public Auteur() {
    }

    public Auteur(Integer id,String nationalite, String nom, String prenom) {
        this.id = id;
        this.nationalite = nationalite;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    
    
}
