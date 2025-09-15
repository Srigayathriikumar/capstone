import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Teamdetails } from './teamdetails';

describe('Teamdetails', () => {
  let component: Teamdetails;
  let fixture: ComponentFixture<Teamdetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Teamdetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Teamdetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
