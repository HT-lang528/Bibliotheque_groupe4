import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { AvisService } from '../../service/avis-service';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvisPage } from './avis-page';

describe('AvisPage', () => {
  let component: AvisPage;
  let fixture: ComponentFixture<AvisPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvisPage],
      providers: [{ provide: AvisService, useValue: { findByLivreId: () => of([]), findAll: () => of([]) } }, { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }],
    }).compileComponents();

    fixture = TestBed.createComponent(AvisPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
