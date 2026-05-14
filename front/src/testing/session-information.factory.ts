import {SessionInformation} from "../app/interfaces/sessionInformation.interface";

export const createMockSessionInfo = (
  overrides: Partial<SessionInformation> = {}
): SessionInformation => ({
  token: 'token',
  type: 'Bearer',
  id: 1,
  username: 'michelb',
  firstName: 'Michel',
  lastName: 'Boulon',
  admin: false,
  ...overrides
});
