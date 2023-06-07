import User from './User';
import { TransactionType } from './../enums/TransactionType';

interface Transaction {
  id: number;
  description: string;
  amount: number;
  userPerforming: User;
  sender: string;
  receiver: string;
  transactionType: TransactionType;
  timestamp: string;
}

export default Transaction;
