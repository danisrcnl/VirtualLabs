import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElaboraticontteacherComponent } from './elaboraticontteacher.component';

describe('ElaboraticontteacherComponent', () => {
  let component: ElaboraticontteacherComponent;
  let fixture: ComponentFixture<ElaboraticontteacherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElaboraticontteacherComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElaboraticontteacherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
