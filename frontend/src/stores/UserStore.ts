import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';
import AccountCompact from '../interfaces/User';

// STORE
export const useUserStore = defineStore({
  id: 'user',
  state: () => ({
    user: null as User | null,
    // accounts: [] as AccountCompact[],
  }),
  getters: {
    getUser(state) {
      return state;
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
          };
          this.setUser(user);
          console.log(user);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    setUser(user: User) {
      this.user = user;
    },
  },
});
