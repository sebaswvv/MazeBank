<template>
    <div class="container py-5">
        <!-- Button voor overboeken -->
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-primary">Overboeken</button>
            </div>
        </div>
        <!-- Show the current account -->
        <div class="row py-5 rounded">
            <div class="col d-flex justify-content-center">
                <AccountPreview title="Current account" />
            </div>
        </div>

        <!-- Show transactions of this account -->
        <div class="row rounded bg-light py-2 d-flex justify-content-center">
            <template v-if="accountStore.getTransactions.length > 0">
                <TransactionComponent v-for="transaction in accountStore.getTransactions" :transaction="transaction" />
            </template>
            <template v-else>
                <p>Deze rekening heeft nog geen transacties.</p>
            </template>
        </div>
    </div>
</template>

<script setup lang="ts">
import AccountPreview from '../components/AccountPreview.vue';
import TransactionComponent from '../components/TransactionComponent.vue';
import { useAccountStore } from '../stores/AccountStore';

const accountStore = useAccountStore();

accountStore.fetchTransactions();


// fetch transactions

</script>

<style>
.transactions-container {
    border-radius: 5px;

}
</style>