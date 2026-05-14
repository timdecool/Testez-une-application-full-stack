import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {createMockTeacher} from "../../testing/teacher.factory";

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeachers = [
    createMockTeacher(),
    createMockTeacher({ id: 2, lastName: 'Boulon', firstName: 'Michel'})
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call GET api/teacher on all()', () => {
    service.all().subscribe(teachers => {
      expect(teachers).toEqual(mockTeachers);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers);
  });

  it('should call GET api/teacher/1 on detail()', () => {
    service.detail("1").subscribe(teacher => {
      expect(teacher).toEqual(mockTeachers[0]);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers[0]);
  });
});
