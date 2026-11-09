import { of } from 'rxjs';
import { AuteurService } from '../../service/auteur-service';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuteurPage } from './auteur-page';

describe('AuteurPage', () => {
  let component: AuteurPage;
  let fixture: ComponentFixture<AuteurPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: AuteurService, useValue: { findAll: () => of([]) } }],
      imports: [AuteurPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AuteurPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
