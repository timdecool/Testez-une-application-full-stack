import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import {SessionApiService} from "../../services/session-api.service";
import {of} from "rxjs";
import {RouterTestingModule} from "@angular/router/testing";

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  const mockSessions = [
    {
      id: 1,
      name: 'test',
      description: '',
      date: new Date(),
      teacher_id: 1,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      name: 'test2',
      description: '',
      date: new Date(),
      teacher_id: 2,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  const mockSessionApiService = {
    all: jest.fn().mockReturnValue(of(mockSessions))
  }

  const initComponent = () => {
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [RouterTestingModule, HttpClientModule, MatCardModule, MatIconModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    })
      .compileComponents();
    mockSessionService.sessionInformation.admin = true;
  });

  it('should create', () => {
    initComponent();
    expect(component).toBeTruthy();
  });

  it('should display sessions list', () => {
    const compiled = initComponent();
    const cards = compiled.querySelectorAll('mat-card.item');
    expect(cards).toHaveLength(2);
  });

  it('should display create and edit buttons when user is admin', () => {
    const compiled = initComponent();
    const buttons = Array.from(compiled.querySelectorAll('button span'));
    expect(buttons.some(b => b.textContent?.includes('Create'))).toBe(true);
    expect(buttons.some(b => b.textContent?.includes('Edit'))).toBe(true);

  });

  it('should not display create and edit buttons when user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    const compiled = initComponent();
    const buttons = Array.from(compiled.querySelectorAll('button span'));
    expect(buttons.some(b => b.textContent?.includes('Create'))).toBe(false);
    expect(buttons.some(b => b.textContent?.includes('Edit'))).toBe(false);
  });

  it('should return session information from session service', () => {
    initComponent();
    expect(component.user).toEqual(mockSessionService.sessionInformation);
  });

});
