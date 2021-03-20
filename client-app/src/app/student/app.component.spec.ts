import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponentStudent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponentStudent
      ],
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponentStudent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'Lab4'`, () => {
    const fixture = TestBed.createComponent(AppComponentStudent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('Lab4');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponentStudent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('.content span').textContent).toContain('Lab4 app is running!');
  });
});
