<template>
    <div class="container py-5 d-flex justify-content-center flex-column">
        <div class="row pb-3">
            <!-- FullName of the user -->
            <div class="col d-flex justify-content-center align-items-center">
                <h1>User: {{ user.firstName }} {{ user.lastName }} {{ userId }}</h1>
            </div>

        </div>
        <!-- Select atm option -->
        <div class="row">
            <!-- Col met alle userdata -->
            <div class="col-md-6">
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
                <div>
                    <label for="dayLimit">Dag limiet:</label>
                    <input v-model="user.dayLimit" id="dayLimit" placeholder="Day Limit" />
                </div>
                <div>
                    <label for="transactionLimit">Transactie limiet:</label>
                    <input v-model="user.transactionLimit" id="transactionLimit" placeholder="Transaction Limit" />
                </div>
                <button @click="saveUser" class="btn-primary">Opslaan</button>
                <p id="message">{{ message }}</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from '../stores/UserStore';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import { ref, onMounted, reactive } from 'vue';
import User from '../interfaces/User';
import router from '../router';

const userStore = useUserStore();
const authenticationStore = useAuthenticationStore();

onMounted(async () => {
    // Check if the user is authenticated
    if (!authenticationStore.isLoggedIn) {
        router.push('/');
    }

    await userStore.fetchUser(2);
    Object.assign(user, userStore.getUser);

});

const user = reactive<User>({
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: 'CUSTOMER',
    accounts: [],
    dayLimit: 0,
    transactionLimit: 0
});

const userId = userStore.getUserId;
const message = ref('');

async function saveUser() {
    try {
        // Call a method in the userStore to save the user data
        await userStore.updateUser(user);
        Object.assign(user, userStore.getUser);

        message.value = 'User data saved successfully.';
    } catch (error) {
        message.value = 'Error occurred while saving user data.';
    }
}
</script>

<style scoped></style>
