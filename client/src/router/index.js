import { createRouter, createWebHistory } from 'vue-router';
import SongList from '../components/SongList.vue';
//import Login from '../components/LoginForm.vue';  // Angenommen, du hast eine Login-Komponente

const routes = [
    {
        path: '/',
        name: 'home',
        component: SongList,
        //meta: { requiresAuth: true }
    },
    /*{
        path: '/login',
        name: 'login',
        component: Login
    }*/
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

// Authentifizierung-Guard
/*router.beforeEach((to, from, next) => {
    const isLoggedIn = localStorage.getItem('authToken');
    if (to.meta.requiresAuth && !isLoggedIn) {
        next({ name: 'login' }); // Wenn nicht eingeloggt, zur Login-Seite weiterleiten
    } else {
        next();
    }
});*/

export default router;
