<template>
    <div class="container">
        <h2 class="text-center mt-4">Welkom {{ user.firstName }} {{ user.lastName }}</h2>
        <div class="py-5 accounts">
            <div class="center">
                <template v-if="user.accounts && user.accounts.length > 0">
                    <p class="text-center mt-2">Klik op één van uw rekeningen om verder te gaan</p>
                    <AccountPreviewDashboard v-for="account in user.accounts?.sort((a, b) => a.accountType - b.accountType)"
                        :key="account.id" :iban="account.iban" :balance="account.balance"
                        :accountType="account.accountType === AccountType.CURRENT ? 'Betaalrekening' : 'Spaarrekening'"
                        class="account" @click="handleClickOnAccount(account.id)" />
                </template>
                <template v-else>
                    <p>U heeft nog geen accounts, neem contact op met ons</p>
                </template>
                <p class="text-total-balance mt-3">Totaal saldo: €{{ totalBalance }}</p>
                <p class="text-total-balance mt-3">Resterend transactie limiet voor vandaag: €{{
                    currentUserStore.amountRemaining?.toLocaleString('NL-NL', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }) }}</p>

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, ref } from 'vue';
import { useCurrentUserStore } from '../stores/CurrentUserStore.js';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import { useAccountStore } from '../stores/AccountStore';
import axios from './../utils/axios';
import router from '../router';
import AccountPreviewDashboard from '../components/AccountPreviewDashboard.vue';
import User from '../interfaces/User';
import { RoleType } from '../enums/RoleType';
import { AccountType } from '../enums/AccountType';

const authenticationStore = useAuthenticationStore();
const currentUserStore = useCurrentUserStore();
const accountStore = useAccountStore();

const totalBalance = ref(0);

const user = reactive<User>({
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: RoleType.CUSTOMER,
    accounts: [],
    blocked: false,
    bsn: '',

});

async function fetchTotalBalance() {
    try {
        const response = await axios.get(`/users/${user.id}/balance`);
        totalBalance.value = response.data.totalBalance.toLocaleString('NL-NL', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    } catch (error) {
        console.log(error);
    }
}

onMounted(async () => {
    // check is user is authenticated
    if (!authenticationStore.isLoggedIn) {
        router.push('/');
    }
    await currentUserStore.fetchUser(authenticationStore.userId);
    await currentUserStore.fetchAccountsOfUser(authenticationStore.userId);
    Object.assign(user, currentUserStore.getUser);
    await fetchTotalBalance();
});

const handleClickOnAccount = async (id: any) => {
    // Use the `iban` parameter as needed
    await accountStore.fetchAccount(id);
    router.push('/account');
};
</script>

<style scoped>
.center {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}

.account {
    margin: 5px;
}

.account:hover {
    cursor: pointer;
}

.accounts {
    margin-top: 5vh;
}

.text-total-balance {
    font-size: 1.5rem;
    font-weight: bold;
}
</style>