package fr.bibliotheque.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.bibliotheque.dao.IDAOEditeur;


@RestController 
@RequestMapping("/api/editeur")
public class EditeurRestControlleur {
    @Autowired
    IDAOEditeur daoEditeur;


}
