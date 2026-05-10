declare namespace Cypress {
  interface Chainable {
    login(isAdmin?: boolean): Chainable<void>
    getFixtureById(fixtureName: string, id: number): Chainable<void>
  }
}
