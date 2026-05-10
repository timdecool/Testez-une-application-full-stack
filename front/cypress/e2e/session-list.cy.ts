describe('Session list spec', () => {
  describe('As any role', () => {
    beforeEach(() => {
      cy.login();
    });

    it('should display session list', () => {
      cy.get('mat-card.item').should('have.length', 2);
    });

    it('should display detail button and allow access', () => {
      cy.getFixtureById('sessions', 1).then((session) => {
        cy.intercept('GET', '/api/session/1', { body: session }).as('session');
      })
      cy.getFixtureById('teachers', 1).then((teacher) => {
        cy.intercept('GET', '/api/teacher/1', { body: teacher }).as('teachers');
      })
      cy.get('mat-card.item').first().find('button').contains('Detail').click();
      cy.url().should('include', 'sessions/detail/1');
    });

  });

  describe('As a user', () => {
    beforeEach(() => {
      cy.login();
    });

    it('should not display create and edit buttons', () => {
      cy.get('button').contains('Create').should('not.exist');
      cy.get('button').contains('Edit').should('not.exist');
    });
  });

  describe('As an admin', () => {
    beforeEach(() => {
      cy.login(true);
    });

    it('should display create and edit buttons', () => {
      cy.get('button').contains('Create').should('be.visible');
      cy.get('mat-card.item').each((card) => {
        cy.wrap(card).find('button').contains('Edit').should('be.visible');
      });
    });

    it('should allow access to create screen', () => {
      cy.intercept('GET', '/api/teacher', { fixture: 'teachers' }).as('teachers');
      cy.get('button').contains('Create').click();
      cy.url().should('include', '/sessions/create');
    })

    it('should allow access to edit screen', () => {
      cy.fixture('sessions').then((sessions) => {
        cy.intercept('GET', '/api/session/1', { body: sessions[0] }).as('session');
      })
      cy.intercept('GET', '/api/teacher', { fixture: 'teachers' }).as('teachers');

      cy.get('mat-card.item').first().find('button').contains('Edit').click();
      cy.url().should('include', '/sessions/update/1');
    });
  });
});
