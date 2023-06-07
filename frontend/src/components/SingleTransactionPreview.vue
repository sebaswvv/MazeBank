<template>
    <div class="row box">
        <div class="col mt-1">
            <h6>{{ new Date(transaction.timestamp).toLocaleString("nl-NL") }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ transaction.sender }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ transaction.receiver }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ transaction.description }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ transaction.transactionType }}</h6>
        </div>
        <div class="col mt-1">
            <h6>€ {{ formatBalance(transaction.amount) }}</h6>
        </div>
    </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import Transaction from '../interfaces/Transaction';

defineProps({
    transaction: {
        type: Object as PropType<Transaction>,
        required: true,
    },
});

const formatBalance = (balance: any) => {
    return balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

</script>

<style scoped>
.box {
    border: 1px solid #dee2e6;
    margin: 5px;
}
</style>
