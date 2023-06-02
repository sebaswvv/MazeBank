<template>
    <div class="w-50 px-3 py-2 bg-light">
        <div class="row ">
            <div class="col-md-6">
                <h5>{{ title }}</h5>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <p>{{ accountStore.getIban }}</p>
            </div>
            <div class="col d-flex justify-content-end">
                <p>€ {{ accountStore.getBalance }}</p>
            </div>
        </div>



    </div>
</template>

<script setup lang="ts">
import { useAccountStore } from '../stores/AccountStore';

const accountStore = useAccountStore();
const { title } = defineProps({
    title: {
        type: String,
        required: true
    }
})

</script>

<style scoped></style>