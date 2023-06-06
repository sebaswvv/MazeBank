import { defineStore } from 'pinia';
import axios from '../utils/axios';
import Login from '../interfaces/requests/Login';
import Register from '../interfaces/requests/Register';
import AuthState from '../interfaces/AuthState';
import { useCurrentUserStore } from './CurrentUserStore';

// STORE
export const useAuthenticationStore = defineStore({
  id: 'authentication',
  state: (): AuthState => ({
    userId: localStorage.getItem('userId') ?? null,
    isLoggedIn: localStorage.getItem('token') !== null,
  }),
  getters: {
    // Deze implementeren met /users/:id ??
    // getUser(state) {
    //   return state.user;
    // },
    getUserId(state) {
      return state.userId;
    },
    getIsLoggedIn(state) {
      return state.isLoggedIn;
    },
  },
  actions: {
    async login(data: Login) {
      try {
        const response = await axios.post('/auth/login', {
          email: data.email,
          password: data.password,
        });
        if (response.status === 200) {
          this.setUser(response.data.authenticationToken);
          axios.updateAuthorizationHeader(response.data.authenticationToken);
        }
      } catch (error: any) {
        return error;
      }
    },
    logout() {
      localStorage.clear();

      axios.updateAuthorizationHeader('');
      this.userId = null;
      this.isLoggedIn = false;
      useCurrentUserStore().logout();
      this.router.push('/');
    },
    async register(registerRequest: Register) {
      try {
        const response = await axios.post('/auth/register', registerRequest);
        if (response.status === 201) {
          this.setUser(response.data.authenticationToken);
          axios.updateAuthorizationHeader(response.data.authenticationToken);
        }
      } catch (error: any) {
        console.error(error);
      }
    },
    // async updateUser(updateUser: User) {
    //   this.user = updateUser;
    //   try {
    //     const response = await axios.put(`/users/${updateUser.id}`, updateUser);
    //     this.user.password = undefined;
    //     console.log(response);
    //     if (response.status === 200) return true;
    //   } catch (error: any) {
    //     console.error(error);
    //     return false;
    //   }
    // },
    setUser(token: string) {
      // set token in local storage
      localStorage.setItem('token', token);
      const decodedUserId = this.decodeJwtReturnsUserId(token);

      // set userId in local storage and store
      localStorage.setItem('userId', decodedUserId);
      this.userId = decodedUserId;
      this.isLoggedIn = true;
    },
    decodeJwtReturnsUserId(token: string) {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const decodedData = atob(base64);
      const decodedToken = JSON.parse(decodedData);
      return decodedToken.userId;
    },
  },
});
