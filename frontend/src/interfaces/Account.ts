import type UserCompact from "./UserCompact";

interface Account {
  id: number;
  iban: string;
  accountType: number; // Assuming AccountType is an enum represented by numbers
  balance: number;
  userId: number;
  user?: UserCompact;
  active: boolean;
  createdAt: string; // Assuming createdAt is a string representing a date/time
  absoluteLimit: number;
}

export default Account;
