<template>
    <div class="container py-5">
        <div class="row">
            <div class="col d-flex justify-content-center mb-5">
                <button class="btn-primary" @click="handleBackButton">Terug naar overzicht</button>
            </div>
        </div>

        <div v-if="!employeeView" class="row mb-5 rounded">
            <div class="col d-flex justify-content-center">
                <AccountPreview
                    :title="account?.accountType === AccountType.CURRENT ? 'Betaalrekening' : 'Spaarrekening'" />
            </div>
        </div>

        <div class="row rounded bg-light py-2 d-flex justify-content-center">
            <form class="col-md-6">
                <label for="amount" class="form-label">Bedrag</label>
                <div class="input-group">
                    <span class="input-group-text" id="amount">€</span>
                    <input type="number" class="form-control" placeholder="0.00" inputmode="decimal" step="0.01" min="0.01"
                        v-model="amount">
                </div>

                <div v-if="employeeView" class="mb-3">
                    <label for="senderIban" class="form-label">Rekeningnummer verzender (IBAN)</label>
                    <input class="form-control" id="senderIban" list="senderIbanSearch"
                        placeholder="Vul een IBAN in of zoek op naam..." v-model="senderIban"
                        @input="e => handleIbanSearch(e, 'sender')">
                    <datalist id="senderIbanSearch">
                        <option v-for="result in senderIbanSearchResults" :value="result.iban">
                            {{ result.firstName }} {{ result.lastName }}
                        </option>
                    </datalist>
                </div>

                <div class="mb-3">
                    <label for="receiverIban" class="form-label">Rekeningnummer ontvanger (IBAN)</label>
                    <input class="form-control" id="receiverIban" list="receiverIbanSearch"
                        placeholder="Vul een IBAN in of zoek op naam..." v-model="receiverIban"
                        @input="e => handleIbanSearch(e, 'receiver')">
                    <datalist id="receiverIbanSearch">
                        <option v-for="result in receiverIbanSearchResults" :value="result.iban">
                            {{ result.firstName }} {{ result.lastName }}
                        </option>
                    </datalist>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Omschrijving</label>
                    <input type="text" class="form-control" id="description" placeholder="Vul hier een omschrijving in"
                        v-model="description">
                </div>

                <button type="submit" class="btn btn-primary" @click="handleSubmit">Overboeken</button>
                <div class="alert alert-danger mt-3" role="alert" v-if="submitMessage">
                    {{ submitMessage }}
                </div>
            </form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { storeToRefs } from 'pinia';
import { AxiosError } from 'axios';
import AccountPreview from '../components/AccountPreview.vue';
import { useAccountStore } from '../stores/AccountStore';
import { useTransactionStore } from '../stores/TransactionStore';
import router from '../router';
import AccountSearch from '../interfaces/AccountSearch';
import { AccountType } from '../enums/AccountType';

const { employeeView } = defineProps({
    employeeView: {
        type: Boolean,
        required: false,
        default: false,
    },
});

const accountStore = useAccountStore();
const { account } = storeToRefs(accountStore);
const transactionStore = useTransactionStore();

const senderIbanSearchResults = ref<AccountSearch[]>([]);
const receiverIbanSearchResults = ref<AccountSearch[]>([]);
const amount = ref<number | null>();
const senderIban = ref('');
const receiverIban = ref('');
const description = ref('');
const submitMessage = ref('');

function handleBackButton() {
    router.push(employeeView ? '/employee' : '/account');
}

async function handleIbanSearch({ target }: Event, type: 'sender' | 'receiver') {
    const query = (target as HTMLInputElement).value.trim();
    const results = query ? await accountStore.searchAccount(query) : [];
    const searchResults = type === 'sender' ? senderIbanSearchResults : receiverIbanSearchResults;
    searchResults.value = results.slice(0, 20);
}

async function handleSubmit(e: Event) {
    e.preventDefault();

    try {
        const sender = employeeView ? senderIban.value : account.value!.iban;

        await transactionStore.createTransaction({
            amount: amount.value!,
            description: description.value,
            senderIban: sender,
            receiverIban: receiverIban.value,
        });
    } catch (err) {
        if (err instanceof AxiosError)
            submitMessage.value = Object.values(err.response?.data).join('\n');
        return;
    }

    router.push('/account');
}

onMounted(async () => {
    // redirect to dashboard if no account is selected
    if (!employeeView && !account.value) {
        return router.push('/dashboard');
    }
});
</script>

<style>
.input-group-text {
    padding: 12px 15px;
    margin: 8px 0;
}
</style>