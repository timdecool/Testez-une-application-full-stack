
describe('Logout spec', () => {

  it('should logout and redirect to home page', () => {
    cy.login();
    cy.get('span').contains('Logout').click();
    cy.url().should('include', '/').should('not.include', '/sessions');
  });

  it('should redirect to login when visting protected pages while not logged', () => {
    cy.visit('/sessions');
    cy.url().should('not.include', '/sessions').should('include', '/login');

    cy.visit('/me');
    cy.url().should('not.include', '/me').should('include', '/login');

    cy.visit('/sessions/create');
    cy.url().should('not.include', '/sessions/create').should('include', '/login');
  });
})
