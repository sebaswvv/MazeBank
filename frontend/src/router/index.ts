import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/HomeView.vue';
import Atm from '../views/AtmView.vue';
import Account from '../views/AccountView.vue';
import Login from '../views/LoginView.vue';
import CustomerDashboard from '../views/DashboardView.vue';
import EmployeeDashboard from '../views/EmployeeDashboardView.vue';
import User from '../views/UserView.vue';
import MyUserAccountView from '../views/MyUserAccountView.vue';

// Define routes
const routes = [
  { path: '/', component: Login },
  { path: '/home', component: Home },
  { path: '/atm', component: Atm },
  { path: '/login', component: Login },
  { path: '/dashboard', component: CustomerDashboard },
  { path: '/employee', component: EmployeeDashboard },
  { path: '/account', component: Account, meta: { requiresAuth: true } },
  { path: '/dashboard', component: CustomerDashboard },
  { path: '/user', component: User, meta: { requiresAuth: true } },
  {
    path: '/mijn-account',
    component: MyUserAccountView,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Check if user is logged in before each route change zodat je paginas kan afschermen
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
