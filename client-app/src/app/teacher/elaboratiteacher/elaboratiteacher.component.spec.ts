import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElaboratiteacherComponent } from './elaboratiteacher.component';

describe('ElaboratiteacherComponent', () => {
  let component: ElaboratiteacherComponent;
  let fixture: ComponentFixture<ElaboratiteacherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElaboratiteacherComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElaboratiteacherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
