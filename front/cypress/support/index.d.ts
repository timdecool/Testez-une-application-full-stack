declare namespace Cypress {
  interface Chainable {
    login(isAdmin?: boolean): Chainable<void>
    getFixtureById<T>(fixtureName: string, id: number): Chainable<T>
  }
}
