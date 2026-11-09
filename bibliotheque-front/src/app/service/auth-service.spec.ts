import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth-service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;
  const validToken = () => 'header.' + btoa(JSON.stringify({ exp: Date.now() / 1000 + 3600 })) + '.signature';
  beforeEach(() => {
    sessionStorage.removeItem('token');
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AuthService); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => { http.verify(); sessionStorage.removeItem('token'); });
  it('appelle la route de connexion et lit le JSON token', () => {
    service.login({ username: 'lecteur', password: 'secret-test' }).subscribe(r => expect(r.token).toBe('jwt-test'));
    const req = http.expectOne('/api/user/auth'); expect(req.request.method).toBe('POST');
    req.flush({ success: true, token: 'jwt-test' });
  });
  it('inscrit sans envoyer de rôle', () => {
    service.subscribe({ username: 'lecteur', password: 'secret-test' }).subscribe(id => expect(id).toBe(1));
    const req = http.expectOne('/api/user/subscribe');
    expect(req.request.body).toEqual({ username: 'lecteur', password: 'secret-test' }); req.flush(1);
  });
  it('déconnecte en vidant le service et le stockage', () => {
    service.token = validToken(); expect(sessionStorage.getItem('token')).toBeTruthy();
    service.logout(); expect(service.token).toBe(''); expect(sessionStorage.getItem('token')).toBeNull();
  });
  it('supprime les tokens expirés ou mal formés', () => {
    for (const token of ['incorrect', 'h.' + btoa(JSON.stringify({ exp: 1 })) + '.s']) {
      service.token = token; expect(service.token).toBe(''); expect(sessionStorage.getItem('token')).toBeNull();
    }
  });
  it('conserve un token non expiré pour les appels API', () => {
    const token = validToken(); service.token = token; expect(service.token).toBe(token);
  });
});
