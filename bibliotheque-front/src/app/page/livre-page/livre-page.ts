import { Component, DestroyRef, ElementRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Title } from '@angular/platform-browser';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { Livre, LivreReferences, LivreRequest } from '../../model/livre';
import { LivreService } from '../../service/livre-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-livre-page',
  imports: [ReactiveFormsModule],
  templateUrl: './livre-page.html',
  styleUrl: './livre-page.css',
})
export class LivrePage implements OnInit {
  private readonly service = inject(LivreService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly title = inject(Title);
  private readonly router = inject(Router);
  @ViewChild('deleteDialog') private deleteDialog!: ElementRef<HTMLDialogElement>;

  readonly livres = signal<Livre[]>([]);
  readonly references = signal<LivreReferences | null>(null);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly referenceError = signal('');
  readonly notice = signal('');
  readonly editingId = signal<number | undefined>(undefined);
  readonly pendingDelete = signal<Livre | null>(null);

  readonly form = new FormGroup({
    titre: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.pattern(/\S/), Validators.maxLength(255)] }),
    resume: new FormControl('', { nonNullable: true }),
    annee: new FormControl<number | null>(null, [Validators.min(0), Validators.max(9999), Validators.pattern(/^\d+$/)]),
    auteurId: new FormControl<number | null>(null, Validators.required),
    editeurId: new FormControl<number | null>(null, Validators.required),
    collectionId: new FormControl<number | null>(null),
  });

  ngOnInit(): void {
    this.title.setTitle('Livres · Bibliothèque');
    this.refresh();
    this.loadReferences();
  }

  voirAvis(idLivre: number): void {
    this.router.navigate(['/avis', idLivre]);
  }

  refresh(): void {
    this.loading.set(true);
    this.error.set('');
    this.service.findAll().pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.loading.set(false)))
      .subscribe({ next: livres => this.livres.set(livres), error: err => this.error.set(this.message(err)) });
  }

  loadReferences(): void {
    this.referenceError.set('');
    this.service.references().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: references => this.references.set(references),
      error: err => this.referenceError.set('Impossible de charger les auteurs, éditeurs et collections. ' + this.message(err)),
    });
  }

  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving() || !this.references()) return;
    const value = this.form.getRawValue();
    if (value.auteurId === null || value.editeurId === null) return;
    // Le backend attend des relations sous la forme { id: ... }.
    const request: LivreRequest = {
      titre: value.titre.trim(), resume: value.resume.trim() || null, annee: value.annee,
      auteur: { id: value.auteurId }, editeur: { id: value.editeurId },
      collection: value.collectionId === null ? null : { id: value.collectionId },
    };
    const editing = this.editingId() !== undefined;
    this.saving.set(true); this.error.set(''); this.notice.set('');
    this.service.save(request, this.editingId()).pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => { this.cancelEdit(); this.notice.set(editing ? 'Livre modifié.' : 'Livre ajouté.'); this.refresh(); },
        error: err => this.error.set(this.message(err)),
      });
  }

  edit(livre: Livre): void {
    if (this.saving()) return;
    this.editingId.set(livre.id); this.notice.set('');
    this.form.reset({ titre: livre.titre, resume: livre.resume || '', annee: livre.annee,
      auteurId: livre.auteur.id, editeurId: livre.editeur.id, collectionId: livre.collection?.id ?? null });
  }

  cancelEdit(): void { this.editingId.set(undefined); this.form.reset(); }

  requestDelete(livre: Livre): void {
    this.pendingDelete.set(livre);
    this.deleteDialog.nativeElement.showModal();
  }
  cancelDelete(): void { this.deleteDialog.nativeElement.close(); this.pendingDelete.set(null); }
  confirmDelete(): void {
    const livre = this.pendingDelete();
    if (!livre || this.saving()) return;
    this.saving.set(true); this.error.set(''); this.notice.set('');
    this.service.remove(livre.id).pipe(takeUntilDestroyed(this.destroyRef), finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.cancelDelete();
          if (this.editingId() === livre.id) this.cancelEdit();
          this.notice.set('Livre supprimé.'); this.refresh();
        },
        error: err => { this.cancelDelete(); this.error.set(this.message(err)); },
      });
  }

  private message(error: HttpErrorResponse): string {
    if (error.status === 0) return 'Le serveur est inaccessible. Vérifie que le backend est démarré.';
    if (error.status === 401 || error.status === 403) return 'Accès refusé : une connexion ou les autorisations du groupe sont nécessaires.';
    if (error.status === 409) return 'Suppression impossible : ce livre possède peut-être des avis ou des données liées.';
    if (error.status === 404) return 'Le livre ou une référence n’existe plus. Actualise les données.';
    if (error.status === 400) return 'Vérifie les champs du formulaire et les références sélectionnées.';
    return 'Une erreur est survenue. Réessaie dans un instant.';
  }

}
