
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
