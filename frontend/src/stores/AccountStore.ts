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
  getters: {},
  actions: {},
});
