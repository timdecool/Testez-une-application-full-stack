import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import {expect, it} from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import {ActivatedRoute, convertToParamMap, Router} from "@angular/router";
import {of} from "rxjs";
import {TeacherService} from "../../../../services/teacher.service";
import {createMockTeacher} from "../../../../../testing/teacher.factory";
import {createMockSession} from "../../../../../testing/session.factory";
import {createMockSessionInfo} from "../../../../../testing/session-information.factory";

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  const mockSessionService = {
    sessionInformation: createMockSessionInfo({ admin: true })
  }

  const mockSession = createMockSession();
  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession))
  }

  const mockTeachers = [
    createMockTeacher(),
    createMockTeacher({ id: 2, lastName: 'Boulon', firstName: 'Michel' })
  ];
  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of(mockTeachers))
  }

  const mockActivatedRoute = {
    snapshot: { paramMap: convertToParamMap({ id: '1'}) }
  }

  const mockForm = {
    name: 'Session Yoga',
    date: new Date().toISOString().split('T')[0],
    teacher_id: 1,
    description: 'valid description',
  }

  const mockSnackBar = { open: jest.fn() };

  const initComponent = () => {
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true))
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
  });


  it('should create', () => {
    initComponent();
    expect(component).toBeTruthy();
  });

  it('should redirect when user session is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    initComponent();
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should init empty form in create mode', () => {
    initComponent();
    expect(component.sessionForm?.value).toEqual({
      name: '', date: '', teacher_id: '', description: ''
    });
  });

  it('should display create title in create mode', () => {
    const compiled = initComponent();
    const title = compiled.querySelector('h1') as HTMLElement;
    expect(title.textContent).toEqual('Create session');
  })

  it('should init filled form in update mode', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    initComponent();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm?.value).toEqual({
      name: mockSession.name,
      date: mockSession.date.toISOString().split('T')[0],
      teacher_id: mockSession.teacher_id,
      description: mockSession.description,
    });
  });

  it('should display update title in update mode', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    const compiled = initComponent();
    const title = compiled.querySelector('h1') as HTMLElement;
    expect(title.textContent).toEqual('Update session');
  });

  const invalidCases: [string, Partial<typeof mockForm>, ][] = [
    ['empty name', { name: '' }],
    ['empty date', { date: '' }],
    ['empty teacher id', { teacher_id: null as any }],
    ['empty description ', { description: '' }],
    ['description too long', { description: 'too long '.repeat(500) }],
  ];

  it.each(invalidCases)('should disable submit when %s', (description, override) => {
    const compiled = initComponent();
    component.sessionForm?.setValue({...mockForm, ...override });
    fixture.detectChanges();
    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBe(true);
  });

  it('should create session and redirect when valid form is submitted in create mode', () => {
    const compiled = initComponent();

    component.sessionForm?.setValue(mockForm);
    fixture.detectChanges();
    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    submitButton.click();

    expect(mockSessionApiService.create).toHaveBeenCalledWith(mockForm);
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000});
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session and redirect when valid form is submitted in update mode', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    const compiled = initComponent();

    component.sessionForm?.setValue(mockForm);
    fixture.detectChanges();
    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    submitButton.click();

    expect(mockSessionApiService.update).toHaveBeenCalledWith('1', mockForm);
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000});
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
