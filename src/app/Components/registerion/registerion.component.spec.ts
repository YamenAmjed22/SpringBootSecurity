import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterionComponent } from './registerion.component';

describe('RegisterionComponent', () => {
  let component: RegisterionComponent;
  let fixture: ComponentFixture<RegisterionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
