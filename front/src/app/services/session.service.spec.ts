import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import {SessionInformation} from "../interfaces/sessionInformation.interface";

describe('SessionService', () => {
  let service: SessionService;

  const mockSessionInformation: SessionInformation = {
    token: "token",
    type: "Bearer",
    id: 1,
    username: "michelboulon",
    firstName: "Michel",
    lastName: "Boulon",
    admin: true
  }

  beforeEach(() => {
    service = new SessionService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with isLogged false and no session information', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should set session information and isLogged on login', () => {
    service.logIn(mockSessionInformation);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockSessionInformation);
  });

  it('should clear session information and isLogged on logout', () => {
    service.logIn(mockSessionInformation);
    service.logOut();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit false on $isLogged after logout', () => {
    service.logIn(mockSessionInformation);

    let emittedValue: boolean | undefined;
    service.$isLogged().subscribe(v => emittedValue = v);
    service.logOut();
    expect(emittedValue).toBe(false);
  });
});
