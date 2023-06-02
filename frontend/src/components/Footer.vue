<template>
    <footer class="bg-light py-4">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <h5 class="mb-0">&copy; {{ getCurrentYear }} MazeBank: 100% legit - no heists required</h5>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <ul class="list-inline mb-0">
                        <li class="list-inline-item">&middot;</li>
                        <li class="list-inline-item">
                            <a href="/mijn-account" class="text-dark">Mijn account</a>
                        </li>

                    </ul>
                </div>
            </div>
        </div>
    </footer>
</template>
<script setup lang="ts">
import { computed } from 'vue';

const getCurrentYear = computed(() => {
    return new Date().getFullYear();
});
</script>

<style>
footer {
    border-top: 1px solid #ddd;
    position: fixed;
    bottom: 0;
    width: 100%;
}

/*
footer p {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 0;
}

footer ul li {
    display: inline-block;
}

footer ul li:not(:last-child) {
    margin-right: 10px;
}

footer ul li a {
    font-size: 16px;
    font-weight: 500;
    text-decoration: none;
    transition: color 0.2s ease-in-out;
}

footer ul li a:hover {
    color: #000;
} */
</style>