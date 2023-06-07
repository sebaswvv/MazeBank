<template>
    <div>
        <h1 class="mt-5" style="text-align: center;">Medewerker overzicht</h1>
        <p class="text-center mt-2">Klik op één van de opties om verder te gaan</p>
        <div class="mt-5 button-container">
            <div>
                <button class="btn btn-secondary btn-lg btn-block custom-button" @click="handleNav('users')">
                    <span class="button-text">Overzicht gebruikers</span>
                    <span class="button-icon">
                        <font-awesome-icon icon="fa-solid fa-user" />
                    </span>
                </button>
            </div>
            <div>
                <button class="btn btn-secondary btn-lg btn-block custom-button" @click="handleNav('accounts')">
                    <span class="button-text">Overzicht rekeningen</span>
                    <span class="button-icon">
                        <font-awesome-icon icon="fa-solid fa-credit-card" />
                    </span>
                </button>
            </div>
            <div>
                <button class="btn btn-secondary btn-lg btn-block custom-button" @click="handleNav('employee/transfer')">
                    <span class="button-text">Overboeken</span>
                    <span class="button-icon">
                        <font-awesome-icon icon="fa-solid fa-money-bill-transfer" />
                    </span>
                </button>
            </div>
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
    height: 300px;
}

.button-text {
    margin-bottom: 5px;
}

.button-container {
    display: flex;
    justify-content: center;
    gap: 20px;
}
</style>
