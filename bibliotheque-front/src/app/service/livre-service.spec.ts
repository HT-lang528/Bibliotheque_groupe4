import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { LivreService } from './livre-service';
import { LivreRequest } from '../model/livre';

describe('LivreService', () => {
  let service: LivreService;
  let http: HttpTestingController;
  const livre: LivreRequest = { titre: 'Un livre', resume: null, annee: 2026, auteur: { id: 1 }, editeur: { id: 2 }, collection: null };
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(LivreService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());

  it('charge la liste depuis le backend Livre', () => {
    service.findAll().subscribe(result => expect(result).toEqual([]));
    const request = http.expectOne('/api/livre');
    expect(request.request.method).toBe('GET'); request.flush([]);
  });
  it('crée un livre avec ses identifiants de relations', () => {
    service.save(livre).subscribe();
    const request = http.expectOne('/api/livre');
    expect(request.request.method).toBe('POST'); expect(request.request.body).toEqual(livre);
    request.flush({ ...livre, id: 1 });
  });
  it('modifie le livre désigné par son identifiant', () => {
    service.save(livre, 7).subscribe();
    const request = http.expectOne('/api/livre/7');
    expect(request.request.method).toBe('PUT'); expect(request.request.body).toEqual(livre);
    request.flush({ ...livre, id: 7 });
  });
  it('supprime par identifiant', () => {
    service.remove(7).subscribe();
    const request = http.expectOne('/api/livre/7');
    expect(request.request.method).toBe('DELETE'); request.flush(null);
  });
  it('charge les trois listes de références', () => {
    service.references().subscribe(refs => expect(refs).toEqual({ auteurs: [], editeurs: [], collections: [] }));
    http.expectOne('/api/auteur').flush([]);
    http.expectOne('/api/editeur').flush([]);
    http.expectOne('/api/collection').flush([]);
  });
  it('transmet les erreurs pour que la page les affiche', () => {
    service.remove(7).subscribe({ error: error => expect(error.status).toBe(409) });
    http.expectOne('/api/livre/7').flush('Livre lié à des avis', { status: 409, statusText: 'Conflict' });
  });
});
