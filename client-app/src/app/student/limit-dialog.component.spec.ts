import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LimitDialogComponent } from './limit-dialog.component';

describe('LimitDialogComponent', () => {
  let component: LimitDialogComponent;
  let fixture: ComponentFixture<LimitDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LimitDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LimitDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
