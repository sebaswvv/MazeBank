<template>
    <h1>Hi</h1>
    <button @click="handleFetch">Fetch test data</button>
</template>

<script setup lang="ts">

async function handleFetch() {
    const data = await fetch(
        'http://localhost:8080/auth/register',
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                // use this info for register
                email: "johndoe@gmail.com",
                bsn: 987654325,
                firstName: "Lars",
                lastName: "Hartendorp",
                password: "asdasdAasdasd1!",
                phoneNumber: "0613997325",
                dateOfBirth: "1990-01-01"
            })
        }
    );

    console.log(data);
}

</script>

<style></style>