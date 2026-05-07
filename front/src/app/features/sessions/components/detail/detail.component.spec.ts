import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import {of} from "rxjs";
import {SessionApiService} from "../../services/session-api.service";
import {TeacherService} from "../../../../services/teacher.service";
import {MatCardModule, MatCardTitle} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {ActivatedRoute, convertToParamMap, Router} from "@angular/router";


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 }
  }

  const mockSession = {
    id: 1,
    name: 'test',
    description: '',
    date: new Date(),
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  }

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    delete: jest.fn().mockReturnValue(of(null)),
    participate: jest.fn().mockReturnValue(of(null)),
    unParticipate: jest.fn().mockReturnValue(of(null))
  }

  const mockTeacher = {
    id: 1,
    lastName: 'Portique',
    firstName: 'Miranda',
    createdAt: new Date(),
    updatedAt: new Date()
  }

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(mockTeacher)),
  }

  const mockActivatedRoute = {
    snapshot: { paramMap: convertToParamMap({ id: '1'}) }
  }

  const mockSnackBar = { open: jest.fn() };

  const initComponent = () => {
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  beforeEach(async () => {
    jest.clearAllMocks();
    mockSessionService.sessionInformation = { admin: true, id: 1 }
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
    })
      .compileComponents();
  });

  it('should create', () => {
    initComponent();
    expect(component).toBeTruthy();
  });

  it('should fetch session and teacher on init', () => {
    initComponent();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith('1');
  })

  it('should go back when back arrow is clicked', () => {
    const historySpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    const backButton = initComponent().querySelector('button[mat-icon-button]') as HTMLButtonElement;
    backButton.click();
    expect(historySpy).toHaveBeenCalledTimes(1);
  });

  it('should display delete button when user is admin', () => {
    const buttonText = initComponent().querySelector('button[mat-raised-button] span') as HTMLElement;
    expect(buttonText.innerHTML).toContain('Delete');
  });

  it('should display participate button when user is not admin and not participating', () => {
    mockSessionService.sessionInformation.admin = false;
    const buttonText = initComponent().querySelector('button[mat-raised-button] span') as HTMLElement;
    expect(buttonText.innerHTML).toContain('Participate');
  });

  it('should display unparticipate button when user is not admin and participating', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSessionApiService.detail.mockReturnValueOnce(of({ ...mockSession, users: [1] }));
    const buttonText = initComponent().querySelector('button[mat-raised-button] span') as HTMLElement;
    expect(buttonText.innerHTML).toContain('Do not participate');
  });

  it('should delete and redirect when delete button is clicked', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate')
      .mockImplementation(() => Promise.resolve(true));
    const button = initComponent().querySelector('button[mat-raised-button]') as HTMLButtonElement;
    button.click();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should add participation and update button when participate button is clicked', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSessionApiService.detail
      .mockReturnValueOnce(of({ ...mockSession, users: [2, 3] }))
      .mockReturnValueOnce(of({ ...mockSession, users: [1, 2, 3] }));

    const compiled = initComponent();
    const button = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    button.click();
    fixture.detectChanges();

    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
    const buttonText = compiled.querySelector('button[mat-raised-button] span') as HTMLElement;
    expect(buttonText.innerHTML).toContain('Do not participate');
  });

  it('should remove participation and update button when unparticipate button is clicked', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSessionApiService.detail
      .mockReturnValueOnce(of({ ...mockSession, users: [1, 2, 3] }))
      .mockReturnValueOnce(of({ ...mockSession, users: [2, 3] }));

    const compiled = initComponent();
    const button = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    button.click();
    fixture.detectChanges();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    const buttonText = compiled.querySelector('button[mat-raised-button] span') as HTMLElement;
    expect(buttonText.innerHTML).toContain('Participate');
  });
});

