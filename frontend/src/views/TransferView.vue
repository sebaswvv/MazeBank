<template>
    <div class="container py-5">
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-primary" @click="handleBackButton">Terug naar overzicht</button>
            </div>
        </div>

        <div class="row py-5 rounded">
            <div class="col d-flex justify-content-center">
                <AccountPreview :title="account?.accountType === AccountType.CURRENT ? 'Betaalrekening' : 'Spaarrekening'" />
            </div>
        </div>

        <div class="row rounded bg-light py-2 d-flex justify-content-center">
            <form class="col-md-6">
                <label for="amount" class="form-label">Bedrag</label>
                <div class="input-group">
                    <span class="input-group-text" id="amount">€</span>
                    <input type="number" class="form-control" placeholder="0.00" inputmode="decimal" step="0.01" min="0.01" v-model="amount">
                </div>

                <div class="mb-3">
                    <label for="iban" class="form-label">Rekeningnummer ontvanger (IBAN)</label>
                    <input class="form-control" id="iban" list="ibanSearch" placeholder="Vul een IBAN in of zoek op naam..." v-model="iban" @input="handleIbanSearch">
                    <datalist id="ibanSearch">
                        <option v-for="result in ibanSearchResults" :value="result.iban">
                            {{ result.firstName }} {{ result.lastName }}
                        </option>
                    </datalist>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label">Omschrijving</label>
                    <input type="text" class="form-control" id="description" placeholder="Vul hier een omschrijving in" v-model="description">
                </div>

                <button type="submit" class="btn btn-primary" @click="handleSubmit">Overboeken</button>
                <div id="submitHelp" class="form-text py-2 text-danger">{{ submitMessage }}</div>
            </form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { storeToRefs } from 'pinia';
import AccountPreview from '../components/AccountPreview.vue';
import { useAccountStore } from '../stores/AccountStore';
import { useTransactionStore } from '../stores/TransactionStore';
import router from '../router';
import AccountSearch from '../interfaces/AccountSearch';
import { AccountType } from '../enums/AccountType';
import { AxiosError } from 'axios';

const accountStore = useAccountStore();
const { account } = storeToRefs(accountStore);
const transactionStore = useTransactionStore();
const ibanSearchResults = ref<AccountSearch[]>([]);

const amount = ref<number | null>();
const iban = ref('');
const description = ref('');
const submitMessage = ref('');

function handleBackButton() {
    router.push('/account');
}

async function handleIbanSearch({ target }: Event) {
    const query = (target as HTMLInputElement).value.trim();
    const results = query ? await accountStore.searchAccount(query) : [];
    ibanSearchResults.value = results.slice(0, 20);
}

async function handleSubmit(e: Event) {
    e.preventDefault();

    try {
        await transactionStore.createTransaction({
            amount: amount.value!,
            description: description.value,
            senderIban: account.value!.iban,
            receiverIban: iban.value,
        });
    } catch (err) {
        if (err instanceof AxiosError)
            submitMessage.value = Object.values(err.response?.data).join('\n');
        return;
    }

    router.push('/account');
}

onMounted(async () => {
    if (!account.value) {
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