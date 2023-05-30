<template>
    <footer class="bg-light py-4">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <p class="mb-0">&copy; {{ getCurrentYear }} MazeBank. All Rights Reserved.</p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <ul class="list-inline mb-0">
                        <li class="list-inline-item">
                            <a href="/privacy-policy" class="text-dark">Privacy Policy</a>
                        </li>
                        <li class="list-inline-item">&middot;</li>
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
}

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
}
</style>