import {User} from "../../src/app/interfaces/user.interface";

describe('Profile spec', () => {

  const mockUser: User = {
    id: 1,
    email: "yoga@studio.com",
    lastName: "Boulon",
    firstName: "Michel",
    admin: false,
    password: "password",
    createdAt: new Date("2026-05-08"),
    updatedAt: new Date("2026-05-10"),
  }

  const accessAccountPage = (admin: boolean = false) => {
    cy.intercept('GET', '/api/user/1', { ...mockUser, admin: admin}).as('account');
    cy.get("span.link").contains("Account").click();
    cy.wait('@account');
  }

  describe('As a user', () => {
    beforeEach(() => {
      cy.login();
    })

    it('should display user information', () => {
      accessAccountPage();
      cy.get('p').should('contain', 'Michel BOULON');
      cy.get('p').should('contain', 'yoga@studio.com');
      cy.get('p').should('contain', 'May 8, 2026');
      cy.get('p').should('contain', 'May 10, 2026');
      cy.get('button[mat-raised-button').should('contain', 'Delete');
      cy.get('p').contains('You are admin').should('not.exist');
    });

    it('should go back when clicking back arrow', () => {
      accessAccountPage();
      cy.intercept('GET', '/api/session', { fixture: 'sessions' }).as('sessions');
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
      cy.wait('@sessions');
    });

    it('should delete and redirect when clicking delete button', () => {
      accessAccountPage();
      cy.intercept('DELETE', '/api/user/1', { statusCode: 200 }).as('deleteUser');
      cy.get('button[mat-raised-button]').contains('Delete').click();
      cy.wait('@deleteUser');

      cy.url().should('include', '/');
      cy.url().should('not.include', '/me');
    });
  });

  describe('As an admin', () => {
    beforeEach(() => {
      cy.login(true);
    })

    it('should not display delete button', () => {
      accessAccountPage(true);
      cy.get('button').contains('Delete').should('not.exist');
      cy.get('p.my2').should('contain', 'You are admin');
    });
  });
})
