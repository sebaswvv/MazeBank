import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';
import AccountCompact from '../interfaces/AccountCompact';
import UserPatchRequest from '../interfaces/requests/UserPatchRequest';
// import AccountCompact from '../interfaces/User';

// STORE
export const useUserStore = defineStore({
  id: 'user',
  state: () => ({
    user: null as User | null,
    // accounts: [] as AccountCompact[],
  }),
  getters: {
    getUser(state) {
      return state.user;
    },
    getFullName(state) {
      return `${state.user?.firstName} ${state.user?.lastName}`;
    },
    getUserId(state) {
      return state.user?.id;
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
            dayLimit: response.data.dayLimit,
            transactionLimit: response.data.transactionLimit,
            accounts: response.data.accounts,
            blocked: response.data.blocked,
          };
          this.setUser(user);
          console.log(user);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async fetchAccounts() {
      try {
        if (!this.user?.id) return;
        const response = await axios.get(`/users/${this.user.id}/accounts`);
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

          this.user.accounts = accounts;
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    async updateUser(user: User) {
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
    setUser(user: User) {
      this.user = user;
    },
  },
});
