import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import {of, throwError} from "rxjs";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const fillForm = (email = "email@email.com", password = "password123") => {
    component.form.setValue({ email, password });
    fixture.detectChanges();
  }

  const initComponent = () => {
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  const mockSessionInfo = {
    token: "token"
  }

  const mockAuthService = {
    login: jest.fn().mockReturnValue(of(mockSessionInfo))
  }

  const mockSessionService = {
    logIn: jest.fn()
  }

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: AuthService, useValue: mockAuthService },
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with empty and invalid form', () => {
    initComponent();
    expect(component.form.valid).toBe(false);
    expect(component.form.value).toEqual({ email: '', password: ''});
  });

  it('should disable submit button if email is invalid', () => {
    const compiled = initComponent();
    fillForm('not-an-email');

    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBe(true);
  });

  it('should disable submit button if password is invalid', () => {
    const compiled = initComponent();
    fillForm("email@email.com", "pa");

    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBe(true);
  });

  it('should enable submit button if form is valid', () => {
    const compiled = initComponent();
    fillForm();

    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBe(false);
  });

  it('should not display error message on init', () => {
    expect(initComponent().querySelector('p.error')).toBeNull();
  })

  it('should display error message on failed submit', () => {
    mockAuthService.login.mockReturnValueOnce(throwError(() => new Error('Unauthorized')))
    const compiled = initComponent();
    fillForm();

    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    submitButton.click();
    fixture.detectChanges();

    expect(compiled.querySelector('p.error')).toBeTruthy();
  });

  it('should login and navigate on successful submit', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate')
      .mockImplementation(() => Promise.resolve(true));
    const compiled = initComponent();
    fillForm();

    const submitButton = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    submitButton.click();
    fixture.detectChanges();

    expect(mockAuthService.login).toHaveBeenCalledWith({ email: "email@email.com", password: "password123" });
    expect(mockSessionService.logIn).toHaveBeenCalledWith(mockSessionInfo);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should toggle password visibility when eye button is clicked', () => {
    const compiled = initComponent();
    const passwordInput = compiled.querySelector('input[formControlName="password"]') as HTMLInputElement;
    const visibilityButton = compiled.querySelector('button[aria-label="Hide password"]') as HTMLButtonElement;

    expect(passwordInput.type).toBe('password');

    visibilityButton.click();
    fixture.detectChanges();
    expect(passwordInput.type).toBe('text');

    visibilityButton.click();
    fixture.detectChanges();
    expect(passwordInput.type).toBe('password');
  });
});
