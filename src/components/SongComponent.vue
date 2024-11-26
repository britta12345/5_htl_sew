<template>
  <div class="song">
    <h3>{{ song.title }}</h3>
    <p>Artist: {{ song.artist ? song.artist.name : 'Unknown' }}</p>
    <p>Genre: {{ song.genre }}</p>
    <p>Length: {{ song.length }} seconds</p>

    <!--  8-------->
    <!-- Audio-Komponente, die auf den geladenen Audio-Blob zugreift -->
    <audio v-if="audioUrl" :src="audioUrl" controls></audio>

    <button @click="$emit('edit')">Edit</button>
    <button @click="$emit('delete')">Delete</button>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
//----------8 ----------
export default {
  props: {
    song: Object
  },
  setup(props) {
    const audioUrl = ref(null);

    // Funktion zum Laden der Audiodatei von der API
    const loadAudio = async (songId) => {
      try {
        const response = await axios.get(`http://localhost:8082/api/songs/${songId}/audio`, {
          responseType: 'blob'  // Holen der Audiodaten als Blob
        });
        // URL für das Audio erstellen
        audioUrl.value = URL.createObjectURL(response.data);  // Blob URL für den Audio-Player
      } catch (error) {
        console.error("Error loading audio:", error);
      }
    };


    // Audiodatei laden, wenn der Component geladen wird
    onMounted(() => {
      if (props.song && props.song.id) {
        loadAudio(props.song.id);  // Lädt die Audiodaten beim Laden der Komponente
      }

      // Wenn im Editiermodus, dann das Audio auch laden
      if (props.isEdit && props.song.audioData) {
        audioUrl.value = URL.createObjectURL(new Blob([new Uint8Array(atob(props.song.audioData).split("").map(function(c) { return c.charCodeAt(0); }))]));
      }
    });



    return {
      audioUrl
    };
  }
};
</script>

<style scoped>
.song {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px 0;
}
</style>
