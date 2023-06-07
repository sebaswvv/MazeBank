<template>
    <div class="container">
        <div class="row d-flex justify-content-center align-items-center">
            <div class="col-md-12 justify-content-center align-items-center">
                <div class="col d-flex justify-content-center align-items-center mt-5">
                    <h3>Zoek op IBAN of omschrijving</h3>
                </div>
                <div class="row mt-5">
                    <div class="col d-flex flex-column">
                        <p>Begindatum</p><input type="date" name="" id="" v-model="startDate">
                    </div>
                    <div class="col d-flex flex-column">
                        <p>Einddatum</p><input type="date" name="" id="" v-model="endDate">
                    </div>
                    <div class="col d-flex flex-column ">
                        <p>Van prijs</p>
                        <div class="input-group">
                            <span class="input-group-text" id="amount">€</span>
                            <input type="number" class="form-control" placeholder="0.00" inputmode="decimal" step="0.01"
                                min="0.01" v-model="minAmount">
                        </div>
                    </div>
                    <div class="col d-flex flex-column">
                        <p>Tot prijs</p>
                        <div class="input-group">
                            <span class="input-group-text" id="amount">€</span>
                            <input type="number" class="form-control" placeholder="0.00" inputmode="decimal" step="0.01"
                                min="0.01" v-model="maxAmount">
                        </div>
                    </div>
                    <div class="col d-flex flex-column">
                        <p>precieze prijs</p>
                        <div class="input-group">
                            <span class="input-group-text" id="amount">€</span>
                            <input type="number" class="form-control" placeholder="0.00" inputmode="decimal" step="0.01"
                                min="0.01" v-model="amount">
                        </div>
                    </div>
                    <div class="col d-flex flex-column">
                        <p>van IBAN</p><input type="text" name="" id="" v-model="fromIban">
                    </div>
                    <div class="col d-flex flex-column">
                        <p>Naar IBAN</p><input type="text" name="" id="" v-model="toIban">
                    </div>
                </div>
                <div class="col">
                    <button v-if="sort === 'asc'" class="btn btn-secondary btn-sm" @click="performSort('desc')">Sorteer
                        aflopend</button>
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
                <button class="btn-primary" @click="handleSearchTransactions">Zoeken</button>
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
import axios from './../utils/axios';

const userId = localStorage.getItem('userId')!;

const transactions = ref<Transaction[]>([]);
const filteredTransactions = ref<Transaction[]>([]);
const pageNumber = ref(0);
const pageSize = 10;

// startDate and endDate are in ISO format and ref
const startDate = ref('');
const endDate = ref('');



const fromIban = ref('');
const toIban = ref('');
const amount = ref(null);
const minAmount = ref(null);
const maxAmount = ref(null);
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

async function handleSearchTransactions() {
    transactions.value = await fetchTransactions()!;
    filteredTransactions.value = transactions.value;
}

const performSort = (direction: 'asc' | 'desc') => {
    if (direction === 'asc') {
        filteredTransactions.value = filteredTransactions.value.sort((a, b) => a.amount - b.amount);
        sort.value = 'asc';
    } else {
        filteredTransactions.value = filteredTransactions.value.sort((a, b) => b.amount - a.amount);
        sort.value = 'desc';
    }
}

const fetchTransactions = async () => {
    try {
        const baseUrl = `/users/${userId}/transactions`;
        const queryParams = new URLSearchParams();

        // Add optional parameters to the query string
        queryParams.set('pageNumber', pageNumber.value.toString());
        queryParams.set('pageSize', pageSize.toString());
        queryParams.set('startDate', startDate.value.toString());
        queryParams.set('endDate', endDate.value.toString());
        queryParams.set('fromIban', fromIban.value);
        queryParams.set('toIban', toIban.value);
        queryParams.set('amount', amount.value == null ? '' : amount.value);
        queryParams.set('maxAmount', maxAmount.value == null ? '' : maxAmount.value);
        queryParams.set('minAmount', minAmount.value == null ? '' : minAmount.value);

        const url = `${baseUrl}?${queryParams.toString()}`;
        const response = await axios.get(url);
        return response.data;
    }
    catch (error) {
        console.log(error);
    }
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
