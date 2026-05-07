import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {DetailComponent} from "./detail.component";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {ActivatedRoute, convertToParamMap, Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {Component, NgZone} from "@angular/core";
import {ReactiveFormsModule} from "@angular/forms";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {SessionService} from "../../../../services/session.service";
import {SessionApiService} from "../../services/session-api.service";
import {TeacherService} from "../../../../services/teacher.service";
import { expect } from '@jest/globals';
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {Session} from "../../interfaces/session.interface";
import {SessionInformation} from "../../../../interfaces/sessionInformation.interface";
import {Teacher} from "../../../../interfaces/teacher.interface";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@Component({ template: ''})
class DummyComponent {}

describe('Session Detail Flow - Integration Test Suite', () => {
  let fixture: ComponentFixture<DetailComponent>;
  let component: DetailComponent;
  let httpMock: HttpTestingController;
  let router: Router;
  let sessionService: SessionService;

  const mockSessionInfo: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 1,
    username: 'michelboulon',
    firstName: 'Michel',
    lastName: 'Boulon',
    admin: false
  };
  const mockSessionInfoAdmin: SessionInformation = { ...mockSessionInfo, admin: true };

  const mockSession: Session = {
    id: 1,
    name: "test session",
    description: "",
    date: new Date(),
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date()
  };
  const mockSessionParticipate: Session = { ...mockSession,  users: [1] };

  const mockTeacher: Teacher = {
    id: 1,
    lastName: 'Portique',
    firstName: 'Miranda',
    createdAt: new Date(),
    updatedAt: new Date()
  }

  const initComponent = () => {
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  const flushInitRequests = ({ participating = false } = {}) => {
    httpMock.expectOne('api/session/1').flush(participating ? mockSessionParticipate:mockSession);
    httpMock.expectOne('api/teacher/1').flush(mockTeacher);
    tick();
    fixture.detectChanges();
  }

  const mockSnackBar = { open: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailComponent ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: DummyComponent }
        ]),
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatIconModule,
        BrowserAnimationsModule
      ],
      providers: [
        SessionService,
        SessionApiService,
        TeacherService,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1'}) }}
        },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    sessionService.logIn(mockSessionInfo);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should fetch session and teacher and init component properties', fakeAsync(() => {
    initComponent();
    flushInitRequests();
    expect(component.sessionId).toBe('1');
    expect(component.isAdmin).toBe(false);
    expect(component.userId).toBe('1');
    expect(component.isParticipate).toBe(false);
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
  }));

  it('should fetch and update properties on participate', fakeAsync(() => {
    const compiled = initComponent();
    flushInitRequests();

    const participateButton = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    participateButton.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('POST');
    req.flush(null);

    httpMock.expectOne('api/session/1').flush(mockSessionParticipate);
    httpMock.expectOne('api/teacher/1').flush(mockTeacher);
    tick();
    fixture.detectChanges();

    expect(component.isParticipate).toBe(true);
  }));

  it('should fetch and update properties on unParticipate', fakeAsync(() => {
    const compiled = initComponent();
    flushInitRequests({ participating : true });
    expect(component.isParticipate).toBe(true);

    const unParticipateButton = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    unParticipateButton.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/session/1/participate/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    httpMock.expectOne('api/session/1').flush(mockSession);
    httpMock.expectOne('api/teacher/1').flush(mockTeacher);
    tick();
    fixture.detectChanges();

    expect(component.isParticipate).toBe(false);
  }));

  it('should delete session and navigate to /sessions when admin', fakeAsync(() => {
    const ngZone = TestBed.inject(NgZone);
    sessionService.logIn(mockSessionInfoAdmin);
    const compiled = initComponent();
    flushInitRequests();

    const deleteButton = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    deleteButton.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');

    ngZone.run(() => {
      req.flush(null);
    });
    tick();
    fixture.detectChanges();

    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(router.url).toBe('/sessions');
  }));
});
