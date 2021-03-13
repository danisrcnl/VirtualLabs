import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VmsContcomponentComponent } from './vms-contcomponent.component';

describe('VmsContcomponentComponent', () => {
  let component: VmsContcomponentComponent;
  let fixture: ComponentFixture<VmsContcomponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VmsContcomponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsContcomponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
