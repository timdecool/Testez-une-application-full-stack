import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import {createMockSessionInfo} from "../../testing/session-information.factory";

describe('SessionService', () => {
  let service: SessionService;

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
    service.logIn(createMockSessionInfo());
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(createMockSessionInfo());
  });

  it('should clear session information and isLogged on logout', () => {
    service.logIn(createMockSessionInfo());
    service.logOut();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit false on $isLogged after logout', () => {
    service.logIn(createMockSessionInfo());

    let emittedValue: boolean | undefined;
    service.$isLogged().subscribe(v => emittedValue = v);
    service.logOut();
    expect(emittedValue).toBe(false);
  });
});
