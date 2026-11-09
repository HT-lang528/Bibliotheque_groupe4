import { AsyncPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { Observable, startWith, Subject, switchMap } from 'rxjs';
import { Avis } from '../../model/avis';
import { AvisService } from '../../service/avis-service';

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
    private formBuilder: FormBuilder
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

    this.avis$ = this.refresh$.pipe(
      // On initialise notre Observable
      startWith(null),
      // Fonction qui sera déclenchée dès qu'une nouvelle valeur dans le flux arrivera
      switchMap(()=>{
        return this.avisService.findAll();
      })
    )
  }

  protected ajouterOuModifier(){
    if (this.formAvisGrp.invalid) {
    return;
  }
    const avis: Avis = {
      ...this.formAvisGrp.getRawValue(),
      id: this.editingAvisId
    };

    this.avisService.save(avis).subscribe(() => {
      this.refresh$.next();
    });

    this.formAvisGrp.reset();
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
