import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { LivrePage } from './livre-page';
import { LivreService } from '../../service/livre-service';
import { Livre, LivreRequest } from '../../model/livre';

describe('LivrePage', () => {
  let fixture: ComponentFixture<LivrePage>;
  let page: LivrePage;
  let service: { findAll: ReturnType<typeof vi.fn>; references: ReturnType<typeof vi.fn>; save: ReturnType<typeof vi.fn>; remove: ReturnType<typeof vi.fn> };
  const auteur = { id: 1, prenom: 'Antoine', nom: 'de Saint-Exupéry' };
  const editeur = { id: 2, nom: 'Gallimard' };
  const livre: Livre = { id: 7, titre: 'Le Petit Prince', resume: 'Un voyage', annee: 1943, auteur, editeur, collection: null };

  beforeEach(async () => {
    service = {
      findAll: vi.fn(() => of([livre])),
      references: vi.fn(() => of({ auteurs: [auteur], editeurs: [editeur], collections: [] })),
      save: vi.fn(() => of(livre)), remove: vi.fn(() => of(undefined)),
    };
    await TestBed.configureTestingModule({ imports: [LivrePage], providers: [{ provide: LivreService, useValue: service }] }).compileComponents();
    fixture = TestBed.createComponent(LivrePage); page = fixture.componentInstance;
    fixture.detectChanges(); await fixture.whenStable();
    // JSDOM ne gère pas toujours l'ouverture native des dialogues.
    const dialog = fixture.nativeElement.querySelector('dialog') as HTMLDialogElement;
    dialog.showModal = vi.fn(); dialog.close = vi.fn();
  });

  function remplir(): void {
    page.form.setValue({ titre: '  Un nouveau livre  ', resume: '', annee: 2026, auteurId: 1, editeurId: 2, collectionId: null });
  }

  it('affiche les livres et leurs références', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Le Petit Prince'); expect(text).toContain('Antoine'); expect(text).toContain('Gallimard');
  });
  it('refuse un formulaire sans titre', () => {
    remplir(); page.form.controls.titre.setValue('   '); page.save();
    expect(service.save).not.toHaveBeenCalled(); expect(page.form.invalid).toBe(true);
  });
  it('refuse un livre sans auteur ou éditeur', () => {
    remplir(); page.form.controls.auteurId.setValue(null); page.save();
    expect(service.save).not.toHaveBeenCalled();
  });
  it('envoie une création avec une collection facultative et le titre nettoyé', () => {
    remplir(); page.save();
    const expected: LivreRequest = { titre: 'Un nouveau livre', resume: null, annee: 2026, auteur: { id: 1 }, editeur: { id: 2 }, collection: null };
    expect(service.save).toHaveBeenCalledWith(expected, undefined);
    expect(page.notice()).toBe('Livre ajouté.'); expect(page.form.controls.titre.value).toBe('');
  });
  it('préremplit une édition et transmet le bon identifiant', () => {
    page.edit(livre); expect(page.form.controls.titre.value).toBe('Le Petit Prince');
    page.form.controls.titre.setValue('Titre corrigé'); page.save();
    expect(service.save).toHaveBeenCalledWith(expect.objectContaining({ titre: 'Titre corrigé' }), 7);
    expect(page.editingId()).toBeUndefined();
  });
  it('conserve le formulaire si le backend refuse un enregistrement', () => {
    service.save.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400 })));
    remplir(); page.save();
    expect(page.error()).toContain('Vérifie'); expect(page.form.controls.titre.value).toContain('Un nouveau livre'); expect(page.saving()).toBe(false);
  });
  it('ne supprime rien avant confirmation et permet d’annuler', () => {
    page.requestDelete(livre); expect(service.remove).not.toHaveBeenCalled();
    page.cancelDelete(); expect(page.pendingDelete()).toBeNull(); expect(service.remove).not.toHaveBeenCalled();
  });
  it('supprime le livre seulement après confirmation', () => {
    page.requestDelete(livre); page.confirmDelete();
    expect(service.remove).toHaveBeenCalledWith(7); expect(page.notice()).toBe('Livre supprimé.');
  });
  it('explique un refus de suppression lié aux avis', () => {
    service.remove.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 409 })));
    page.requestDelete(livre); page.confirmDelete(); expect(page.error()).toContain('avis'); expect(page.livres()).toHaveLength(1);
  });
  it('affiche un message si la sécurité refuse l’accès', () => {
    service.findAll.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    page.refresh(); fixture.detectChanges(); expect(fixture.nativeElement.textContent).toContain('Accès refusé');
  });
  it('signale les références indisponibles', () => {
    service.references.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 0 })));
    page.references.set(null); page.loadReferences(); remplir(); page.save();
    expect(page.referenceError()).toContain('serveur'); expect(service.save).not.toHaveBeenCalled();
  });
});
