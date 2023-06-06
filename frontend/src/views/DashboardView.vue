<template>
    <div class="container">
        <h2 class="text-center mt-4">Welkom {{ user.firstName }} {{ user.lastName }}</h2>
        <div class="py-5 accounts">
            <div class="center">
                <template v-if="user.accounts && user.accounts.length > 0">
                    <p class="text-center mt-2">Klik op één van uw rekeningen om verder te gaan</p>
                    <AccountPreviewDashboard v-for="account in user.accounts?.sort((a, b) => a.accountType - b.accountType)"
                        :key="account.id" :iban="account.iban" :balance="account.balance"
                        :accountType="account.accountType === 1 ? 'Betaal' : 'Spaar'" class="account"
                        @click="handleClickOnAccount(account.id)" />
                </template>
                <template v-else>
                    <p>U heeft nog geen accounts, neem contact op met ons</p>
                </template>
                <!-- Wilt u u gegevens wijgzigen ga dan naar de <a> mijn-account pagina -->
                <div class="text-center mt-2">
                    <p>Wilt u uw gegevens wijzigen? ga dan naar de <router-link to="/mijn-account">mijn-account
                            pagina</router-link></p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue';
import { useCurrentUserStore } from '../stores/CurrentUserStore.js';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import { useAccountStore } from '../stores/AccountStore';
import router from '../router';
import AccountPreviewDashboard from '../components/AccountPreviewDashboard.vue';
import User from '../interfaces/User';
import { RoleType } from '../enums/RoleType';

const authenticationStore = useAuthenticationStore();
const currentUserStore = useCurrentUserStore();
const accountStore = useAccountStore();

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

onMounted(async () => {
    // check is user is authenticated
    if (!authenticationStore.isLoggedIn) {
        router.push('/');
    }
    await currentUserStore.fetchUser(authenticationStore.userId);
    await currentUserStore.fetchAccountsOfUser(authenticationStore.userId);

    Object.assign(user, currentUserStore.getUser);
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
</style>