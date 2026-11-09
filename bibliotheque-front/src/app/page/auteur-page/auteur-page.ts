import { AsyncPipe } from '@angular/common';
import { Component,signal  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuteurService } from '../../service/auteur-service';
import { Title } from '@angular/platform-browser';
import { rxResource } from '@angular/core/rxjs-interop';
import { Auteur } from '../../model/auteur';
import { Livre } from '../../model/livre';

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
  protected livres = signal<Livre[]>([]);
  protected auteurSelectionne = signal<Auteur | undefined>(undefined);
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

  protected afficherLivre(auteur: Auteur) {
    if (auteur.id === this.auteurSelectionne()?.id) {
      this.livres.set([]);
      this.auteurSelectionne.set(undefined);
      return;
    }

    this.auteurSelectionne.set(auteur);
    this.auteurService.findLivre(auteur).subscribe(livres => {
      this.livres.set(livres);
    });
  }
}




