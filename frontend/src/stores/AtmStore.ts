import { defineStore } from 'pinia';
import { AtmScenes } from './../enums/AtmScenes';
import axios from '../utils/axios';

export const useAtmStore = defineStore({
  id: 'atm',
  state: () => ({
    sceneState: AtmScenes.SELECT as AtmScenes | null,
    amount: null as number | null,
    accountId: null as number | null,
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
    async deposit(amount?: number | null) {
      const response = await axios.post(`/accounts/${this.accountId}/deposit`, {
        amount: amount,
      });

      // Check the response status and handle accordingly
      if (response.status !== 201) {
        throw new Error(response.data.message);
      } else {
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
        this.sceneState = AtmScenes.SELECT;
      }
    },
  },
});
