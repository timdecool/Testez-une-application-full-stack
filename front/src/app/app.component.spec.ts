import { HttpClientModule } from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import {SessionService} from "./services/session.service";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";


describe('AppComponent', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  const mockSessionService = {
    $isLogged: jest.fn().mockReturnValue(of(true)),
    logOut: jest.fn()
  }

  const initComponent = () => {
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  beforeEach(async () => {
    jest.clearAllMocks()
    mockSessionService.$isLogged.mockReturnValue(of(true));
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should display menu if logged session', () => {
    const spans = initComponent().querySelectorAll('span');
    expect(spans.length).toBe(4);
    expect(spans[1].textContent).toContain("Sessions");
    expect(spans[2].textContent).toContain("Account");
    expect(spans[3].textContent).toContain("Logout");
  })

  it('should display login and register links if no logged session', () => {
    mockSessionService.$isLogged.mockReturnValue(of(false));
    const spans = initComponent().querySelectorAll('span');
    expect(spans.length).toBe(3);
    expect(spans[1].textContent).toContain("Login");
    expect(spans[2].textContent).toContain("Register");
  })

  it('should logout and redirect when logout button is clicked', () => {
    const logoutButton = initComponent().querySelectorAll('span')[3];
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate')
      .mockImplementation(() => Promise.resolve(true));

    logoutButton.click();

    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  })
});
