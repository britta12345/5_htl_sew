<template>
  <div id="app">
    <!-- Router-View wird den entsprechenden Inhalt je nach Route anzeigen -->
    <router-view></router-view>
  </div>
</template>

<script>
//import SongComponent from './components/SongComponent.vue';
//import SongEditor from './components/SongEditor.vue';

export default {
  components: {
    //SongComponent,
    //SongEditor
  },
  data() {
    return {
      isLoggedIn: false, // Login-Status
    };
  },
  mounted() {
    this.checkLogin(); // Überprüfe den Login-Status beim Laden der Seite
  },
  methods: {
    // Überprüfe, ob der Benutzer eingeloggt ist
    checkLogin() {
      const token = localStorage.getItem('authToken'); // Hole das Token aus dem localStorage
      this.isLoggedIn = !!token; // Wenn das Token existiert, setze isLoggedIn auf true
    },
  }
};
</script>
