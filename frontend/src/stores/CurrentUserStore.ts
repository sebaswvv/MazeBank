import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';
import AccountCompact from '../interfaces/User';
import UserPatchRequest from '../interfaces/requests/UserPatchRequest';

// STORE
export const useCurrentUserStore = defineStore({
  id: 'currentUser',
  state: (): User => ({
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: 'CUSTOMER',
    accounts: [],
    transactionLimit: 0,
    dayLimit: 0,
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
        if (!id) return;

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
            accounts: response.data.accounts,
            transactionLimit: response.data.transactionLimit,
            dayLimit: response.data.dayLimit,
          };
          this.setUser(user);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async getAccountsOfUser(id: number) {
      try {
        if (!id) return;
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
    async editUser(user: User) {
      try {
        const userPatchRequest: UserPatchRequest = {
          email: user.email,
          firstName: user.firstName,
          lastName: user.lastName,
          phoneNumber: user.phoneNumber,
          dayLimit: user.dayLimit,
          transactionLimit: user.transactionLimit,
        };

        const response = await axios.patch(
          `/users/${user.id}`,
          userPatchRequest
        );
        if (response.status === 200) {
          this.setUser(user);
          return true;
        } else {
          console.log(response);
          return false;
        }
      } catch (error: any) {
        console.error(error);
        return false;
      }
    },
    setAccounts(accounts: any[]) {
      this.accounts = accounts;
    },
    setUser(user: User) {
      this.id = user.id;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.email = user.email;
      this.phoneNumber = user.phoneNumber;
      this.role = user.role;
      this.accounts = user.accounts;
      this.transactionLimit = user.transactionLimit;
    },
    logout() {
      this.id = 0;
      this.firstName = '';
      this.lastName = '';
      this.email = '';
      this.phoneNumber = '';
      this.role = 'CUSTOMER';
      this.accounts = [];
      this.transactionLimit = 0;
      this.dayLimit = 0;
    },
    getAccounts() {
      return this.accounts;
    },
  },
});
