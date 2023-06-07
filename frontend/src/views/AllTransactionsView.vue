<template>
    <div class="container">
        <div class="row d-flex justify-content-center align-items-center">
            <div class="col-md-8 justify-content-center align-items-center">
                <div class="col d-flex justify-content-center align-items-center mt-5">
                    <h3>Zoek op IBAN of omschrijving</h3>
                </div>
                <div class="col">
                    <input type="text" class="form-control" placeholder="Zoek op IBAN of omschrijving" v-model="searchQuery" @input="performSearch" />
                    <button v-if="sort === 'asc'" class="btn btn-secondary btn-sm" @click="performSort('desc')">Sorteer aflopend</button>
                    <button v-else class="btn btn-secondary btn-sm" @click="performSort('asc')">Sorteer oplopend</button>
                </div>
            </div>
        </div>
        <div class="row d-flex justify-content-center align-items-center mt-5">
            <div class="col-md-12 d-flex justify-content-center align-items-center">
                <div class="all-accounts bg-light">
                    <template v-if="filteredTransactions.length > 0">
                        <span v-for="transaction in filteredTransactions" :key="transaction.id" to="/account"
                            class="single-account-link">
                            <SingleTransactionPreview :transaction="transaction" />
                        </span>
                    </template>
                    <template v-else>
                        <p>Geen transacties gevonden met deze zoekopdracht.</p>
                    </template>
                </div>
            </div>
            <div class="row d-flex justify-content-center align-items-center mt-3">
                <div class="col-md-8">
                    <button class="btn-secondary" @click="previousPage" v-if="pageNumber !== 0">Previous</button>
                    <button class="btn-secondary" @click="nextPage">Next</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import router from '../router';
import SingleTransactionPreview from '../components/SingleTransactionPreview.vue';
import { useTransactionStore } from '../stores/TransactionStore';
import Transaction from '../interfaces/Transaction';

const accountId = router.currentRoute.value.query.account as string;
const userId = router.currentRoute.value.query.user as string;

const transactions = ref<Transaction[]>([]);
const filteredTransactions = ref<Transaction[]>([]);
const searchQuery = ref('');
const pageNumber = ref(0);
const pageSize = 10;
const sort = ref('desc');

const transactionStore = useTransactionStore();

const performSearch = ({ target }: Event) => {
    const lowerCaseQuery = (target as HTMLInputElement).value.toLowerCase();
    if (!lowerCaseQuery) {
        filteredTransactions.value = transactions.value;
        return;
    }
    filteredTransactions.value = transactions.value.filter((t) =>
        t.sender.toLowerCase().includes(lowerCaseQuery) ||
        t.receiver.toLowerCase().includes(lowerCaseQuery) ||
        t.description.toLowerCase().includes(lowerCaseQuery)
    );
};

const performSort = (direction: 'asc' | 'desc') => {
    if (direction === 'asc') {
        filteredTransactions.value = filteredTransactions.value.sort((a, b) => a.amount - b.amount);
        sort.value = 'asc';
    } else {
        filteredTransactions.value = filteredTransactions.value.sort((a, b) => b.amount - a.amount);
        sort.value = 'desc';
    }
}

const fetchTransactions = () => {
    if (accountId)
        return transactionStore.fetchTransactionsFromAccount(accountId, pageNumber.value, pageSize, sort.value);
    else if (userId)
        return transactionStore.fetchTransactionsFromUser(userId, pageNumber.value, pageSize, sort.value);
    // else
    //     return accountStore.fetchTransactions(pageNumber.value, pageSize, sort.value);
}

const previousPage = async () => {
    if (pageNumber.value > 0) {
        pageNumber.value--;
        transactions.value = await fetchTransactions()!;
    }
};

const nextPage = async () => {
    pageNumber.value++;
    transactions.value = await fetchTransactions()!;
};

onMounted(async () => {
    transactions.value = await fetchTransactions()!;
    filteredTransactions.value = transactions.value;
});
</script>

<style scoped>
.all-accounts {
    width: 100%;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    padding: 10px;
}

.single-account-link {
    text-decoration: none;
    color: black;
}
</style>
