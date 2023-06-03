<template>
    <div class="container py-5">
        <div class="row ">
            <div class="col d-flex justify-content-center align-items-center">
                <h1>Withdraw</h1>
            </div>
        </div>
        <!-- Select atm option -->
        <div class="row">
            <div class="col d-flex justify-content-center">
                <button class="btn-primary" @click="handleChangeScene(AtmScenes.SELECT)">Terug</button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { AtmScenes } from '../../enums/AtmScenes';
import { useAtmStore } from './../../stores/AtmStore.ts';

const atmStore = useAtmStore();


function handleChangeScene(scene: AtmScenes) {
    atmStore.setSceneState(scene);
}

</script>

<style></style>