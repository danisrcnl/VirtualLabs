import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LimitDialogComponent2 } from './limit-dialog.component';

describe('LimitDialogComponent', () => {
  let component: LimitDialogComponent2;
  let fixture: ComponentFixture<LimitDialogComponent2>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LimitDialogComponent2 ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LimitDialogComponent2);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
