<template>
    <div id="app">
        <div id="content">
            <!-- Your page content goes here -->
        </div>
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
    </div>
</template>
  
<script setup lang="ts">
import { computed } from 'vue';

const getCurrentYear = computed(() => {
    return new Date().getFullYear();
});
</script>
  
<style>
#app {
    display: flex;
    flex-direction: column;
    min-height: 40vh;
}

#content {
    flex: 1;
}

footer {
    border-top: 1px solid #ddd;
    width: 100%;
}
</style>