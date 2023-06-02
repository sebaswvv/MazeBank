import { RoleType } from './../enums/RoleType';

interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  password?: string;
  profile_picture?: string;
  role: RoleType;
  user_type: number;
}

export default User;
