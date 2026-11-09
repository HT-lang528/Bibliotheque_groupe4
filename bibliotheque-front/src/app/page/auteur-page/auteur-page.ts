import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuteurService } from '../../service/auteur-service';
import { Title } from '@angular/platform-browser';
import { rxResource } from '@angular/core/rxjs-interop';
import { Auteur } from '../../model/auteur';

@Component({
  imports: [FormsModule, AsyncPipe ],
  selector: 'app-auteur-page',
  styleUrl: './auteur-page.css',
  templateUrl: './auteur-page.html',
})
export class AuteurPage {
  protected auteurs = rxResource({
    stream: () => this.auteurService.findAll()
  });
  protected formAuteur!: Auteur;
  constructor(private title: Title, private auteurService: AuteurService) {
    this.title.setTitle("Liste des auteurs");
    this.resetFormAuteur();
    
  }

  protected ajouterOuModifier() {
    this.auteurService.save(this.formAuteur).subscribe(() => {
      this.auteurs.reload();
    });

    this.resetFormAuteur();
  }
  protected editer(auteur: Auteur) {
    this.formAuteur = { ...auteur };
  }

  protected supprimer(auteur: Auteur) {
    this.auteurService.remove(auteur).subscribe(() => {
      this.auteurs.reload();
    });
  }
  protected annulerEdition() {
    this.resetFormAuteur();
  }
  private resetFormAuteur() {
    this.formAuteur = { nom: "",prenom:"",nationalite:"" };
  }





}
