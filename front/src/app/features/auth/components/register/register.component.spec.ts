import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect, it } from '@jest/globals';

import { RegisterComponent } from './register.component';
import {of, throwError} from "rxjs";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const mockAuthService = {
    register: jest.fn().mockReturnValue(of(null))
  }

  const mockForm = {
    firstName: 'Michel',
    lastName: 'Boulon',
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

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with empty and invalid form', () => {
    initComponent();
    expect(component.form.valid).toBe(false);
    expect(component.form.value).toEqual({ firstName: '', lastName: '', email: '', password: ''});
  });

  const invalidCases: [string, Partial<typeof mockForm>, ][] = [
    ['invalid email', { email: 'not-an-email' }],
    ['empty email', { email: '' }],
    ['empty password', { password: '' }],
    ['password too short', { password: 'pa' }],
    ['password too long', { password: 'p'.repeat(41) }],
    ['empty first name', { firstName: '' }],
    ['first name too short', { firstName: 'mi' }],
    ['first name too long', { firstName: 'f'.repeat(21) }],
    ['empty last name', { lastName: '' }],
    ['last name too short', { lastName: 'bo' }],
    ['last name too long', { lastName: 'l'.repeat(21) }],
  ]

  it.each(invalidCases)('should disable submit when %s', (description, override) => {
    const compiled = initComponent();
    fillForm(override);
    expect(getSubmitButton(compiled).disabled).toBe(true);
  });

  it('should enable submit button if form is valid', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);

    fillForm();
    expect(submitButton.disabled).toBe(false);
  });

  it('should not display error message on init', () => {
    expect(initComponent().querySelector('span.error')).toBeNull();
  })

  it('should display error message on failed submit', () => {
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);
    mockAuthService.register.mockReturnValueOnce(throwError(() => new Error('Unauthorized')))

    fillForm();
    submitButton.click();
    fixture.detectChanges();

    expect(compiled.querySelector('span.error')).toBeTruthy();
  })

  it('should register and navigate on successful submit', () => {
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate')
      .mockImplementation(() => Promise.resolve(true));
    const compiled = initComponent();
    const submitButton = getSubmitButton(compiled);

    fillForm();
    submitButton.click();
    fixture.detectChanges();

    expect(mockAuthService.register).toHaveBeenCalledWith(mockForm);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  })
});
