import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsegnadialogComponent } from './consegnadialog.component';

describe('ConsegnadialogComponent', () => {
  let component: ConsegnadialogComponent;
  let fixture: ComponentFixture<ConsegnadialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsegnadialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsegnadialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
