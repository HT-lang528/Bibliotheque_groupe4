import { AuthService } from './service/auth-service';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [App], providers: [provideRouter([]), { provide: AuthService, useValue: { token: 'test', logout() {} } }] }).compileComponents();
  });
  it('affiche le point d’entrée de la bibliothèque et le lien Livres', async () => {
    const fixture = TestBed.createComponent(App); fixture.detectChanges(); await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('nav')?.textContent).toContain('Accueil');
    expect(element.querySelector('a[href="/livres"]')).not.toBeNull();
  });
});
