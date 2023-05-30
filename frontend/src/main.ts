import { createApp, markRaw } from 'vue';
import './assets/main.css';
import App from './App.vue';
import router from './router';
import type { Router } from 'vue-router';
import { createPinia } from 'pinia';

/* FontAwesome icons */
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faUser, faMask } from '@fortawesome/free-solid-svg-icons';
library.add(faUser);
library.add(faMask);

// Create app
const app = createApp(App);
app.use(router);

// Add router to pinia
const pinia = createPinia();
pinia.use(({ store }) => {
  store.router = markRaw(router);
});

// Mount app
app.use(pinia);
app.component('font-awesome-icon', FontAwesomeIcon);
app.mount('#app');

// declare module 'pinia' with router
declare module 'pinia' {
  export interface PiniaCustomProperties {
    router: Router;
  }
}
