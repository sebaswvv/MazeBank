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
            <!-- Error message -->
            <p id="message">{{ message }}</p>
            <div v-if="errorMessage" class="alert alert-danger mt-3">
                {{ errorMessage }}
            </div>
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
import UserPatchRequest from '../interfaces/requests/UserPatchRequest';
import axios from './../utils/axios'

const authenticationStore = useAuthenticationStore();
const currentUserStore = useCurrentUserStore();

const message = ref('');
const errorMessage = ref<string | null>(null);


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
    await currentUserStore.fetchAccountsOfUser(authenticationStore.userId);

    Object.assign(user, currentUserStore.getUser);
});



const saveUser = async () => {
    message.value = '';
    errorMessage.value = null;

    const userPatchRequest: UserPatchRequest = {
        email: user.email == '' ? undefined : user.email,
        firstName: user.firstName == '' ? undefined : user.firstName,
        lastName: user.lastName == '' ? undefined : user.lastName,
        phoneNumber: user.phoneNumber == '' ? undefined : user.phoneNumber,
    };

    try {
        const response = await axios.patch(
            `/users/${user.id}`,
            userPatchRequest
        );
        if (response.status === 200) {
            message.value = 'Uw gegevens zijn succesvol aangepast';
            currentUserStore.fetchUser(authenticationStore.userId);
        }
    }
    catch (error: any) {
        errorMessage.value = error.response.data.message;
    }
};
</script>

<style scoped></style>