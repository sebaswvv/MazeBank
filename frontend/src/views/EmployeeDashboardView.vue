<template>
    <h1>Employees</h1>
    <div class="row">
        <div class="col-md-4">
            <button class="btn btn-secondary btn-lg btn-block custom-button" @click="handleNav('users')">
                <span class="button-text">Overzicht gebruikers</span>
                <span class="button-icon">
                <font-awesome-icon icon="fa-solid fa-user" />
                </span>
            </button>
        </div>
    <div class="col-md-4">
            <button class="btn btn-secondary btn-lg btn-block custom-button">
                <span class="button-text">Overzicht rekeningen</span>
                <span class="button-icon">
                <font-awesome-icon icon="fa-solid fa-credit-card" />
                </span>
            </button>
        </div>
        <div class="col-md-4">
            <button class="btn btn-secondary btn-lg btn-block custom-button">
                <span class="button-text">Doe een transactie</span>
                <span class="button-icon">
                <font-awesome-icon icon="fa-solid fa-money-bill-transfer" />
                </span>
            </button>
        </div>
    </div>

</template>

<script setup lang="ts">
import router from '../router';

function handleNav(url: string) {
    router.push('/' + url);
}
</script>

<style>
.custom-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.button-text {
  margin-bottom: 5px; /* Adjust the margin as needed */
}
</style>
