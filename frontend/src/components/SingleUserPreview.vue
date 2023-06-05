<template>
    <div class="row box">
        <div class="col">
            <h5>{{ user.id }}</h5>
        </div>
        <div class="col">
            <h5>{{ user.firstName }}</h5>
        </div>
        <div class="col">
            <h5>{{ user.lastName }}</h5>
        </div>
    </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import UserCompact from '../interfaces/UserCompact';
const { user } = defineProps({
    user: {
        type: Object as PropType<UserCompact>,
        required: true
    }
});
</script>

<style scoped>
.box {
    border: 1px solid #dee2e6;
    margin: 5px;
}
</style>