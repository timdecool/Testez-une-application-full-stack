import {Session} from "../../src/app/features/sessions/interfaces/session.interface"
describe('Session details spec', () => {

  const accessDetail = (sessionId: number) => {
    cy.getFixtureById('sessions', sessionId).then((session: Session) => {
      cy.intercept('GET', `/api/session/${sessionId}`, { body: session }).as('session')
    })
    cy.getFixtureById('teachers', 1).then((teacher) => {
      cy.intercept('GET', '/api/teacher/1', { body: teacher }).as('teacher')
    })
    cy.get('mat-card.item').first().find('button').contains('Detail').click();
    cy.url().should('include', '/sessions/detail/1');
    cy.wait('@session');
    cy.wait('@teacher');
  }

  describe('As any role', () => {
    beforeEach(() => {
      cy.login();
    })

    it('should display session information', () => {
      accessDetail(1);

      cy.get('h1').should('contain', 'Test1');
      cy.get('div.description').should('contain', 'test1');
      cy.get('span.ml1').should('contain', 'Miranda PORTIQUE');
      cy.get('span.ml1').should('contain', '0 attendees');
      cy.get('span.ml1').should('contain', 'May 4, 2026');
      cy.get('div.created').should('contain', 'May 4, 2026');
      cy.get('div.updated').should('contain', 'May 8, 2026');
    });

    it('should redirect to session list when clicking back arrow', () => {
      accessDetail(1);
      cy.intercept('/api/session', { fixture: 'sessions' }).as('sessions');

      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
      cy.wait('@sessions');
    });
  });

  describe('As a user', () => {
    beforeEach(() => {
      cy.login();
    });

    it('should add or remove participation to session', () => {
      accessDetail(1);

      cy.intercept('POST', '/api/session/1/participate/1', { statusCode: 200 }).as('participate');
      cy.getFixtureById('sessions', 1).then((session: Session) => {
        const sessionUpdated = { ...session, users: [1]};
        cy.intercept('GET', '/api/session/1', { body: sessionUpdated }).as('participateSession');
      });
      cy.getFixtureById('teachers', 1).then((teacher) => {
        cy.intercept('GET', '/api/teacher/1', { body: teacher }).as('teacher');
      })

      cy.get('button[mat-raised-button]').should('contain', 'Participate').click();
      cy.wait('@participate');
      cy.wait('@participateSession');
      cy.wait('@teacher');

      cy.get('span.ml1').should('contain', '1 attendees');

      cy.intercept('DELETE', '/api/session/1/participate/1', { statusCode: 200 }).as('unParticipate');
      cy.getFixtureById('sessions', 1).then((session: Session) => {
        cy.intercept('GET', '/api/session/1', { body: session }).as('session');
      });
      cy.getFixtureById('teachers', 1).then((teacher) => {
        cy.intercept('GET', '/api/teacher/1', { body: teacher }).as('teacher');
      })

      cy.get('button[mat-raised-button]').should('contain', 'Do not participate').click();
      cy.wait('@unParticipate');
      cy.wait('@session');
      cy.wait('@teacher');

      cy.get('span.ml1').should('contain', '0 attendees');
      cy.get('button[mat-raised-button]').should('contain', 'Participate');
    });
  });

  describe('As an admin', () => {
    beforeEach(() => {
      cy.login(true);
    });

    it('should delete session and redirect to sessions', () => {
      accessDetail(1);

      cy.intercept('DELETE', '/api/session/1', { statusCode: 200 }).as('deleteSession');
      cy.fixture('sessions').then((sessions) => {
        const sessionsWithoutDeleted = sessions.filter(s => s.id !==  1);
        cy.intercept('GET', '/api/session', { body: sessionsWithoutDeleted }).as('sessionsWithoutDeleted');
      });

      cy.get('button[mat-raised-button]').should('contain', 'Delete').click();
      cy.wait('@deleteSession');
      cy.url().should('include', '/sessions');
      cy.wait('@sessionsWithoutDeleted');

      cy.get('mat-card.item').should('have.length', 1);
    });
  });
});
