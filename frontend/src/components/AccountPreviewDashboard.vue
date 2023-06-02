<template>
    <div class="w-50 px-3 py-2 bg-light">
        <div class="row ">
            <div class="col-md-6">
                <h5>{{ accountType }}</h5>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <p>{{ iban }}</p>
            </div>
            <div class="col d-flex justify-content-end">
                <p>€ {{ balance }}</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
// get props iban and balance from parent component
defineProps({
    accountType: {
        type: String,
        required: true
    },
    iban: {
        type: String,
        required: true
    },
    balance: {
        type: Number,
        required: true
    }
});
</script>

<style scoped></style>