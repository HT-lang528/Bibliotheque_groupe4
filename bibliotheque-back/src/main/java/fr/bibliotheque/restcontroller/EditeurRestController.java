package fr.bibliotheque.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.bibliotheque.dao.IDAOEditeur;
import fr.bibliotheque.model.Editeur;


@RestController 
@RequestMapping("/api/editeur")
public class EditeurRestController {
    @Autowired
    IDAOEditeur daoEditeur;

    @GetMapping
    public List<Editeur> afficherTous()
    {
	    return daoEditeur.findAll();
	}

    @GetMapping("/{id}")
	public Editeur afficherParId(@PathVariable Integer id)
	{
		return daoEditeur.findById(id).orElse(null);
	}

	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable Integer id)
	{
		daoEditeur.deleteById(id);
	}

	@PostMapping
	public Editeur ajouter(@RequestBody Editeur editeur)
	{
		daoEditeur.save(editeur);
		return editeur;
	}

	@PutMapping("/{id}")
	public Editeur modifier(@PathVariable Integer id,@RequestBody Editeur editeur)
	{
	    editeur.setId(id);
        daoEditeur.save(editeur);
        return editeur;
	}
}