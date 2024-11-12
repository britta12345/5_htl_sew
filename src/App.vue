<template>
  <div id="app">
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

    <!-- Pagination Navigation -->
    <div class="pagination">
      <!-- bei einem Klick auf Buttons -> changePage-Methode wird aufgerufen
       :disabled: -> Buttons werden deaktiviert wenn zb erste oder letzte Seite-->
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
</template>

<script>
import axios from 'axios';
import SongComponent from './components/SongComponent.vue';
import SongEditor from './components/SongEditor.vue';

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
      isLoading: false,
      showError: false,
    };
  },
  mounted() {
    this.fetchSongs(); // Lade die Songs, wenn die Komponente gemountet wird
  },
  methods: {
    async fetchSongs() {
      try {
        this.isLoading = true;
        // Sendet GET-Anfrage an den Server
        const response = await axios.get(`http://localhost:8082/api/songs`, {
          // (aktuellen Seiten- und Größen parameter) werden übergeben
          params: {
            page: this.currentPage,
            size: 5 // Anzahl der Songs pro Seite
          }
        });
        // Enthält Song-Daten die von Page<Song>-Rückgabe des Servers zurückgegeben werden
        this.songs = response.data.content;
        // Aktualisiert die Gesamtanzahl der Seiten die auf dem Server zurückgegeben werden
        this.totalPages = response.data.totalPages;
      } catch (error) {
        console.error('There was an error fetching the songs!', error);
        this.songs = [];
        this.showError = true;
      } finally {
        this.isLoading = false;
      }
    },
    changePage(newPage) {
      if (newPage >= 0 && newPage < this.totalPages) {
        this.currentPage = newPage; // Setzt die aktuelle Seite
        this.fetchSongs(); // Ladet die Songs für die neue Seite
      }
    },


    // = serverbasierte Suche
    //Suche nicht lokal (auf dem Client) sondern auf einem Server
    // Datenbanksuche mit dem Query und sucherg in this.songs gespeichert
    searchSongs() {
      // Wenn das Suchfeld leer ist, alle Songs laden
      if (this.searchQuery.trim() === '') {
        this.fetchSongs(); // Funktion zum Laden aller Songs
      } else {
        console.log('##searchSongs## Searching for:', this.searchQuery);

        // Axios-GET-Anfrage mit den richtigen Query-Parametern
        axios.get(`http://localhost:8082/search`, {
          params: {
            query: this.searchQuery, // Titelparameter mit dem Suchbegriff
          }
        })
            .then(response => {
              console.log('##searchSongs## Search results:', response.data);

              // Überprüfen der Struktur der Antwort
              if (Array.isArray(response.data)) {
                this.songs = response.data; // Ergebnisse in das songs-Array speichern
                this.songs.forEach(song => {
                  console.log(`Title: ${song.title}, Artist: ${song.artist ? song.artist.name : 'Unknown'}`);
                });
              } else {
                console.error('Unexpected response format:', response.data);
                this.songs = []; // Leere Liste, wenn die Antwort nicht wie erwartet ist
              }
            })
            .catch(error => {
              console.error('##searchSongs## Error fetching search results:', error);
              this.songs = []; // Leere Songliste anzeigen, wenn ein Fehler auftritt
            });
      }
    },



    // Setzt den aktuellen Song für die Bearbeitung
    editSong(song) {
      //Object.assign(this.currentSong,song);
      this.currentSong = {...song}; // Erstelle eine Kopie des Songs
      this.isEdit = true; // Setze den Bearbeitungsmodus
      this.editorVisible = true; // Zeige den Editor an
      console.log('Editing song with ID:', song.id); // Logge die Song-ID zur Überprüfung
    },
    // Zeigt den Editor für einen neuen Song an
    showNewSongEditor() {
      this.currentSong = {title: '', artist: '', genre: '', length: 0}; // Leeres Objekt für neuen Song
      this.isEdit = false; // Setze den Bearbeitungsmodus auf "neu"
      this.editorVisible = true; // Zeige den Editor an
    },
    // Löscht einen Song und aktualisiert die Liste
    deleteSong(id) {
      if (confirm('Are you sure you want to delete this song?')) {
        axios.delete(`http://localhost:8082/api/songs/${id}`)
            .then(() => {
          this.fetchSongs(); // Aktualisiere die Songliste
        })
            .catch(error => {
              console.error('Error deleting song', error);
            });
      }
    },
    closeEditor() {
      this.editorVisible = false; // Editor ausblenden
      this.currentSong = { title: '', artist: '', genre: '', length: 0 }; // Felder leeren
    },
  }
};
</script>
