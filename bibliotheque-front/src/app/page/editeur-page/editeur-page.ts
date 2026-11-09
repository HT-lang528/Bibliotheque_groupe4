import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EditeurService } from '../../service/editeur-service';
import { rxResource } from '@angular/core/rxjs-interop';
import { Editeur } from '../../model/editeur';
import { Title } from '@angular/platform-browser';

@Component({
  imports: [FormsModule],
  selector: 'app-editeur-page',
  styleUrl: './editeur-page.css',
  templateUrl: './editeur-page.html',
})

export class EditeurPage {
  
  constructor(private title: Title, private editeurService: EditeurService) { 
    this.resetFormEditeur();
  }
  
  ngOnInit(): void {
    this.title.setTitle("Liste des éditeurs");
  }

  protected editeurs = rxResource({
    stream: () => this.editeurService.findAll()
    });
  protected formEditeur!: Editeur;

  protected ajouterOuModifier() {
    this.editeurService.save(this.formEditeur).subscribe(() => {
      this.editeurs.reload();
      this.resetFormEditeur();
    });
  }

  protected editer(editeur: Editeur) {
    this.formEditeur = { ...editeur };
  }

  protected supprimer(editeur: Editeur) {
    this.editeurService.remove(editeur).subscribe(() => {
      this.editeurs.reload();
    });
  }

  protected annulerEdition() {
    this.resetFormEditeur();
  }

  private resetFormEditeur() {
    this.formEditeur = { nom: "" , pays : ""};
  }
}
