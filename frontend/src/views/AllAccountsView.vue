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

    </div>
</template>

<script setup lang="ts">
import SingleAccountPreview from '../components/SingleAccountPreview.vue';
import { ref, onMounted, computed, watch } from 'vue';
import axios from './../utils/axios';
import { useAccountStore } from '../stores/AccountStore';
import { useUserStore } from '../stores/UserStore';
import router from '../router';
import { AccountCompact } from '../interfaces/AccountCompact';
import { UserCompact } from '../interfaces/UserCompact';

const accounts = ref<AccountCompact[]>([]);
const users = ref<UserCompact[]>([]);
const searchQuery = ref('');
const offset = ref(0);
const limit = ref(10);
const sort = ref('asc');

const fetchAccounts = async () => {
    const res = await axios.get(`/accounts?offset=${offset.value}&limit=${limit.value}&sort=${sort.value}`);
    accounts.value = res.data;
    console.log(res.data);
};

const fetchUsers = async () => {
    const res = await axios.get('/users');
    users.value = res.data;
};


const getNameholder = (userId: number): string => {
    const user = users.value.find((user) => user.id === userId);
    if (user) {
        return `${user.firstName} ${user.lastName}`;
    }
    return '';
};

const performSearch = (query: string) => {
    const lowerCaseQuery = query.toLowerCase();
    filteredAccounts.value = accounts.value.filter(
        (account) =>
            account.iban.toLowerCase().includes(lowerCaseQuery) ||
            getNameholder(account.userId).toLowerCase().includes(lowerCaseQuery)
    );
};

const filteredAccounts = computed(() => {
    const lowerCaseQuery = searchQuery.value.toLowerCase();
    if (lowerCaseQuery === '') {
        return accounts.value;
    } else {
        return accounts.value.filter((account) =>
            account.iban.toLowerCase().includes(lowerCaseQuery) ||
            getNameholder(account.userId).toLowerCase().includes(lowerCaseQuery)
        );
    }
});

function handleAccountClick(accountId: number) {
    useAccountStore().fetchAccount(accountId);
    router.push('/account');
}

watch(searchQuery, (newValue) => {
    performSearch(newValue);
});

onMounted(() => {
    fetchAccounts();
    fetchUsers();
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
