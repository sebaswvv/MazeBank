import { defineStore } from 'pinia';
import axios from '../utils/axios';
import Login from '../interfaces/Login';
import AuthState from '../interfaces/AuthState';

// STORE
export const useAuthenticationStore = defineStore({
  id: 'authentication',
  state: (): AuthState => ({
    userId: null,
    isLoggedIn: localStorage.getItem('token') !== null,
  }),
  getters: {
    // Deze implementeren met /users/:id ??
    // getUser(state) {
    //   return state.user;
    // },
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
          axios.updateAuthorizationHeader(response.data.jwt);
          this.router.push('/');
        }
      } catch (error: any) {
        return error;
      }
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      axios.updateAuthorizationHeader('');
      this.userId = null;
      this.isLoggedIn = false;

      this.router.push('/login');
    },
    // async register(newUser: User) {
    //   try {
    //     const response = await axios.post('/users/register', newUser);
    //     if (response.status === 200) {
    //       this.setUser(response.data.user, response.data.jwt);
    //       axios.updateAuthorizationHeader(response.data.jwt);
    //       // refresh page to update navbar
    //       window.location.reload();
    //     }
    //   } catch (error: any) {
    //     console.error(error);
    //   }
    // },
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

      // get userId from token
      const decodeJwt = (token: string) => {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const decodedData = atob(base64);
        const decodedToken = JSON.parse(decodedData);
        return decodedToken;
      };

      const decodedToken = decodeJwt(token);

      // set userId in local storage and store
      localStorage.setItem('userId', decodedToken.userId);
      this.userId = decodedToken.userId;
      this.isLoggedIn = true;
    },
    // async checkAuth() {
    //   const userId = localStorage.getItem('user_id');
    //   if (userId && this.user === null) {
    //     const response = await axios.get('/users/' + userId);
    //     // console.log(response.data);
    //     // this.setUser(response.data, localStorage.getItem('token') || '');
    //   }
    // },
  },
});
