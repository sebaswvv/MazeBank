<template>
    <div class="bg-light w-50">
        <div class="row">
            <div class="col-md-6">
                <h4>{{ title }}</h4>
                <p></p>
            </div>
            <div class="col"></div>
        </div>

    </div>
</template>

<script setup lang="ts">
const { title } = defineProps({
    title: {
        type: String,
        required: true
    }
})

</script>

<style scoped></style>