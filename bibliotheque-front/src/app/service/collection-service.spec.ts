import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CollectionService } from './collection-service';

describe('CollectionService', () => {
  let service: CollectionService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(CollectionService); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('charge les collections sur la bonne route', () => {
    service.findAll().subscribe(); const req = http.expectOne('/api/collection'); expect(req.request.method).toBe('GET'); req.flush([]);
  });
  it('crée une collection', () => {
    service.save({ nom: 'Poche' }).subscribe(); const req = http.expectOne('/api/collection'); expect(req.request.method).toBe('POST'); req.flush({ id: 1, nom: 'Poche' });
  });
  it('modifie une collection', () => {
    service.save({ id: 1, nom: 'Poche' }).subscribe(); const req = http.expectOne('/api/collection/1'); expect(req.request.method).toBe('PUT'); req.flush({ id: 1, nom: 'Poche' });
  });
  it('supprime une collection', () => {
    service.remove({ id: 1, nom: 'Poche' }).subscribe(); const req = http.expectOne('/api/collection/1'); expect(req.request.method).toBe('DELETE'); req.flush(null);
  });
});
