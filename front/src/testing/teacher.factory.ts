import {Teacher} from "../app/interfaces/teacher.interface";

export const createMockTeacher = (
  overrides: Partial<Teacher> = {}
): Teacher => (
  {
    id: 1,
    lastName: 'Portique',
    firstName: 'Miranda',
    createdAt: new Date(),
    updatedAt: new Date(),
    ...overrides
  }
);
