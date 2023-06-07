import { defineStore } from 'pinia';
import axios from '../utils/axios';
import TransactionRequest from '../interfaces/requests/TransactionRequest';
import Transaction from '../interfaces/Transaction';

export const useTransactionStore = defineStore({
  id: 'transaction',
  state: () => ({
    accountId: null as number | null,
  }),
  getters: {},
  actions: {
    async createTransaction(body: TransactionRequest): Promise<Transaction | void> {
        try {
            const response = await axios.post<Transaction>(`/transactions`, {
                amount: body.amount,
                description: body.description,
                senderIban: body.senderIban,
                receiverIban: body.receiverIban,
            } satisfies TransactionRequest);

            if (response.status === 201) {
                return response.data;
            }
        } catch (error) {
            throw error;
        }
    },
    async fetchTransactionsFromAccount(id: string, pageNumber: number, pageSize: number, sort = "desc"): Promise<Transaction[]> {
      try {
        const response = await axios.get<Transaction[]>(`/accounts/${id}/transactions?pageNumber=${pageNumber}&pageSize=${pageSize}&sort=${sort}`);
        if (response.status === 200) {
          return response.data;
        }
      } catch (error: any) {
        console.error(error);
      }
      return [];
    },
    async fetchTransactionsFromUser(id: string, pageNumber: number, pageSize: number, sort = "desc"): Promise<Transaction[]> {
      try {
        const response = await axios.get<Transaction[]>(`/users/${id}/transactions?pageNumber=${pageNumber}&pageSize=${pageSize}&sort=${sort}`);
        if (response.status === 200) {
          return response.data;
        }
      } catch (error: any) {
        console.error(error);
      }
      return [];
    },
  },
});
