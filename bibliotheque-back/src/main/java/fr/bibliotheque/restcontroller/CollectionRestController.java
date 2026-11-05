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

import fr.bibliotheque.dao.IDAOCollection;
import fr.bibliotheque.model.Collection;

@RestController 
@RequestMapping ("api/collection")
public class CollectionRestController {

    @Autowired 
    IDAOCollection daoCollection;

    @GetMapping
    public List<Collection> afficherTous()
    {
	  return daoCollection.findAll();
	}

    @GetMapping("/{id}")
	public Collection afficherParId(@PathVariable Integer id)
	{
		return daoCollection.findById(id).orElse(null);
	}

	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable Integer id)
	{
		daoCollection.deleteById(id);
	}

	@PostMapping
	public Collection ajouter(@RequestBody Collection collection)
	{
		daoCollection.save(collection);
		return collection;
	}

	@PutMapping("/{id}")
	public Collection modifier(@PathVariable Integer id,@RequestBody Collection collection)
	{
	  collection.setId(id);
      daoCollection.save(collection);
      return collection;
	}
}
