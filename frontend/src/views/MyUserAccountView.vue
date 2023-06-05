<template>
    <div class="container">
        <div class="row bg-light rounded mt-5 p-5">
            <h1 class="mt-5 text-center">U kunt hier uw gegevens aanpassen</h1>
            <div>
                <label for="email">Email:</label>
                <input v-model="user.email" id="email" placeholder="Email" />
            </div>
            <div>
                <label for="firstName">Voornaam:</label>
                <input v-model="user.firstName" id="firstName" placeholder="First Name" />
            </div>
            <div>
                <label for="lastName">Achternaam:</label>
                <input v-model="user.lastName" id="lastName" placeholder="Last Name" />
            </div>
            <div>
                <label for="phoneNumber">Telefoonnummer:</label>
                <input v-model="user.phoneNumber" id="phoneNumber" placeholder="Phone Number" />
            </div>
            <button @click="saveUser" class="btn-primary mt-3">Opslaan</button>
            <p id="message">{{ message }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, ref } from 'vue';
import { useCurrentUserStore } from '../stores/CurrentUserStore.js';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import router from '../router';
import User from '../interfaces/User';
import { RoleType } from '../enums/RoleType';

const authenticationStore = useAuthenticationStore();
const currentUserStore = useCurrentUserStore();

const message = ref('');

const user = reactive<User>({
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: RoleType.CUSTOMER,
    accounts: [],
    dayLimit: 0,
    transactionLimit: 0,
    bsn: '',
    blocked: false,
});

onMounted(async () => {
    // Check if the user is authenticated
    if (!authenticationStore.isLoggedIn) {
        router.push('/');
    }

    await currentUserStore.fetchUser(authenticationStore.userId);
    await currentUserStore.getAccountsOfUser(authenticationStore.userId);

    Object.assign(user, currentUserStore.getUser);
});

const saveUser = async () => {
    message.value = '';
    // Save the updated user data
    if (await currentUserStore.editUser(user)) {
        message.value = 'Uw gegevens zijn succesvol aangepast';
    } else {
        message.value = 'Er is iets misgegaan';
    }
};
</script>

<style scoped></style>