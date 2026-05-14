describe('Register spec', () => {
  const fillForm = (overrides = {}) => {
    const credentials = {
      firstName: 'Michel',
      lastName: 'Boulon',
      email: 'michel.boulon@laposte.net',
      password: 'password123',
      ...overrides
    };
    cy.get('input[formControlName=firstName]').clear().type(credentials.firstName);
    cy.get('input[formControlName=lastName]').clear().type(credentials.lastName);
    cy.get('input[formControlName=email]').clear().type(credentials.email);
    cy.get('input[formControlName=password]').clear().type(credentials.password);
  }

  beforeEach(() => {
    cy.visit('/register');
  })

  it('should submit and redirect to login with valid credentials', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {
        message: 'User registered successfully!'
      }
    }).as('registerSuccess');

    fillForm();
    cy.get('button[type="submit"]').click();

    cy.wait('@registerSuccess');
    cy.url().should('include', '/login');
  });

  it('should display error message when register fails', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Error: Email is already taken!'
      }
    }).as('registerFailure');

    fillForm();
    cy.get('button[type="submit"]').click();

    cy.wait('@registerFailure');
    cy.url().should('include', '/register');
    cy.get('span.error').should('be.visible').contains('An error occurred');
  });

  it('should prevent submitting with invalid input', () => {
    fillForm({ firstName: 'M' });
    cy.get('button[type="submit"]').should('be.disabled');

    fillForm({ lastName: 'B'});
    cy.get('button[type="submit"]').should('be.disabled');

    fillForm({ email: 'wrong' });
    cy.get('button[type="submit"]').should('be.disabled');

    fillForm({ password: 'pw'});
    cy.get('button[type="submit"]').should('be.disabled');

    fillForm();
    cy.get('button[type="submit"]').should('be.enabled');
  });
});
