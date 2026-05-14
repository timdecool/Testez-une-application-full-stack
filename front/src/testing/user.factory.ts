import {User} from "../app/interfaces/user.interface";

export const createMockUser = (
  overrides: Partial<User> = {}
): User => (
  {
    id: 1,
    firstName: 'Michel',
    lastName: 'Boulon',
    email: 'michel.boulon@laposte.net',
    admin: false,
    createdAt: new Date(),
    updatedAt: new Date(),
    password: "password",
    ...overrides
  }
);
