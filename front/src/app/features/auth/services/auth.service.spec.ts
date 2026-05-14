import {AuthService} from "./auth.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed } from "@angular/core/testing";
import { expect } from '@jest/globals';
import {createMockSessionInfo} from "../../../../testing/session-information.factory";
import {createMockRegisterForm} from "../../../../testing/register-form.factory";
import {createMockLoginForm} from "../../../../testing/login-form.factory";

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockRegister = createMockRegisterForm();
  const mockLogin = createMockLoginForm();
  const mockSessionInformation = createMockSessionInfo();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call POST api/auth/register on register()', () => {
    service.register(mockRegister).subscribe();

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRegister);

    req.flush(null);
  });

  it('should call POST api/auth/login on login()', () => {
    service.login(mockLogin).subscribe(sessionInformation => {
      expect(sessionInformation).toEqual(mockSessionInformation);
    })

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLogin);

    req.flush(mockSessionInformation);
  });
});
