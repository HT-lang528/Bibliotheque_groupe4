import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { CollectionPage } from './collection-page';
import { CollectionService } from '../../service/collection-service';

describe('CollectionPage', () => {
  let fixture: ComponentFixture<CollectionPage>;
  let service: { findAll: ReturnType<typeof vi.fn>; save: ReturnType<typeof vi.fn>; remove: ReturnType<typeof vi.fn> };
  beforeEach(async () => {
    service = { findAll: vi.fn(() => of([{ id: 1, nom: 'Poche' }])), save: vi.fn(() => of({ id: 1, nom: 'Poche' })), remove: vi.fn(() => of(undefined)) };
    await TestBed.configureTestingModule({ imports: [CollectionPage], providers: [{ provide: CollectionService, useValue: service }] }).compileComponents();
    fixture = TestBed.createComponent(CollectionPage); fixture.detectChanges(); await fixture.whenStable();
  });
  it('affiche les noms et le formulaire avec des propriétés cohérentes', () => {
    expect(fixture.nativeElement.querySelector('tbody').textContent).toContain('Poche');
    expect(fixture.nativeElement.querySelector('form')).toBeTruthy();
  });
  it('revient à la création après une modification réussie', async () => {
    const root = fixture.nativeElement as HTMLElement;
    (root.querySelector('tbody button') as HTMLButtonElement).click(); fixture.detectChanges();
    expect(root.querySelector('button[type=submit]')?.textContent).toContain('Modifier');
    root.querySelector('form')!.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges(); await fixture.whenStable();
    expect(service.save).toHaveBeenCalledWith({ id: 1, nom: 'Poche' });
    expect(root.querySelector('button[type=submit]')?.textContent).toContain('Ajouter');
    expect((root.querySelector('input') as HTMLInputElement).value).toBe('');
  });
});
