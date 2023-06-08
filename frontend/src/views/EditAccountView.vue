<template>
    <div class="container">
        <h1>Edit account</h1>
        <!-- show iban, accountType, user, absoluteLimit and created at -->

        <div class="row mt-5">
            <div class="col">
                <label for="iban">IBAN</label>
                <p> {{ accountStore.account?.iban }}</p>
            </div>
            <div class="col">
                <label for="accountType">Type rekening</label>
                <p> {{ accountStore.account?.accountType == 1 ? 'Betaal' : 'Spaar' }}</p>
            </div>
            <div class="col">
                <label for="absoluteLimit">Absolute limiet</label>
                <p> {{ accountStore.account?.absoluteLimit }}</p>
            </div>
            <div class="col">
                <label for="user">Gebruiker</label>
                <p> {{ accountStore.account?.user?.firstName }} {{ accountStore.account?.user?.lastName }}</p>

            </div>

            <!-- button to edit absoluteLimit -->
            <div class="col">
                <label for="absoluteLimit">Nieuwe absolute limiet</label>
                <input type="number" class="form-control" id="absoluteLimit" placeholder="Absolute limiet"
                    v-model="newAbsoluteLimit" />
                <button class="btn-secondary" @click="handleUpdateNewAbsoluteLimit">Opslaan</button>
            </div>


            <div class="col">
                <button class="btn-secondary" @click="handleBlockState"> {{ accountStore.account?.active == true ? 'Blokkeer rekening' : 'Deblokkeer rekening' }}
                </button>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { useAccountStore } from '../stores/AccountStore';
import { ref } from 'vue';
import AccountPatchRequest from './../interfaces/requests/AccountPatchRequest';
import router from '../router';


const accountStore = useAccountStore();
const newAbsoluteLimit = ref(0);
const errorMessage = ref('');

async function handleUpdateNewAbsoluteLimit() {
    const request: AccountPatchRequest = {
        absoluteLimit: newAbsoluteLimit.value
    }
    await accountStore.updateAccount(request);
    router.push('/employee');
}

async function handleBlockState() {
    try {
        if (accountStore.account?.active) {
            await accountStore.blockAccount();
        } else {
            await accountStore.unblockAccount();
        }
        
    } catch (error) {
        errorMessage.value = 'Something went wrong with (un)blocking the account';
    }
}


</script>

<style scoped></style>