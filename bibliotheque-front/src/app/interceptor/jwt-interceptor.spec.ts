import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth-service';
import { jwtInterceptor } from './jwt-interceptor';

describe('jwtInterceptor', () => {
  let http: HttpTestingController;
  let client: HttpClient;
  let auth: { token: string; logout: () => void };
  beforeEach(() => {
    auth = { token: 'test-token', logout: () => { auth.token = ''; } };
    TestBed.configureTestingModule({ providers: [{ provide: Router, useValue: { navigate: () => Promise.resolve(true) } },
      provideHttpClient(withInterceptors([jwtInterceptor])), provideHttpClientTesting(),
      { provide: AuthService, useValue: auth },
    ] });
    http = TestBed.inject(HttpTestingController); client = TestBed.inject(HttpClient);
  });
  afterEach(() => http.verify());
  it('joint le token aux requêtes API privées', () => {
    client.get('/api/livre').subscribe(); const req = http.expectOne('/api/livre');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token'); req.flush([]);
  });
  it('ne transmet aucun token aux routes publiques ni à un tiers', () => {
    for (const url of ['/api/user/auth', '/api/user/subscribe', 'https://example.com/api/livre']) {
      client.post(url, {}).subscribe(); const req = http.expectOne(url);
      expect(req.request.headers.has('Authorization')).toBe(false); req.flush({});
    }
  });
  it('ne fabrique pas de Bearer vide sans connexion', () => {
    auth.token = ''; client.get('/api/livre').subscribe();
    const req = http.expectOne('/api/livre'); expect(req.request.headers.has('Authorization')).toBe(false); req.flush([]);
  });
  it('supprime la connexion refusée en 401', () => {
    client.get('/api/livre').subscribe({ error: () => {} });
    http.expectOne('/api/livre').flush('', { status: 401, statusText: 'Unauthorized' });
    expect(auth.token).toBe('');
  });
  it('conserve la session en cas de droits insuffisants 403', () => {
    client.get('/api/livre').subscribe({ error: () => {} });
    http.expectOne('/api/livre').flush('', { status: 403, statusText: 'Forbidden' });
    expect(auth.token).toBe('test-token');
  });
});
