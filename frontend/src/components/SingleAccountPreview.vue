<template>
    <div class="row box">
        <div class="col mt-1">
            <h5>{{ account.iban }}</h5>
        </div>
        <div class="col mt-1">
            <h5>{{ user.firstName[0] }} {{ user.lastName }}</h5>
        </div>
        <div class="col mt-1">
            <h5>{{ account.accountType }}</h5>
        </div>
        <div class="col mt-1">
            <h5>{{ account.balance }}</h5>
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
</script>

<style scoped>
.box {
    border: 1px solid #dee2e6;
    margin: 5px;
}
</style>