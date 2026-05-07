import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';


import { MeComponent } from './me.component';
import {of} from "rxjs";
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockUser = {
    id: 1,
    firstName: 'Michel',
    lastName: 'Boulon',
    email: 'michel.boulon@laposte.net',
    admin: true,
    createdAt: new Date(),
    updatedAt: new Date(),
  }

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  }

  const buildUser = (overrides = {}) => ({ ...mockUser, ...overrides })

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete: jest.fn().mockReturnValue(of(null))
  }

  const mockRouter = { navigate: jest.fn() };
  const mockSnackBar = { open: jest.fn() };

  const initComponent = () => {
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  beforeEach(async () => {
    jest.clearAllMocks()
    mockUserService.getById.mockReturnValue(of(mockUser));
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not display card content when user is not loaded', () => {
    mockUserService.getById.mockReturnValueOnce(of(undefined));
    const userContent = initComponent().querySelector("mat-card-content div") as HTMLElement;
    expect(userContent).toBeNull();
  })

  it('should display user info', () => {
    const paragraphs = initComponent().querySelectorAll('p');
    expect(paragraphs[0].textContent).toContain('Michel BOULON');
    expect(paragraphs[1].textContent).toContain('michel.boulon@laposte.net');
  })

  it('should display admin message when user is admin', () => {
    expect(initComponent().querySelector('p.my2')?.textContent).toContain('You are admin');
  })

  it('should display delete button when user is not admin', () => {
    mockUserService.getById.mockReturnValue(of(buildUser({ admin: false })));
    expect(initComponent().querySelector('button[color="warn"]')).toBeTruthy();
  });

  it('should call window.history.back when back arrow is clicked', () => {
    const historySpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    const backButton = initComponent().querySelector('button[mat-icon-button]') as HTMLButtonElement;
    backButton.click();
    expect(historySpy).toHaveBeenCalledTimes(1);
  })

  it('should delete and redirect when delete button is clicked', () => {
    mockUserService.getById.mockReturnValue(of(buildUser({ admin: false })));
    const deleteButton = initComponent().querySelector('button[mat-raised-button]') as HTMLButtonElement;
    deleteButton.click();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  })
});
