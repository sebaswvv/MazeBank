import User from './User';
import Account from './Account';
import { TransactionType } from './../enums/TransactionType';

interface Transaction {
  id: number;
  description: string;
  amount: number;
  userPerforming: User;
  sender: Account | null;
  receiver: Account | null;
  transactionType: TransactionType;
  timestamp: string;
}

export default Transaction;
