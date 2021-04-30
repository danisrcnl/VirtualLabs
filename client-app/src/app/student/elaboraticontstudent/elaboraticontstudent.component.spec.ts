import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ElaboraticontstudentComponent } from './elaboraticontstudent.component';

describe('ElaboraticontstudentComponent', () => {
  let component: ElaboraticontstudentComponent;
  let fixture: ComponentFixture<ElaboraticontstudentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ElaboraticontstudentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ElaboraticontstudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
