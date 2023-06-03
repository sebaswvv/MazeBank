<template>
    <div class="container py-5">
        <div class="row">
            <div class="col d-flex justify-content-center align-items-center">
                <h1>Deposit</h1>
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
                <!-- Input field for amount -->
                <div class="form-group">
                    <label for="amount">Amount:</label>
                    <input type="number" class="form-control" id="amount" v-model="amount"
                        placeholder="Vul een gewenst bedrag in" />
                </div>

                <!-- Submit button -->
                <div class="d-flex justify-content-center">
                    <button class="btn-primary" @click="handleDeposit">Stort bedrag</button>
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
const amount = ref<number | null>(null);
const errorMessage = ref<string | null>(null);

function handleChangeScene(scene: AtmScenes) {
    atmStore.setSceneState(scene);
}

async function handleDeposit() {
    try {
        // Reset error message
        errorMessage.value = null;

        // Call the deposit method
        await atmStore.deposit(amount.value);

        // Reset amount field after successful deposit
        amount.value = null;
    } catch (error: any) {
        // Set error message if deposit fails
        errorMessage.value = error.response.data.message;
    }
}
</script>

<style></style>
