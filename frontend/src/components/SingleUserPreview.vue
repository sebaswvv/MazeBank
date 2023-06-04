<template>
    <div class="row box">
        <div class="col"><h5>{{  user.id }}</h5></div>
        <div class="col"><h3>{{ user.firstName }}</h3></div>
        <div class="col"><h3>{{ user.lastName }}</h3></div>
    </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue';
import UserCompact from '../interfaces/UserCompact';
const {user} = defineProps({
    user: {
        type: Object as PropType<UserCompact>,
        required: true
    }
});
</script>

<style scoped>
.box {
    width: 95%;
    border-bottom: 1px solid #dee2e6;
}

</style>