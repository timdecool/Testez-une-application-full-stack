
export const createMockRegisterForm = (
  overrides = {}
) => ({
  firstName: 'Michel',
  lastName: 'Boulon',
  email: 'michel@gmail.com',
  password: 'password123',
  ...overrides
});
