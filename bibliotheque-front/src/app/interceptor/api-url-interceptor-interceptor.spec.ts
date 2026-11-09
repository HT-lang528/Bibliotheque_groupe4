import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { apiUrlInterceptor } from './api-url-interceptor-interceptor';

describe('apiUrlInterceptor', () => {
  let client: HttpClient;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [
      provideHttpClient(withInterceptors([apiUrlInterceptor])), provideHttpClientTesting(),
    ] });
    client = TestBed.inject(HttpClient); http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('ajoute le préfixe aux anciennes routes relatives', () => {
    client.post('/user/auth', {}).subscribe(); http.expectOne('/api/user/auth').flush({});
  });
  it('ne double pas le préfixe des routes métier', () => {
    client.get('/api/livre').subscribe(); http.expectOne('/api/livre').flush([]);
  });
  it('conserve les URL absolues et externes', () => {
    for (const url of ['http://localhost:8080/api/auteur', 'https://example.com/data', '//example.com/data']) {
      client.get(url).subscribe(); http.expectOne(url).flush([]);
    }
  });
});
