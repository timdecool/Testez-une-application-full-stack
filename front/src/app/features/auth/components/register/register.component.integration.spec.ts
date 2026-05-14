import {Component, NgZone} from "@angular/core";
import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {RegisterComponent} from "./register.component";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {createMockRegisterForm} from "../../../../../testing/register-form.factory";
import {RouterTestingModule} from "@angular/router/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {AuthService} from "../../services/auth.service";
import { expect } from '@jest/globals';
import spyOn = jest.spyOn;

@Component({ template: '' })
class DummyComponent {}
describe('RegisterComponent integration suite', () => {
  let fixture: ComponentFixture<RegisterComponent>
  let component: RegisterComponent;
  let httpMock: HttpTestingController;
  let router: Router;
  let ngZone : NgZone;

  const mockForm = createMockRegisterForm();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterComponent ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'login', component: DummyComponent },
        ]),
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule
      ],
      providers: [
        AuthService,
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    ngZone = TestBed.inject(NgZone);
    fixture.detectChanges();
  })

  it('should create account and redirect to login on successful submit', fakeAsync(() => {
    component.form.setValue(mockForm);
    fixture.detectChanges();

    ngZone.run(() => {
      fixture.nativeElement.querySelector('button[type="submit"]').click();
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockForm);

    ngZone.run(() => {
      req.flush(null);
    });
    tick();
    fixture.detectChanges();

    expect(router.url).toBe('/login');
  }));

  it('should display error message on failed registration', fakeAsync(() => {
    spyOn(router, 'navigate');
    spyOn(router, 'navigateByUrl');

    component.form.setValue(mockForm);
    fixture.detectChanges();

    ngZone.run(() => {
      fixture.nativeElement.querySelector('button[type="submit"]').click();
    });
    const req = httpMock.expectOne('api/auth/register');

    ngZone.run(() => {
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
    tick();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('span.error')).toBeTruthy();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  }));
});
