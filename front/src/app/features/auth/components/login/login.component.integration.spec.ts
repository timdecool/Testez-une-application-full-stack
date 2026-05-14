import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {LoginComponent} from "./login.component";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {SessionService} from "../../../../services/session.service";
import { expect } from '@jest/globals';
import {Component, NgZone} from "@angular/core";
import {MatCardModule} from "@angular/material/card";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {createMockSessionInfo} from "../../../../../testing/session-information.factory";
import {createMockLoginForm} from "../../../../../testing/login-form.factory";

@Component({ template: ''})
class DummyComponent {}

describe('Auth Flow - Integration Test Suite', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let httpMock: HttpTestingController;
  let router: Router;

  const mockForm = createMockLoginForm();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: DummyComponent }
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
        SessionService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should login, update session state and navigate to /session on successful login', fakeAsync(() => {
    const ngZone = TestBed.inject(NgZone);

    component.form.setValue(mockForm);
    fixture.detectChanges();
    ngZone.run(() => {
      fixture.nativeElement.querySelector('button[type="submit"]').click();
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockForm);

    ngZone.run(() => {
      req.flush(createMockSessionInfo());
    });
    tick();
    fixture.detectChanges();

    const sessionService = TestBed.inject(SessionService);
    expect(sessionService.isLogged).toBe(true);
    expect(sessionService.sessionInformation).toEqual(createMockSessionInfo());

    expect(router.url).toBe('/sessions');
  }));

  it('should display error message and not navigate on failed login', fakeAsync(() => {
    const ngZone = TestBed.inject(NgZone);

    component.form.setValue(mockForm);
    fixture.detectChanges();
    ngZone.run(() => {
      fixture.nativeElement.querySelector('button[type="submit"]').click();
    });

    const req = httpMock.expectOne('api/auth/login');
    ngZone.run(() => {
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
    tick();
    fixture.detectChanges();

    const sessionService = TestBed.inject(SessionService);
    expect(sessionService.isLogged).toBe(false);
    expect(sessionService.sessionInformation).toBeUndefined();

    expect(fixture.nativeElement.querySelector('p.error')).toBeTruthy();
    expect(router.url).toBe('/');
  }));
});
