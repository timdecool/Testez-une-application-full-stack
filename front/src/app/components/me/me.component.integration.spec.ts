import {Component, NgZone} from "@angular/core";
import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {MeComponent} from "./me.component";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {createMockSessionInfo} from "../../../testing/session-information.factory";
import {createMockUser} from "../../../testing/user.factory";
import {RouterTestingModule} from "@angular/router/testing";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {SessionService} from "../../services/session.service";
import {UserService} from "../../services/user.service";
import { expect } from '@jest/globals';
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";

@Component({ template: ''})
class DummyComponent {}

describe('MeComponent integration tests', () => {
  let fixture: ComponentFixture<MeComponent>;
  let component: MeComponent;
  let httpMock: HttpTestingController;
  let router: Router;
  let sessionService: SessionService;
  let userService: UserService;

  const mockSessionInfo = createMockSessionInfo();
  const mockUser = createMockUser();
  const mockSnackBar = { open: jest.fn() };

  const initComponent = () => {
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  const flushInitRequest = () => {
    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
    tick();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MeComponent ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: '', component: DummyComponent },
        ]),
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule
      ],
      providers: [
        SessionService,
        UserService,
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    userService = TestBed.inject(UserService);
    sessionService.logIn(mockSessionInfo);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should load user info on init', fakeAsync(() => {
    initComponent();
    flushInitRequest();
    expect(component.user).toBe(mockUser);
  }));

  it('should delete user and redirect to home page when delete button is clicked', fakeAsync(() => {
    const ngZone = TestBed.inject(NgZone);
    const compiled = initComponent();
    flushInitRequest();

    const deleteButton = compiled.querySelector('button[mat-raised-button]') as HTMLButtonElement;
    deleteButton.click();
    fixture.detectChanges();

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');

    ngZone.run(() => {
      req.flush(null);
    });
    tick();
    fixture.detectChanges();

    expect(mockSnackBar.open).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(router.url).toBe('/');
  }));
});
