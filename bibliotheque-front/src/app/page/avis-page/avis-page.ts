import { AsyncPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { Avis } from '../../model/avis';
import { AvisService } from '../../service/avis-service';
import { ActivatedRoute } from '@angular/router';
import { Livre } from '../../model/livre';

@Component({
  imports: [ ReactiveFormsModule, AsyncPipe ],
  selector: 'app-avis-page',
  styleUrl: './avis-page.css',
  templateUrl: './avis-page.html',
})


export class AvisPage implements OnInit{
  protected avis$!: Observable<Avis[]>;
  private refresh$: Subject<void> = new Subject<void>();

  protected editingAvisId?: number; 
  protected formAvisGrp!: FormGroup;
  protected formAvisCommentaireCtrl!: FormControl;
  protected formAvisNoteCtrl!: FormControl;

  constructor(
    private title: Title,
    private avisService: AvisService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute //permet de lire les paramètres de l'URL
  ) { }

  
  ngOnInit(): void {
    this.title.setTitle("Liste des avis");

    // Création du formulaire ReactiveForms
    this.formAvisCommentaireCtrl = this.formBuilder.control("", Validators.required);
    this.formAvisNoteCtrl = this.formBuilder.control("", Validators.required);
    this.formAvisGrp = this.formBuilder.group({
      commentaire: this .formAvisCommentaireCtrl,
      note : this.formAvisNoteCtrl
    });

    const idLivre = Number(this.route.snapshot.paramMap.get('idLivre'));

    this.avis$ = this.refresh$.pipe(
      // On initialise notre Observable
      startWith(null),
      // Fonction qui sera déclenchée dès qu'une nouvelle valeur dans le flux arrivera
      switchMap(()=>{
        return this.avisService.findByLivreId(idLivre);
      })
    )
  }

  protected ajouterOuModifier() {
    if (this.formAvisGrp.invalid) {
      return;
    }

    const idLivre = Number(this.route.snapshot.paramMap.get('idLivre'));

    const avis: Avis = {
      ...this.formAvisGrp.getRawValue(),
      id: this.editingAvisId!,
      livre: { id: idLivre } as Livre
    };

    this.avisService.save(avis).subscribe(() => {
      this.refresh$.next();
    });

    this.formAvisGrp.reset();
    this.editingAvisId = undefined;
  }

  protected editer(avis: Avis) {
    this.editingAvisId = avis.id;
    this.formAvisCommentaireCtrl.setValue(avis.commentaire);
  }

  protected supprimer(avis: Avis) {
    this.avisService.remove(avis).subscribe(() => {
      this.refresh$.next();
    });
  }

  protected annulerEdition() {
    this.editingAvisId = undefined;
    this.formAvisGrp.reset();
  }
}
