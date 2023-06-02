import { RoleType } from './../enums/RoleType';

interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: RoleType;
}

export default User;
