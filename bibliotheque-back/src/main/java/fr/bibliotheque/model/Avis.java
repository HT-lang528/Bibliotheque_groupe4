package fr.bibliotheque.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="avis")
public class Avis {

    @Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private double note;

    @Column(length = 25,nullable = false)
    private String commentaire;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name="livre")
    private Livre livre;


    public Avis() {}
    public Avis(Integer id, double note, String commentaire, LocalDate date, Livre livre) {
        this.id = id;
        this.note = note;
        this.commentaire = commentaire;
        this.date = date;
        this.livre = livre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }



}
