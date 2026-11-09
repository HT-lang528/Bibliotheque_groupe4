package fr.bibliotheque.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateur")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column( nullable = false, unique = true)
    private String username;
    @Column( nullable = false)
    private String password;


    @Column(nullable = false, columnDefinition = "varchar(16) default 'USER'")
    private String role = "USER";

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public User() {
    }

    public User(int id, String password, String username) {
        this.id = id;
        this.password = password;
        this.username = username;
    }

    
    


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
