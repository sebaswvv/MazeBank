<template>
    <nav class="navbar navbar-expand-lg sticky bg-white navbar-light">
        <div class="container">
            <router-link :to="getMazeBankLink" class="nav-link" active-class="active navbar-brand">MazeBank</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                    </li>
                    <li class="nav-item" v-if="authenticationStore.getIsLoggedIn">
                        <router-link to="/atm" class="nav-link" active-class="active">
                            <font-awesome-icon icon="fa-solid fa-user" />
                            ATM</router-link>
                    </li>
                    <li v-if="authenticationStore.getIsLoggedIn" class=" nav-item">
                        <router-link to="/dashboard" class="nav-link" active-class="active">
                            <font-awesome-icon icon="fa-solid fa-user" />
                            Dashboard</router-link>
                    </li>
                    <li v-if="currentUserStore.getIsEmployee" class=" nav-item">
                        <router-link to="/employee" class="nav-link" active-class="active">
                            <font-awesome-icon icon="fa-solid fa-user" />
                            Medewerker</router-link>
                    </li>
                    <!-- Buttons voor in/uit-loggen -->
                    <li class="nav-item">
                        <button v-if="!authenticationStore.getIsLoggedIn" class=" btn btn-primary"
                            @click="goToLogin">Inloggen</button>
                        <button v-if="authenticationStore.getIsLoggedIn" class=" btn btn-primary"
                            @click="authenticationStore.logout">Uitloggen</button>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useAuthenticationStore } from '../stores/AuthenticationStore';
import { useCurrentUserStore } from '../stores/CurrentUserStore';
import { computed } from 'vue';

// VARIABLES
const authenticationStore = useAuthenticationStore();
const currentUserStore = useCurrentUserStore();
const router = useRouter();

// Computed property to determine the MazeBank link based on login status
const getMazeBankLink = computed(() => {
    return authenticationStore.getIsLoggedIn ? '/dashboard' : '/';
});

// redirect to login page
function goToLogin() {
    router.push('/login');
}
</script>

<style>
.nav-link {
    font-size: 17px;
    font-weight: 600;
    color: #000 !important;
    padding-left: 20px !important;
    padding-right: 20px !important;
}

.navbar-brand {
    font-size: 25px !important;
    padding-left: 0px !important;
}

@media (max-width: 992px) {
    .navbar-nav {
        margin-top: 10px;
    }

    .nav-item {
        text-align: center;
        margin-bottom: 10px;
    }

    .nav-link {
        padding: 10px 0;
        font-size: 16px;
        font-weight: 500;
    }

    .navbar-toggler {
        margin: 5px;
    }

    /* hamburger black */


    /* remove the grey border around the hamburger menu */
    .navbar-light .navbar-toggler {
        color: black;
        border: none;
        border: none;
    }
}
</style>