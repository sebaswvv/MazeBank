<template>
    <div class="row box">
        <div class="col mt-1">
            <h6>{{ user.id }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ user.firstName }}</h6>
        </div>
        <div class="col mt-1">
            <h6>{{ user.lastName }}</h6>
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