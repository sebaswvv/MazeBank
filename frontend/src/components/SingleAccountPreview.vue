<template>
    <div class="row box">
        <div class="col mt-1">
            <h6>{{ account.iban }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ user.firstName[0] }} {{ user.lastName }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ getAccountTypeText(account.accountType) }}</h6>
        </div>
        <div class="col mt-1">
            <h6>€ {{ formatBalance(account.balance) }}</h6>
        </div>
    </div>
</template>


<script setup lang="ts">
import { PropType } from 'vue';
import AccountCompact from '../interfaces/AccountCompact';
import UserCompact from '../interfaces/UserCompact';

defineProps({
    user: {
        type: Object as PropType<UserCompact>,
        required: true
    },
    account: {
        type: Object as PropType<AccountCompact>,
        required: true
    }
});

const getAccountTypeText = (accountType) => {
    return accountType === 1 ? 'Betaalrekening' : 'Spaarrekening';
};

const formatBalance = (balance) => {
    return balance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

</script>

<style scoped>
.box {
    border: 1px solid #dee2e6;
    margin: 5px;
}
</style>
