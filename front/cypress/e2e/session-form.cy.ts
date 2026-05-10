import {Session} from "../../src/app/features/sessions/interfaces/session.interface"

describe("Session form spec", () => {
  beforeEach(() => {
    cy.login(true);
  })

  describe("Session creation", () => {
    const mockSession = {
      id: 3,
      name: 'test3',
      description: 'test3',
      date: '2026-05-08',
      teacher_id: 1,
      users: [],
      createdAt: '2026-05-08',
      updatedAt: '2026-05-08',
    };

    beforeEach(() => {
      cy.intercept('GET', '/api/teacher', { fixture: 'teachers' }).as('teachers');
      cy.get('button').contains('Create').click();
      cy.url().should('include', '/sessions/create');
      cy.wait('@teachers');
    });

    it('should load all teachers in select', () => {
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').should('have.length', 2);
      cy.get('mat-option').eq(0).should('contain', 'Miranda Portique');
      cy.get('mat-option').eq(1).should('contain', 'Michel Boulon');
    });

    it('should go back when clicking back arrow', () => {
      cy.intercept('GET', '/api/session', { fixture: 'sessions' }).as('sessions');
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
      cy.wait('@sessions');
    })

    it('should display create title and creation form', () => {
      cy.get('h1').should('contain', 'Create session');
      cy.get('input[formControlName=name]').should('be.visible').should('be.empty');
      cy.get('input[formControlName=date]').should('be.visible').should('be.empty');
      cy.get('mat-select[formControlName=teacher_id]').should('be.visible');
      cy.get('textarea[formControlName=description]').should('be.visible').should('be.empty');
      cy.get('button[type=submit]').should('be.disabled');
    });

    it('should create session and redirect with valid form', () => {
      cy.intercept('POST', '/api/session', { body: mockSession }).as('createSession');
      cy.fixture('sessions').then((sessions) => {
        sessions.push(mockSession);
        cy.intercept('GET', '/api/session', { body: sessions }).as('sessions');
      });

      cy.get('input[formControlName=name]').type('test3');
      cy.get('input[formControlName=date]').type('2026-05-08');
      cy.get('mat-select').click();
      cy.get('mat-option').contains('Miranda Portique').click();
      cy.get('textarea[formControlName=description]').type('test3');

      cy.get('button[type="submit"]').should('be.enabled').click();
      cy.wait('@createSession');

      cy.url().should('include', '/sessions');
      cy.wait('@sessions');
      cy.get('mat-card.item').should('have.length', 3)
        .contains('mat-card-title','test3').should('exist');
    });
  });

  describe("Session update", () => {
    const mockUpdatedSession = {
      id: 1,
      name: 'test1 updated',
      description: 'test1 updated',
      date: '2026-05-08',
      teacher_id: 1,
      users: [],
      createdAt: '2026-05-08',
      updatedAt: '2026-05-10',
    };

    beforeEach(() => {
      cy.intercept('GET', '/api/teacher', { fixture: 'teachers' }).as('teachers');
      cy.getFixtureById('sessions', 1).then((session: Session) => {
        cy.intercept('GET', '/api/session/1', { body: session}).as('session');
      })
      cy.get('mat-card.item').first().find('button').contains('Edit').click();
      cy.url().should('include', '/sessions/update/1');
      cy.wait('@teachers');
      cy.wait('@session');
    });

    it('should display update title and filled form', () => {
      cy.get('h1').should('contain', 'Update session');
      cy.get('input[formControlName=name]').should('be.visible').should('have.value', 'test1');
      cy.get('input[formControlName=date]').should('be.visible').should('have.value', '2026-05-04');
      // cy.get('mat-select[formControlName=teacher_id]').should('be.visible').should('have.value', '1');
      cy.get('textarea[formControlName=description]').should('be.visible').should('have.value', 'test1');
      cy.get('button[type=submit]').should('be.enabled');
    });

    it('should update session and redirect with valid form', () => {
      cy.intercept('PUT', '/api/session/1', { body: mockUpdatedSession }).as('updateSession');
      cy.fixture('sessions').then((sessions) => {
        const newSessions = [...sessions.filter((session: Session) => session.id !== 1), mockUpdatedSession];
        cy.intercept('GET', '/api/session', { body: newSessions }).as('sessions');
      });

      cy.get('input[formControlName=name]').clear().type(mockUpdatedSession.name);
      cy.get('input[formControlName=date]').clear().type(mockUpdatedSession.date);
      cy.get('mat-select').click();
      cy.get('mat-option').contains('Miranda Portique').click();
      cy.get('textarea[formControlName=description]').clear().type(mockUpdatedSession.description);
      cy.get('button[type="submit"]').should('be.enabled').click();
      cy.wait('@updateSession');

      cy.url().should('include', '/sessions');
      cy.wait('@sessions');
      cy.get('mat-card.item').should('have.length', 2)
        .contains('mat-card-title','test1 updated').should('exist');
    });
  });
});
