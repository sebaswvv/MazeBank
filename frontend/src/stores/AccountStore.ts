import { defineStore } from 'pinia';
import Account from '../interfaces/Account';
import Transaction from '../interfaces/Transaction';
import axios from '../utils/axios';
import AccountPatchRequest from '../interfaces/requests/AccountPatchRequest';
import AccountSearch from '../interfaces/AccountSearch';

export const useAccountStore = defineStore({
  id: 'account',
  state: () => ({
    account: null as Account | null,
    transactions: [] as Transaction[],
  }),
  getters: {
    getIban(): string | undefined {
      return this.account?.iban;
    },
    getBalance(): number | undefined {
      return this.account?.balance;
    },
    getTransactions(): Transaction[] {
      return this.transactions;
    },
    ownerFullName(): string {
      return this.account?.user ? `${this.account.user.firstName} ${this.account.user.lastName}` : '';
    }
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
    async searchAccount(name: string): Promise<AccountSearch[]> {
      try {
        const query = encodeURIComponent(name);
        const response = await axios.get<AccountSearch[]>(`/accounts/search/${query}`);
        if (response.status === 200) {
          return response.data.filter(account => account.iban !== this.account?.iban);
        }
      } catch (error: any) {
        console.error(error);
      }
      return [];
    },
  },
});
