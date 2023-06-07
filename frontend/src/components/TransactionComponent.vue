<template>
    <div class="outer px-3 py-2">
        <div class="row">
            <div class="col d-flex justify-content-between align-items-center">
                <p>{{ new Date(transaction.timestamp).toLocaleString("nl-NL") }}</p>
                <h5>{{ transaction.description }}</h5>
                <div class="amount-box d-flex justify-content-center align-items-center pt-2"
                    :class="{ 'green-box': accountStore.getIban === transaction.receiver, 'red-box': accountStore.getIban !== transaction.receiver }">
                    <p v-if="accountStore.getIban === transaction.receiver">+</p>
                    <p v-else>-</p>&nbsp;
                    <p class="text-center">€ {{ transaction.amount }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import Transaction from '../interfaces/Transaction';
import { PropType } from 'vue';
import { useAccountStore } from '../stores/AccountStore';

const accountStore = useAccountStore();

const { transaction } = defineProps({
    transaction: {
        type: Object as PropType<Transaction>,
        required: true
    }
});
</script>

<style scoped>
.outer {
    width: 95%;
    border-bottom: 1px solid #dee2e6;
}

.amount-box {
    background-color: #efefef;
    width: 100px;
    height: 50px;
    border-radius: 5px;
}

.green-box {
    background-color: rgb(159, 227, 159);
}

.red-box {
    background-color: rgb(237, 151, 151);
}

.amount-box p {
    font-size: 1.2rem;
    font-weight: bold;
    color: var(--primary-color);
}
</style>
