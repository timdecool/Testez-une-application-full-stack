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

  const mockForm = {
    email: 'michel@gmail.com',
    password: 'password123',
  }
  const fillForm = (overrides = {}) => {
    component.form.setValue({ ...mockForm, ...overrides });
    fixture.detectChanges();
  }

  const initComponent = () => {
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  const getSubmitButton = (compiled: HTMLElement) =>
    compiled.querySelector('button[type="submit"]') as HTMLButtonElement;


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

  it('should not display error message on init', () => {
    expect(initComponent().querySelector('p.error')).toBeNull();
  })

  it('should disable submit button if email is invalid', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);

    fillForm({ email: 'not-an-email'});
    expect(submitButton.disabled).toBe(true);
  });

  it('should disable submit button if password is invalid', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);

    fillForm({ password: "pa"});
    expect(submitButton.disabled).toBe(true);
  });

  it('should enable submit button if form is valid', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);

    fillForm();
    expect(submitButton.disabled).toBe(false);
  });

  it('should display error message on failed submit', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);
    mockAuthService.login.mockReturnValueOnce(throwError(() => new Error('Unauthorized')))

    fillForm();
    submitButton.click();
    fixture.detectChanges();

    expect(compiled.querySelector('p.error')).toBeTruthy();
  });

  it('should login and navigate on successful submit', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate')
      .mockImplementation(() => Promise.resolve(true));

    fillForm();
    submitButton.click();
    fixture.detectChanges();

    expect(mockAuthService.login).toHaveBeenCalledWith(mockForm);
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
