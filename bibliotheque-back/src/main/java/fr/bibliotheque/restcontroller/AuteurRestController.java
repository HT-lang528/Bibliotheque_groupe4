package fr.bibliotheque.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.bibliotheque.dao.IDAOAuteur;
import fr.bibliotheque.model.Auteur;


@RestController
@RequestMapping("api/auteur")
@CrossOrigin("*")
public class AuteurRestController {
    @Autowired
    private IDAOAuteur daoAuteur;

    @GetMapping()
    public List<Auteur> afficherTous() {
        return daoAuteur.findAll();
    }

    @GetMapping("/{id}")
    public Auteur afficherParId(@PathVariable Integer id) {
        return daoAuteur.findById(id).orElse(null);
    }
    
    @DeleteMapping("/{id}")
	public void supprimer(@PathVariable Integer id)
	{
		daoAuteur.deleteById(id);
	}

    @PostMapping
	public Auteur ajouter(@RequestBody Auteur auteur)
	{
		daoAuteur.save(auteur);
		return auteur;
	}

	@PutMapping("/{id}")
	public Auteur modifier(@PathVariable Integer id,@RequestBody Auteur auteur)
	{
	  auteur.setId(id);
      daoAuteur.save(auteur);
      return auteur;
	}

    
}
