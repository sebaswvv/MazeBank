<template>
    <div class="container py-5 d-flex justify-content-center flex-column">
        <div class="row pb-3">
            <div class="col d-flex justify-content-center align-items-center">
                <h1>User: {{ userStore.getFullName }} {{ userStore.getUserId }}</h1>
            </div>
        </div>
        <!-- Select atm option -->
        <div class="row">

        </div>
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from '../stores/UserStore';
import { ref } from 'vue';

const userStore = useUserStore();

// const meegekregenId = ref(2);

// userStore.fetchUser(meegekregenId.value);

</script>

<style scoped></style>