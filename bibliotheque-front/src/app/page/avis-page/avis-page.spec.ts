import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvisPage } from './avis-page';

describe('AvisPage', () => {
  let component: AvisPage;
  let fixture: ComponentFixture<AvisPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvisPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AvisPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
