import { defineStore } from 'pinia';
import axios from '../utils/axios';
import User from '../interfaces/User';
import AccountCompact from '../interfaces/User';

// STORE
export const useUserStore = defineStore({
    id: 'user',
    state: (): any => ({
        id: 0,
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        role: 0,
        accounts: [],
    }),
    getters: {
        getUser(state) {
            return state;
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
                    const accounts: AccountCompact[] = response.data.map((account: any) => {
                        return {
                            id: account.id,
                            iban: account.iban,
                            accountType: account.accountType,
                            balance: account.balance,
                        };
                    });
                    this.setAccounts(accounts);
                }
            } catch (error: any) {
                console.error(error);
            }
        },
        setAccounts(accounts: AccountCompact[]) {
            this.accounts = accounts;
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