import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../service/auth-service';
import { authGuard } from './auth-guard';

describe('authGuard', () => {
  // Spring valide le JWT ; ce guard vérifie uniquement sa présence.
  let authService: { token: string };
  let router: Router;
  const executeGuard = () => TestBed.runInInjectionContext(() =>
    authGuard({} as ActivatedRouteSnapshot, { url: '/livres' } as RouterStateSnapshot)
  );

  beforeEach(() => {
    authService = { token: '' };
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }],
    });
    router = TestBed.inject(Router);
  });

  it('redirige vers la connexion quand aucun token n’est présent', () => {
    const result = executeGuard();
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe('/login');
  });

  it('autorise la navigation quand un token est présent', () => {
    authService.token = 'token-fictif-pour-le-test';
    expect(executeGuard()).toBe(true);
  });
});
