import {AuthService} from "./auth.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed } from "@angular/core/testing";
import { expect } from '@jest/globals';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockRegister = {
    email: 'test@test.com',
    password: 'password123',
    firstName: 'Michel',
    lastName: 'Boulon'
  };

  const mockLogin = {
    email: 'test@test.com',
    password: 'password123'
  };

  const mockSessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 1,
    username: 'michelb',
    firstName: 'Michel',
    lastName: 'Boulon',
    admin: false
  };

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
