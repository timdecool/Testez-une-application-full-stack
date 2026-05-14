
export const createMockLoginForm = (overrides= {}) => (
  {
    email: 'test@test.com',
    password: 'password123',
    ...overrides
  }
);
