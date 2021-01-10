import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SubjectdialogComponent } from './subjectdialog.component';

describe('SubjectdialogComponent', () => {
  let component: SubjectdialogComponent;
  let fixture: ComponentFixture<SubjectdialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SubjectdialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SubjectdialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
