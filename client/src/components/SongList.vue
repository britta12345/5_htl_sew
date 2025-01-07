<template>
  <div v-if="isLoggedIn">
    <h1>Available Songs</h1>
    <input v-model="searchQuery" @input="searchSongs" placeholder="Search for songs by title or artist" />

    <SongComponent
        v-for="song in songs"
        :key="song.id"
        :song="song"
        @edit="editSong(song)"
        @delete="deleteSong(song.id)"
    />

    <p v-if="songs.length === 0">No songs can be found. Please adjust your search.</p>

    <div class="pagination">
      <button @click="changePage(0)" :disabled="currentPage === 0">First</button>
      <button @click="changePage(currentPage - 1)" :disabled="currentPage === 0">Previous</button>
      <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">Next</button>
      <button @click="changePage(totalPages - 1)" :disabled="currentPage >= totalPages - 1">Last</button>
    </div>

    <button @click="showNewSongEditor">Add New Song</button>
    <SongEditor
        v-if="editorVisible"
        :song="currentSong"
        :isEdit="isEdit"
        @refreshSongs="fetchSongs"
        @closeEditor="closeEditor"
    />
  </div>

  <div v-else>
    <h1>Please log in to access the songs.</h1>
    <button @click="redirectToLogin">Go to Login</button>
  </div>
</template>

<script>
import axios from 'axios';
import SongComponent from './SongComponent.vue';
import SongEditor from './SongEditor.vue';

export default {
  components: {
    SongComponent,
    SongEditor
  },
  data() {
    return {
      songs: [],
      editorVisible: false,
      currentSong: {},
      isEdit: false,
      searchQuery: '',
      currentPage: 0,
      totalPages: 0,
      isLoggedIn: false, // Login-Status für die Song-Liste
    };
  },
  mounted() {
    this.checkLogin(); // Überprüfe den Login-Status beim Laden der Seite
    this.fetchSongs(); // Hole Songs, wenn eingeloggt
  },
  methods: {
    // Überprüfen, ob der Benutzer eingeloggt ist
    checkLogin() {
      const token = localStorage.getItem('authToken');
      this.isLoggedIn = true || !!token; // Wenn Token vorhanden, setze isLoggedIn auf true
    },

    // Songs von der API abrufen
    async fetchSongs() {
      if (!this.isLoggedIn) {
        return; // Songs nur abrufen, wenn eingeloggt
      }

      try {
        const token = localStorage.getItem('authToken');
        const response = await axios.get(`http://localhost:8082/api/songs`, {
          headers: { 'Authorization': `Bearer ${token}` },
          params: { page: this.currentPage, size: 5 }
        });

        this.songs = response.data.content;
        this.totalPages = response.data.totalPages;
      } catch (error) {
        console.error('Error fetching songs', error);
        this.songs = [];
      }
    },

    // Suchfunktion für Songs
    searchSongs() {
      if (this.searchQuery.trim() === '') {
        this.fetchSongs(); // Lade alle Songs, wenn das Suchfeld leer ist
      } else {
        axios.get(`http://localhost:8082/search`, {
          params: {
            query: this.searchQuery,
          }
        })
            .then(response => {
              if (Array.isArray(response.data)) {
                this.songs = response.data;
              } else {
                this.songs = [];
              }
            })
            .catch(error => {
              console.error('Error fetching search results:', error);
              this.songs = [];
            });
      }
    },

    // Seitenwechsel der Song-Liste
    changePage(newPage) {
      if (newPage >= 0 && newPage < this.totalPages) {
        this.currentPage = newPage;
        this.fetchSongs(); // Lade Songs der neuen Seite
      }
    },

    // Song bearbeiten
    editSong(song) {
      this.currentSong = { ...song };
      this.isEdit = true;
      this.editorVisible = true;
    },

    // Song-Editor anzeigen (für einen neuen Song)
    showNewSongEditor() {
      this.currentSong = { title: '', artist: '', genre: '', length: 0 };
      this.isEdit = false;
      this.editorVisible = true;
    },

    // Song löschen
    deleteSong(id) {
      if (confirm('Are you sure you want to delete this song?')) {
        axios.delete(`http://localhost:8082/api/songs/${id}`)
            .then(() => this.fetchSongs())
            .catch(error => {
              console.error('Error deleting song', error);
            });
      }
    },

    // Song-Editor schließen
    closeEditor() {
      this.editorVisible = false;
      this.currentSong = { title: '', artist: '', genre: '', length: 0 };
    },

    // Zur Login-Seite weiterleiten
    redirectToLogin() {
      this.$router.push('/login');
    }
  }
};
</script>
