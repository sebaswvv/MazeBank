import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';

// STORE
export const useUserStore = defineStore({
    id: 'user',
    state: (): User => ({
        id: 0,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        role: 0,
    }),
    getters: {
        getUser(state) {
            return state;
        },       
    },
    actions: {
        async getUser(id: number) {
            try {
                const response = await axios.get(`/users/${id}`);
                if (response.status === 200) {
                    this.setUser(response.data);
                }
            } catch (error: any) {
                console.error(error);
            }
        },
        setUser(user: User) {
            this.id = user.id;
            this.firstName = user.firstName;
            this.lastName = user.lastName;
            this.email = user.email;
            this.phoneNumber = user.phoneNumber;
            this.role = user.role;
        }
    }
});