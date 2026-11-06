import { AsyncPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { finalize, Observable, startWith, Subject, switchMap } from 'rxjs';
import { Collection } from '../../model/collection';
import { CollectionService } from '../../service/collection-service';

@Component({
  selector: 'app-collection-page',
  imports: [ ReactiveFormsModule, AsyncPipe ],
  templateUrl: './collection-page.html',
  styleUrl: './collection-page.css',
})
export class CollectionPage implements OnInit {
  protected collections$!: Observable<Collection[]>;
  private refresh$: Subject<void> = new Subject<void>(); // Permettra de garder la même référence à l'Observable et alimenter le flux dès qu'un changement sera réalisé

  protected editingCollectionId?: number;
  protected saving = false;
  protected erreur = "";
  protected formCollectionGroup!: FormGroup;
  protected formCollectionNomCtrl!: FormControl

  constructor(
    private title: Title,
    private collectionService: CollectionService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.title.setTitle("Liste des collections");

    // Création du formulaire ReactiveForms
    this.formCollectionNomCtrl = this.formBuilder.control("", Validators.required);

    this.formCollectionGroup = this.formBuilder.group({
      nom: this.formCollectionNomCtrl
    });

    this.collections$ = this.refresh$.pipe(
      // On initialise notre Observable
      startWith(null),

      // Fonction qui sera déclenchée dès qu'une nouvelle valeur dans le flux arrivera
      switchMap(() => {
        return this.collectionService.findAll();
      })
    );
  }

  protected ajouterOuModifier() {
    if (this.formCollectionGroup.invalid || this.saving) {
      return;
    }

  

    const collection: Collection = {
      ...this.formCollectionGroup.getRawValue(),
      id: this.editingCollectionId
    };

    this.saving = true;
    this.erreur = "";
    this.collectionService.save(collection).pipe(finalize(() => this.saving = false)).subscribe({
      next: () => {
        this.annulerEdition();
        this.refresh$.next();
      },
      error: () => this.erreur = "Enregistrement impossible. Vérifie l’accès au serveur et les données.",
    });
  }

  protected editer(collection: Collection) {
    this.editingCollectionId = collection.id;
    this.formCollectionNomCtrl.setValue(collection.nom);
  }

  protected supprimer(collection: Collection) {
    this.collectionService.remove(collection).subscribe(() => {

      this.refresh$.next();
    });
  }

  protected annulerEdition() {
    this.editingCollectionId = undefined;
    this.formCollectionGroup.reset();
  }
}
