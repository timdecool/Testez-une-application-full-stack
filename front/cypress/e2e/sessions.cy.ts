
describe('Sessions admin spec', () => {

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
    cy.login(true);
  });

  it('should access details and delete session as admin', () => {
    cy.intercept('DELETE', '/api/session/1', { statusCode: 200 }).as('deleteSession');

    cy.fixture('sessions').then((sessions) => {
      const sessionToBeDeleted = sessions[0];
      const sessionsWithoutDeleted = sessions.filter(s => s.id !==  1);
      cy.intercept('GET', '/api/session/1', { body: sessionToBeDeleted }).as('getSession');
      cy.intercept('GET', '/api/session', { body: sessionsWithoutDeleted }).as('sessionsWithoutDeleted');
    });

    cy.get('mat-card.item').first().find('button').contains('Detail').click();
    cy.url().should('include', 'sessions/detail/1');
    cy.wait('@getSession');

    cy.get('h1').should('be.visible');
    cy.get('button').contains('Delete')
      .should('be.visible')
      .click();

    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
    cy.wait('@sessionsWithoutDeleted');

    cy.get('mat-card.item').should('have.length', 1);
  });

  it('should create a session', () => {
    cy.intercept('GET', '/api/teacher', { fixture: 'teachers' }).as('teachers');
    cy.intercept('POST', '/api/session', { body: mockSession }).as('createSession');

    cy.get('button[routerLink="create"]').click();
    cy.url().should('include', '/sessions/create');
    cy.wait('@teachers');

    cy.intercept('GET', '/api/session', { body: [mockSession] }).as('sessions');

    cy.get('input[formControlName=name]').type('test');
    cy.get('input[formControlName=date]').type('2026-05-08');
    cy.get('mat-select').click();
    cy.get('mat-option').contains('Miranda Portique').click();
    cy.get('textarea[formControlName=description]').type('test');

    cy.get('button[type="submit"]').should('be.enabled').click();
    cy.wait('@createSession');

    cy.url().should('include', '/sessions');
    cy.wait('@sessions');
    cy.get('mat-card.item').should('have.length', 1);
    cy.get('mat-card.item').first().find('mat-card-title').should('contain', 'test');
  });
});
