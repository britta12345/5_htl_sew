<template>
  <div>
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
    };
  },
  mounted() {
    this.fetchSongs();
  },
  methods: {
    async fetchSongs() {
      try {
        const response = await axios.get(`http://localhost:8082/api/songs`, {
          params: { page: this.currentPage, size: 5 },
        });

        this.songs = response.data.content;
        this.totalPages = response.data.totalPages;
      } catch (error) {
        console.error('Error fetching songs', error);
        this.songs = [];
      }
    },

    searchSongs() {
      if (this.searchQuery.trim() === '') {
        this.fetchSongs();
      } else {
        axios.get(`http://localhost:8082/api/songs/search`, {
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

    changePage(newPage) {
      if (newPage >= 0 && newPage < this.totalPages) {
        this.currentPage = newPage;
        this.fetchSongs();
      }
    },

    editSong(song) {
      //------ 9 Concurrent Updates ------
      // Übergib die Song-ID und die Version an den SongEditor
      this.currentSong = { ...song, artistId: song.artist.id }; // dass artistId korrekt gesetzt ist
      this.isEdit = true;
      this.editorVisible = true;
    },

    showNewSongEditor() {
      //------ 9 Concurrent Updates ------
      //version: null für neue Songs!
      //weil neue Songs noch keine Version haben
      this.currentSong = {title: '', artistId: '', genre: '', length: 0, version: null}; //Version hinzugefügt
      this.isEdit = false;
      this.editorVisible = true;
    },

    deleteSong(id) {
      if (confirm('Are you sure you want to delete this song?')) {
        axios.delete(`http://localhost:8082/api/songs/${id}`)
            .then(() => this.fetchSongs())
            .catch(error => {
              console.error('Error deleting song', error);
            });
      }
    },

    closeEditor() {
      this.editorVisible = false;
      this.currentSong = {title: '', artist: '', genre: '', length: 0};
    },
  }
};
</script>