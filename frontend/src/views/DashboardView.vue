<template>
    <div class="container">
        <h2 class="text-center mt-4">Welkom {{ user.firstName }} {{ user.lastName }}</h2>
        <p class="text-center mt-2">Klik op 1 van je accounts on verder te gaan</p>
        <div class="py-5 accounts">
            <div class="center">
                <AccountPreviewDashboard v-for="account in user.accounts?.sort((a, b) => a.accountType - b.accountType)"
                    :key="account.id" :iban="account.iban" :balance="account.balance"
                    :accountType="account.accountType === 0 ? 'Current' : 'Savings'" class="account"
                    @click="handleClickOnAccount(account.iban)" />

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue';
import { useUserStore } from '../stores/UserStore';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import AccountPreviewDashboard from '../components/AccountPreviewDashboard.vue';
import User from '../interfaces/User';

const authenticationStore = useAuthenticationStore();
const userStore = useUserStore();

const user = reactive<User>({
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: 0,
    accounts: []
});

onMounted(async () => {
    await userStore.fetchUser(authenticationStore.userId);
    await userStore.getAccountsOfUser(authenticationStore.userId);

    Object.assign(user, userStore.getUser);
});

const handleClickOnAccount = (iban: any) => {
    // Use the `iban` parameter as needed
    console.log('Clicked on account with IBAN:', iban);
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
</style>
