<template>
    <h1>Hi</h1>
    <button @click="handleFetch">Fetch test data</button>
</template>

<script setup lang="ts">
import axios from './../utils/axios';

async function handleFetch() {
    const data = await axios.post('auth/register', {
        email: 'user2@example.com',
        password: '1234',
    });
    console.log(data);
}

</script>

<style></style>