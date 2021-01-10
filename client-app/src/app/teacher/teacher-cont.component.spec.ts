import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {TeacherContComponent} from './teacher-cont.component';

describe('TeacherContComponent', () => {
  let component: TeacherContComponent;
  let fixture: ComponentFixture<TeacherContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeacherContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeacherContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
