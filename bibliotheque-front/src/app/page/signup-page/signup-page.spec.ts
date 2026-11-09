import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../service/auth-service';
import { SignupPage } from './signup-page';

describe('SignupPage', () => {
  let calls: unknown[];
  let fail: boolean;
  beforeEach(() => {
    calls = []; fail = false;
    TestBed.configureTestingModule({ imports: [SignupPage], providers: [provideRouter([]), {
      provide: AuthService, useValue: { subscribe: (value: unknown) => {
        calls.push(value); return fail ? throwError(() => ({ status: 409 })) : of(1);
      } },
    }] });
  });
  function page() { const f = TestBed.createComponent(SignupPage); f.detectChanges(); return f; }
  it('refuse le formulaire vide et les confirmations différentes', () => {
    const f = page(); f.componentInstance.submit(); expect(calls).toHaveLength(0);
    f.componentInstance.form.setValue({ username: 'lecteur', password: 'secret-test', confirmation: 'different' });
    f.componentInstance.submit(); expect(calls).toHaveLength(0);
    expect(f.componentInstance.error()).toContain('correspondent');
  });
  it('envoie uniquement identifiant et mot de passe puis affiche la réussite', () => {
    const f = page(); f.componentInstance.form.setValue({ username: 'lecteur', password: 'secret-test', confirmation: 'secret-test' });
    f.componentInstance.submit(); f.detectChanges();
    expect(calls).toEqual([{ username: 'lecteur', password: 'secret-test' }]);
    expect(f.nativeElement.textContent).toContain('Votre compte est créé');
    expect(f.componentInstance.form.controls.password.value).toBe('');
  });
  it('affiche un doublon sans effacer le formulaire', () => {
    fail = true; const f = page();
    f.componentInstance.form.setValue({ username: 'lecteur', password: 'secret-test', confirmation: 'secret-test' });
    f.componentInstance.submit(); expect(f.componentInstance.error()).toContain('déjà utilisé');
    expect(f.componentInstance.form.controls.username.value).toBe('lecteur');
  });
});
