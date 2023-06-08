<template>
    <div class="container">
        <div class="row d-flex justify-content-center align-items-center">
            <div class="col-md-8 justify-content-center align-items-center">
                <div class="col d-flex justify-content-center align-items-center mt-5">
                    <h3>Zoek op gebruikers (voornaam, achternaam of ID)</h3>
                </div>
                <div class="col">
                    <input type="text" class="form-control" placeholder="Zoek op ID, voornaam of achternaam"
                        v-model="searchQuery" @input="performSearch" />
                </div>
            </div>
        </div>
        <div class="row d-flex justify-content-center align-items-center mt-5">
            <div class="col-md-8 d-flex justify-content-center align-items-center">
                <div class="all-users bg-light">
                    <template v-if="filteredUsers.length > 0">
                        <router-link v-for="user in filteredUsers" :key="user.id" to="/user" class="single-user-link"
                            @click="handleUserClick(user.id)">
                            <SingleUserPreview :user="user" />
                        </router-link>
                    </template>
                    <template v-else>
                        <p>Geen gebruikers gevonden met deze zoekopdracht.</p>
                    </template>
                </div>
            </div>
            <div class="row d-flex justify-content-center align-items-center">
                <div class="col-md-8">
                    <input type="checkbox" class="form-check-input" id="withoutAccounts" v-model="withoutAccounts"
                        @change="fetchUsers" />
                    <label class="form-check-label" for="withoutAccounts">Gebruikers zonder rekening(en)</label>
                </div>
            </div>
            <div class="row d-flex justify-content-center align-items-center mt-3">
                <div class="col-md-8">

                    <button class="btn-secondary" @click="previousPage" v-if="pageNumber !== 0">Vorige</button>
                    <button class="btn-secondary" @click="nextPage">Volgende</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import SingleUserPreview from '../components/SingleUserPreview.vue';
import { ref, onMounted, computed, watch } from 'vue';
import axios from './../utils/axios';
import { useUserStore } from '../stores/UserStore';
import router from '../router';

const users = ref([]);
const searchQuery = ref('');
const pageNumber = ref(0);
const pageSize = ref(10);
const sort = ref('asc');
const withoutAccounts = ref(false);
const userStore = useUserStore();
const fetchUsers = async () => {
    const res = await axios.get(`/users?pageNumber=${pageNumber.value}&pageSize=${pageSize.value}&sort=${sort.value}`);
    users.value = res.data;
    console.log(res.data);
};

async function fetchUsersWithoutAccounts() {
    const res = await axios.get(`/users?pageNumber=${pageNumber.value}&pageSize=${pageSize.value}&sort=${sort.value}&withoutAccounts=true`);
    users.value = res.data;
}

const previousPage = () => {
    if (pageNumber.value > 0) {
        pageNumber.value--;
        fetchUsers();
    }
};

const nextPage = () => {
    pageNumber.value++;
    fetchUsers();
};

watch(withoutAccounts, async (newValue) => {
    if (newValue) {
        await fetchUsersWithoutAccounts();
    } else {
        await fetchUsers();
    }
});

const performSearch = (query: any) => {
    const lowerCaseQuery = query.toLowerCase();
    filteredUsers.value = users.value.filter(
        user =>
            user.firstName.toLowerCase().includes(lowerCaseQuery) ||
            user.lastName.toLowerCase().includes(lowerCaseQuery) ||
            user.id.toString().includes(lowerCaseQuery)
    );
};

const filteredUsers = computed(() => {
    const lowerCaseQuery = searchQuery.value.toLowerCase();
    if (lowerCaseQuery === '') {
        return users.value;
    } else {
        return users.value.filter(user =>
            user.firstName.toLowerCase().includes(lowerCaseQuery) ||
            user.lastName.toLowerCase().includes(lowerCaseQuery) ||
            user.id.toString().includes(lowerCaseQuery)
        );
    }
});

function handleUserClick(userId: number) {
    userStore.fetchUser(userId);
    localStorage.setItem('checkUserId', userId.toString());
    router.push('/user');
}

watch(searchQuery, (newValue) => {
    performSearch(newValue);
});

onMounted(() => {
    fetchUsers();
    console.log(users.value);
});
</script>

<style scoped>
.all-users {
    width: 100%;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    padding: 10px;
}

.single-user-link {
    text-decoration: none;
    color: black;
}
</style>