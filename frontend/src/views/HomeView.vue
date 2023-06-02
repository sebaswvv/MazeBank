<template>
    <h1>Home bank of the great maze bank</h1>
    <p>Lorem ipsum, dolor sit amet consectetur adipisicing elit. Nostrum, fuga quis. Sunt eum earum dolores commodi, facere
        hic placeat? Repellat accusantium laudantium exercitationem ad illo facere modi placeat, error impedit.</p>
    <button class="btn-primary" @click="handleLogin">Primary</button>
    <button class="btn-secondary">secondary</button>
</template>

<script setup lang="ts">
// import axios from 'axios';
import { useAuthenticationStore } from '../stores/AuthenticationStore';

const authenticationStore = useAuthenticationStore();



async function handleLogin() {

    const data = {
        email: "user3@example.com",
        password: "1234"
    }
    // console.log('login')
    // const response = axios.post('localhost:8080/auth/login', {
    //     email: 'user3@example.com',
    //     password: '1234'
    // })

    // const response = fetch('http://localhost:8080/auth/login', {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify({
    //         email: "user3@example.com",
    //         password: "1234"
    //     }
    //     )
    // });

    // console.log(response)

    authenticationStore.login(data);
}

</script>

<style></style>