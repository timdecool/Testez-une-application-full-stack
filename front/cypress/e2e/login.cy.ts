describe('Login spec', () => {

  beforeEach(() => {
    cy.visit('/login');
  })

  it('should login and redirect with valid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    }).as('loginSuccess');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.wait('@loginSuccess');
    cy.wait('@session');
    cy.url().should('include', '/sessions')
  });

  it('should display error message when login fails', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: { message: 'Unauthorized'} }).as('loginFailure');

    cy.get('input[formControlName=email]').type("wrong@studio.com");
    cy.get('input[formControlName=password]').type(`wrongpass`);
    cy.get('button[type="submit"]').click();

    cy.wait('@loginFailure');
    cy.url().should('include', '/login');
    cy.get('p.error').should('be.visible');
  });

  it('should prevent submitting with invalid input', () => {
    cy.get('input[formControlName=email]').type('wrong');
    cy.get('input[formControlName=password]').type('wrong');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName=email]').clear().type('wrong@studio.com');
    cy.get('input[formControlName=password]').clear().type('wr');
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName=password]').clear().type('correct');
    cy.get('button[type="submit"]').should('be.enabled');
  });
});
