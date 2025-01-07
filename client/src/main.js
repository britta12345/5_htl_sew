import { createApp } from 'vue';
import App from './App.vue';
import router from './router';  // Hier importierst du den Router

const app = createApp(App);

app.use(router);  // Hier registrierst du den Router in der App

app.mount('#app');
