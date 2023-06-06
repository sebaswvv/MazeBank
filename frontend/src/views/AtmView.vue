<template>
    <div class="container py-5 d-flex justify-content-center flex-column">
        <div class="row pb-3">
            <div class="col d-flex justify-content-center align-items-center">
                <h1>ATM</h1>
            </div>
        </div>
        <!-- Select atm option -->
        <div class="row">
            <div class="col d-flex justify-content-center align-items-center">
                <div v-if="atmStore.getSceneState == AtmScenes.SELECT" class="select d-flex justify-content-between">
                    <button class="btn-primary" @click="handleChangeScene(AtmScenes.DEPOSIT)">Storten</button>
                    <button class="btn-primary" @click="handleChangeScene(AtmScenes.WITHDRAW)">Opnemen</button>
                </div>
                <DepositComponent v-if="atmStore.getSceneState == AtmScenes.DEPOSIT" />
                <WithdrawComponent v-if="atmStore.getSceneState == AtmScenes.WITHDRAW" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { AtmScenes } from './../enums/AtmScenes';
import DepositComponent from './../components/atm/DepositComponent.vue';
import WithdrawComponent from './../components/atm/WithdrawComponent.vue';
import { useAtmStore } from './../stores/AtmStore.ts';
import { useCurrentUserStore } from '../stores/CurrentUserStore';
import { computed, onMounted } from 'vue';
const atmStore = useAtmStore();


onMounted(async () => {
    const atmStore = useAtmStore();
    const currentUserStore = useCurrentUserStore();

    // get the current account of the user
    const userId = localStorage.getItem('userId');
    await currentUserStore.fetchUser(Number(userId));
    await currentUserStore.fetchAccountsOfUser(Number(userId));
    atmStore.setAccountId(Number(localStorage.getItem('currentAccountId')));
});


// get the current account of the user
// const userId = localStorage.getItem('userId');
// await currentUserStore.fetchUser(Number(userId));
// await currentUserStore.fetchAccountsOfUser(Number(userId));
// atmStore.setAccountId(Number(localStorage.getItem('currentAccountId')));

function handleChangeScene(scene: AtmScenes) {
    atmStore.setSceneState(scene);
}

</script>

<style>
.select {
    width: 30%;
}
</style>