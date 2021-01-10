import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VmscomponentComponent } from './vmscomponent.component';

describe('VmscomponentComponent', () => {
  let component: VmscomponentComponent;
  let fixture: ComponentFixture<VmscomponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VmscomponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmscomponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
