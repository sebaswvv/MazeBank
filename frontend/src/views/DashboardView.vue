<template>
    <div class="container">
        <h1 class="text-center mt-3">Dashboard</h1>
        <h2>{{ email }}</h2>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useUserStore } from '../stores/UserStore';
import { useAuthenticationStore } from '../stores/AuthenticationStore';

const authenticationStore = useAuthenticationStore();
const userStore = useUserStore();
const email: any = ref('');

onMounted(async () => {
    // direcht na inloggen gaat nog niet goed
    await userStore.fetchUser(authenticationStore.userId);
    await userStore.getAccountsOfUser(authenticationStore.userId);

    email.value = userStore.getUser.role;
});

</script>

<style></style>