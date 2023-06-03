import { defineStore } from 'pinia';
// import Account from '../interfaces/Account';
// import Transaction from '../interfaces/Transaction';
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

      //
      if (response.status != 201) throw new Error(response.data.message);
      else this.sceneState = AtmScenes.SELECT;
    },
  },
});
