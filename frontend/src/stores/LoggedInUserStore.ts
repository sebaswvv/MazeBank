import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';
import AccountCompact from '../interfaces/User';

// STORE
export const useUserStore = defineStore({
  id: 'loggedInUser',
  state: (): any => ({
    id: localStorage.getItem('id') || null,
    firstName: localStorage.getItem('firstName') || null,
    lastName: localStorage.getItem('lastName') || null,
    email: localStorage.getItem('email') || null,
    phoneNumber: localStorage.getItem('phoneNumber') || null,
    role: localStorage.getItem('role') || null,
    accounts: localStorage.getItem('accounts') || null,
  }),
  getters: {
    getUser(state) {
      return state;
    },
    getIsEmployee(state) {
      return state.role === 'EMPLOYEE';
    },
  },
  actions: {
    async fetchUser(id: number) {
      try {
        const response = await axios.get(`/users/${id}`);
        if (response.status === 200) {
          // create user object with user data
          const user: User = {
            id: response.data.id,
            firstName: response.data.firstName,
            lastName: response.data.lastName,
            email: response.data.email,
            phoneNumber: response.data.phoneNumber,
            role: response.data.role,
          };
          this.setUser(user);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async getAccountsOfUser(id: number) {
      try {
        const response = await axios.get(`/users/${id}/accounts`);
        if (response.status === 200) {
          // for each account in response.data, create an AccountCompact object
          const accounts: AccountCompact[] = response.data.map(
            (account: any) => {
              return {
                id: account.id,
                iban: account.iban,
                accountType: account.accountType,
                balance: account.balance,
              };
            }
          );
          this.setAccounts(accounts);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    setAccounts(accounts: AccountCompact[]) {
      this.accounts = accounts;
      localStorage.setItem('accounts', JSON.stringify(accounts));
    },
    setUser(user: User) {
      this.id = user.id;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.email = user.email;
      this.phoneNumber = user.phoneNumber;
      this.role = user.role;
      localStorage.setItem('user', JSON.stringify(user));
    },
    logout() {
      localStorage.removeItem('user');
      localStorage.removeItem('accounts');
      this.id = null;
      this.firstName = null;
      this.lastName = null;
      this.email = null;
      this.phoneNumber = null;
      this.role = null;
      this.accounts = null;
    },
    getAccounts() {
      return this.accounts;
    },
  },
});
