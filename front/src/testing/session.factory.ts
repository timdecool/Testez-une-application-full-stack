import {Session} from "../app/features/sessions/interfaces/session.interface";

export const createMockSession = (
  overrides: Partial<Session> = {}
): Session => (
  {
    id: 1,
    name: "test session",
    description: "",
    date: new Date(),
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date(),
    ...overrides
  }
);
