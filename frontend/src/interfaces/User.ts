import { RoleType } from './../enums/RoleType';
import AccountCompact from './AccountCompact';

interface User {
  id?: number;
  bsn: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: string;
  accounts?: AccountCompact[];
  transactionLimit?: number;
  dayLimit?: number;
  blocked: boolean;
}

export default User;
