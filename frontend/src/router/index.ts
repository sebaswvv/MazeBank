import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/HomeView.vue';

const routes = [{ path: '/', component: Home }];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Check if user is logged in before each route change
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('token') !== null;
  const userRole = localStorage.getItem('user_type');

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login');
  } else if (
    to.meta.requiresRestaurantOwner &&
    (!isLoggedIn || userRole !== '1')
  ) {
    next('/');
  } else if (to.meta.requiresAdmin && !isLoggedIn) {
    next('/');
  } else if (to.path === '/register' && isLoggedIn) {
    next('/');
  } else {
    next();
  }
});

export default router;
