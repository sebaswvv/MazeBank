<template>
    <div class="container">
        <div class="row">
        </div>
        <div class="row d-flex justify-content-center align-items-center">
            <div class="col-md-8 d-flex justify-content-center align-items-center">
                <div class="all-users bg-light">
                    <SingleUserPreview v-for="user in users" :key="user.id" :user="user" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import SingleUserPreview from '../components/SingleUserPreview.vue';
import { ref, onMounted } from 'vue';
import axios from './../utils/axios';

const users = ref([]);

async function fetchUsers() {
    const res = await axios.get('/users');
    users.value = res.data;
    console.log(res.data);
}
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
</style>