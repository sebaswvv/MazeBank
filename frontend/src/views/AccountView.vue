<template>
    <div class="container py-5">
        <!-- Button voor overboeken -->
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-primary" @click="navigateToTransferPage">Overboeken</button>
            </div>
        </div>
        <!-- Show the current account -->
        <div class="row py-5 rounded">
            <div class="col d-flex justify-content-center">
                <AccountPreview :title="account?.accountType === AccountType.CURRENT ? 'Betaalrekening' : 'Spaarrekening'" />
            </div>
        </div>

        <!-- Show transactions of this account -->
        <div class="row rounded bg-light py-2 d-flex justify-content-center">
            <template v-if="transactions.length > 0">
                <TransactionComponent v-for="transaction in transactions" :transaction="transaction" />
            </template>
            <template v-else>
                <p>Deze rekening heeft nog geen transacties.</p>
            </template>
        </div>
        <nav v-if="transactions.length > 10" class="py-2">
            <ul class="pagination justify-content-center">
                <li class="page-item disabled">
                    <a class="page-link">Previous</a>
                </li>
                <li class="page-item"><a class="page-link" href="#">1</a></li>
                <li class="page-item"><a class="page-link" href="#">2</a></li>
                <li class="page-item"><a class="page-link" href="#">3</a></li>
                <li class="page-item">
                    <a class="page-link" href="#">Next</a>
                </li>
            </ul>
        </nav>
    </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import AccountPreview from '../components/AccountPreview.vue';
import TransactionComponent from '../components/TransactionComponent.vue';
import { useAccountStore } from '../stores/AccountStore';
import router from '../router';
import { storeToRefs } from 'pinia';
import { AccountType } from '../enums/AccountType';

const accountStore = useAccountStore();
const { account, transactions } = storeToRefs(accountStore);

function navigateToTransferPage() {
    router.push('/transfer');
}

onMounted(async () => {
    if (!account.value) {
        return router.push('/dashboard');
    }

    // fetch transactions
    await accountStore.fetchTransactions(account.value!.id);
});
</script>

<style>
.transactions-container {
    border-radius: 5px;
}
</style>