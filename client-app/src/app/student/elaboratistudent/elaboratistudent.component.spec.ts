import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElaboratistudentComponent } from './elaboratistudent.component';

describe('ElaboratistudentComponent', () => {
  let component: ElaboratistudentComponent;
  let fixture: ComponentFixture<ElaboratistudentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElaboratistudentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElaboratistudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
