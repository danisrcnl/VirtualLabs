import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VmsContcomponentComponent2 } from './vms-contcomponent.component';

describe('VmsContcomponentComponent2', () => {
  let component: VmsContcomponentComponent2;
  let fixture: ComponentFixture<VmsContcomponentComponent2>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VmsContcomponentComponent2 ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsContcomponentComponent2);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
