package fr.bibliotheque.restcontroller;

import java.time.LocalDate;
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

import fr.bibliotheque.dao.IDAOAvis;
import fr.bibliotheque.model.Avis;

@RestController
@RequestMapping("/api/avis")
public class AvisRestController {
    @Autowired 
    IDAOAvis daoAvis;
    
	@GetMapping
	public List<Avis> afficherTous()
	{
	  return daoAvis.findAll();
	}

	@GetMapping("/{id}")
	public Avis afficherParId(@PathVariable Integer id)
	{
		return daoAvis.findById(id).orElse(null);
	}

	@GetMapping("/livre/{id}")
	public List<Avis> getAvisByLivre(@PathVariable Integer id) {
		return daoAvis.findByLivreId(id);
	}

	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable Integer id)
	{
		daoAvis.deleteById(id);
	}

	@PostMapping
	public Avis ajouter(@RequestBody Avis avis)
	{
		avis.setDate(LocalDate.now());
		daoAvis.save(avis);
		return avis;
	}

	@PutMapping("/{id}")
	public Avis modifier(@PathVariable Integer id,@RequestBody Avis avis)
	{
	  avis.setId(id);
	  avis.setDate(LocalDate.now());
      daoAvis.save(avis);
      return avis;
	}
}
