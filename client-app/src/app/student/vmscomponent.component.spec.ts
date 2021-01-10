import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { VmscomponentComponent2 } from './vmscomponentstudent';



describe('VmscomponentComponent2', () => {
  let component: VmscomponentComponent2;
  let fixture: ComponentFixture<VmscomponentComponent2>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VmscomponentComponent2 ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmscomponentComponent2);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
