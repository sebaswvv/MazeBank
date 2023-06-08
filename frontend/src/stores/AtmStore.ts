import { defineStore } from 'pinia';
import { AtmScenes } from './../enums/AtmScenes';
import axios from '../utils/axios';
import Account from '../interfaces/Account';

export const useAtmStore = defineStore({
  id: 'atm',
  state: () => ({
    sceneState: AtmScenes.SELECT as AtmScenes | null,
    amount: null as number | null,
    accountId: null as number | null,
    account: null as Account | null,
  }),
  getters: {
    getSceneState(state) {
      return this.sceneState;
    },
    getAmount(state) {
      return this.amount;
    },
    getAccountId(state) {
      return this.accountId;
    },
  },
  actions: {
    setSceneState(scene: AtmScenes) {
      this.sceneState = scene;
    },
    setAmount(amount: number) {
      this.amount = amount;
    },
    setAccountId(accountId: number) {
      this.accountId = accountId;
    },
    async fetchAccount() {
      const response = await axios.get(`/accounts/${this.accountId}`);

      // Check the response status and handle accordingly
      if (response.status !== 200) {
        throw new Error(response.data.message);
      } else {
        this.account = response.data;
      }
    },
    async deposit(amount?: number | null) {
      const response = await axios.post(`/accounts/${this.accountId}/deposit`, {
        amount: amount,
      });

      // Check the response status and handle accordingly
      if (response.status !== 201) {
        throw new Error(response.data.message);
      } else {
        this.fetchAccount();
        this.sceneState = AtmScenes.SELECT;
      }
    },
    async withdraw() {
      const response = await axios.post(
        `/accounts/${this.accountId}/withdraw`,
        {
          amount: this.amount,
        }
      );

      // Check the response status and handle accordingly
      if (response.status !== 200) {
        throw new Error(response.data.message);
      } else {
        this.fetchAccount();
        this.sceneState = AtmScenes.SELECT;
      }
    },
  },
});
