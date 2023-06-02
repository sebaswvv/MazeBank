<template>
    <div class="container">
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-primary">Overboeken</button>
            </div>
        </div>
        <!-- Show the current account -->
        <div class="row bg-light py-5">
            <div class="col d-flex justify-content-center">
                <AccountPreview title="Current account" />
            </div>

        </div>

        <!-- Show transactions of this account -->
        <div class="row bg-dark py-5">

        </div>
    </div>
</template>

<script setup lang="ts">
import AccountPreview from '../components/AccountPreview.vue';
// Fetch account

// fetch transactions

</script>

<style></style>