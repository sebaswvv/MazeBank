<template>
    <div class="container">
        <div class="row d-flex justify-content-center align-items-center">
            <div class="col-md-8 justify-content-center align-items-center">
                <div class="col d-flex justify-content-center align-items-center mt-5">
                    <h3>Zoek op IBAN of naamhouder</h3>
                </div>
                <div class="col">
                    <input type="text" class="form-control" placeholder="Zoek op IBAN of naamhouder" v-model="searchQuery"
                        @input="performSearch" />
                </div>
            </div>
        </div>
        <div class="row d-flex justify-content-center align-items-center mt-5">
            <div class="col-md-8 d-flex justify-content-center align-items-center">
                <div class="all-accounts bg-light">
                    <template v-if="filteredAccounts.length > 0">
                        <router-link v-for="account in filteredAccounts" :key="account.id" to="/account"
                            class="single-account-link" @click="handleAccountClick(account.id)">
                            <SingleAccountPreview :user="account.user" :account="account" />
                        </router-link>
                    </template>
                    <template v-else>
                        <p>Geen accounts gevonden met deze zoekopdracht.</p>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import SingleAccountPreview from '../components/SingleAccountPreview.vue';
import { ref, onMounted, computed, watch } from 'vue';
import axios from './../utils/axios';
import router from '../router';

const accounts = ref([]);
const searchQuery = ref('');

const fetchAccounts = async () => {
    const res = await axios.get(`/accounts`);
    accounts.value = res.data;
};

const performSearch = (query) => {
    const lowerCaseQuery = query.toLowerCase();
    filteredAccounts.value = accounts.value.filter(
        (account) =>
            account.iban.toLowerCase().includes(lowerCaseQuery) ||
            account.user.firstName.toLowerCase().includes(lowerCaseQuery) ||
            account.user.lastName.toLowerCase().includes(lowerCaseQuery)
    );
};

const filteredAccounts = computed(() => {
    const lowerCaseQuery = searchQuery.value.toLowerCase();
    if (lowerCaseQuery === '') {
        return accounts.value;
    } else {
        return accounts.value.filter(
            (account) =>
                account.iban.toLowerCase().includes(lowerCaseQuery) ||
                account.user.firstName.toLowerCase().includes(lowerCaseQuery) ||
                account.user.lastName.toLowerCase().includes(lowerCaseQuery)
        );
    }
});

function handleAccountClick(accountId) {
    router.push('/account');
}

watch(searchQuery, (newValue) => {
    performSearch(newValue);
});

onMounted(() => {
    fetchAccounts();
});
</script>

<style scoped>
.all-accounts {
    width: 100%;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    padding: 10px;
}

.single-account-link {
    text-decoration: none;
    color: black;
}
</style>
