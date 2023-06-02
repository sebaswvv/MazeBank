interface Account {
  id: number;
  iban: string;
  accountType: number; // Assuming AccountType is an enum represented by numbers
  balance: number;
  userId: number;
  isActive: boolean;
  createdAt: string; // Assuming createdAt is a string representing a date/time
  absoluteLimit: number;
}

export default Account;
