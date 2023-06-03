<template>
    <div class="container py-5">
        <div class="row">
            <div class="col d-flex justify-content-center align-items-center">
                <h1>Withdraw</h1>
            </div>
        </div>

        <!-- Select atm option -->
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-secondary" @click="handleChangeScene(AtmScenes.SELECT)">Terug</button>
            </div>
        </div>

        <div class="row bg-light d-flex justify-content-center py-5 mt-5 rounded">
            <div class="col-md-6">
                <div class="d-flex justify-content-center align-items-center m-4">
                    <h2 v-if="customAmount == null && atmStore.amount == null">
                        Kies een bedrag
                    </h2>
                    <h2 v-else>
                        Gekozen bedrag: €
                        <span v-if="customAmount != null">{{ customAmount }}</span>
                        <span v-else>{{ atmStore.amount }}</span>
                    </h2>
                </div>
                <div class="grid-container">
                    <div class="grid-item" v-for="presetAmount in presetAmounts" :key="presetAmount">
                        <button class="btn btn-secondary" @click="setWithdrawalAmount(presetAmount)">{{ presetAmount
                        }}</button>
                    </div>
                    <div class="grid-item">
                        <div class="form-group">
                            <label for="amount">Custom Amount:</label>
                            <input type="number" class="form-control" id="amount" @change="atmStore.setAmount"
                                v-model="customAmount" placeholder="Enter custom amount" />
                        </div>
                    </div>

                </div>
                <div class="row my-4 ">
                    <div class="col d-flex justify-content-center align-items-center">
                        <button class="btn btn-primary" @click="handleWithdraw()">Withdraw</button>
                    </div>
                </div>

                <!-- Error message -->
                <div v-if="errorMessage" class="alert alert-danger mt-3">
                    {{ errorMessage }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { AtmScenes } from '../../enums/AtmScenes';
import { useAtmStore } from './../../stores/AtmStore.ts';

const atmStore = useAtmStore();
const presetAmounts = [20, 200, 50, 500, 100];
const customAmount = ref<number | null>(null);
const errorMessage = ref<string | null>(null);

function handleChangeScene(scene: AtmScenes) {
    atmStore.setSceneState(scene);
}

function setWithdrawalAmount(amount: number) {
    customAmount.value = null;
    atmStore.setAmount(amount);
}

async function handleWithdraw() {
    try {
        // Reset error message
        errorMessage.value = null;

        if (customAmount.value) {
            atmStore.setAmount(customAmount.value);
        }

        // Call the withdraw method
        await atmStore.withdraw();

        // Reset custom amount field after successful withdrawal
        customAmount.value = null;
    } catch (error: any) {
        // Set error message if withdrawal fails
        errorMessage.value = error.response.data.message;
    }
}
</script>

<style scoped>
.grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-gap: 10px;
}

.grid-item {
    display: flex;
    justify-content: center;
}

.alert {
    margin-top: 1rem;
}
</style>
