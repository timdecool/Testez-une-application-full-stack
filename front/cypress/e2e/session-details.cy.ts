
describe('Session details spec', () => {

  describe('As any role', () => {
    beforeEach(() => {
      cy.login();
    })

    it('should display session information', () => {

    });
  })

  describe('As a user', () => {

    beforeEach(() => {
      cy.login();
    });

    it('should add or remove participation to session', () => {

    });
  });

  describe('As an admin', () => {
    beforeEach(() => {
      cy.login(true);
    });

    it('should delete session and redirect to sessions', () => {


    });
  });

});
