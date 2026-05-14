import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {createMockSession} from "../../../../testing/session.factory";

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession = createMockSession();
  const mockSessions = [mockSession, createMockSession({ id: 2, name: 'test2', teacher_id: 2 })];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET api/session on all()', () => {
    service.all().subscribe(session => {
      expect(session).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should call GET api/session/1 on detail()', () => {
    service.detail("1").subscribe(session => {
      expect(session).toEqual(mockSessions[0]);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions[0]);
  });

  it('should call DELETE api/session/1 on delete()', () => {
    service.delete("1").subscribe();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should call POST api/session on create()', () => {
    service.create(mockSession).subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockSession);

    req.flush(mockSession);
  });

  it('should call PUT api/session/1 on update()', () => {
    service.update("1", mockSession).subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockSession);

    req.flush(mockSession);
  });

  it('should call POST api/session/1/participate/1 on participate()', () => {
    service.participate("1", "1").subscribe();

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();

    req.flush(null);
  });

  it('should call DELETE api/session/1/participate/1 on unParticipate()', () => {
    service.unParticipate("1", "1").subscribe();

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });
});
