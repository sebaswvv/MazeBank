import { defineStore } from 'pinia';
import Account from '../interfaces/Account';
import Transaction from '../interfaces/Transaction';
import axios from '../utils/axios';
import AccountPatchRequest from '../interfaces/requests/AccountPatchRequest';

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
    async fetchTransactions(accountId: number) {
      try {
        const response = await axios.get(
          `/accounts/${accountId}/transactions`
        );

        // for testing
        if (response.status === 200) {
          this.transactions = response.data;
          console.log(this.transactions);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async updateAccount(request: AccountPatchRequest) {
      try {
        const response = await axios.patch(`/accounts/${this.account?.id}`, request);
        if (response.status === 200) {
          this.account = response.data;
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async blockAccount() {
      try {
        const response = await axios.put(`/accounts/${this.account?.id}/disable`);
        if (response.status === 200) {
          this.fetchAccount(this.account?.id!);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async unblockAccount() {
      try {
        const response = await axios.put(`/accounts/${this.account?.id}/enable`);
        if (response.status === 200) {
          this.fetchAccount(this.account?.id!);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
  },
});
