import { defineStore } from 'pinia';
import Account from '../interfaces/Account';
import Transaction from '../interfaces/Transaction';
import axios from '../utils/axios';

export const useAccountStore = defineStore({
  id: 'account',
  state: () => ({
    account: null as Account | null,
    transactions: [] as Transaction[],
  }),
  getters: {
    getIban(state) {
      return this.account?.iban;
    },
    getBalance(state) {
      return this.account?.balance;
    },
    getTransactions(state) {
      return this.transactions;
    },
  },
  actions: {
    async fetchAccount(id: number) {
      try {
        const response = await axios.get(`/accounts/${id}`);
        if (response.status === 200) {
          this.account = response.data;
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async fetchTransactions() {
      try {
        // const response = await axios.get(
        //   `/transactions/account/${this.account?.id}`
        // );

        // for testing
        const response = await axios.get(`/accounts/2/transactions`);
        if (response.status === 200) {
          this.transactions = response.data;
          console.log(this.transactions);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
  },
});
