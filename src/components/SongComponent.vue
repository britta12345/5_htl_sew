<template>
  <div class="song">
    <h3>{{ song.title }}</h3>
    <p>Artist: {{ song.artist ? song.artist.name : 'Unknown' }}</p>
    <p>Genre: {{ song.genre }}</p>
    <p>Length: {{ song.length }} seconds</p>
ddddddd
    <!--  8-------->
    <!-- Audio-Komponente, die auf den geladenen Audio-Blob zugreift -->
    <audio v-if="audioUrl" :src="audioUrl" controls></audio>

    <button @click="playSong">{{ isPlaying ? 'Pause' : 'Play' }}AAAA</button>
sdfsfdsf
    <button @click="$emit('edit')">Edit</button>
    <button @click="$emit('delete')">Delete</button>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted  } from 'vue';
import axios from 'axios';
//----------8 ----------
export default {
  props: {
    song: Object,
    globalAudioPlayer: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const audioUrl = ref(null);
    const isPlaying = ref(false);
    let audio = null;

    // Funktion -> Laden der Audiodatei von der API
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

    const playSong = () => {
      console.log("song is playing");
      if (!audioUrl.value) return;

      // Wenn ein anderes Audio läuft, stoppe es
      if (props.globalAudioPlayer.audio && props.globalAudioPlayer.audio !== audio) {
        console.log("eine andere audio läuft jetzt");
        props.globalAudioPlayer.audio.pause(); // Pause das andere Audio
        props.globalAudioPlayer.audio.currentTime = 0; // Setze die Zeit zurück
        props.globalAudioPlayer.isPlaying = false; // Setze den globalen Zustand zurück
      }

      // Audio Instanz erstellen, falls nicht bereits vorhanden
      if (!audio) {
        audio = new Audio(audioUrl.value);
        audio.onended = () => {
          props.globalAudioPlayer.isPlaying = false; // Audio ist zu Ende
          isPlaying.value = false; // Setze den Status auf 'nicht spielen'
        };
      }
      // Wiedergabe oder Pause
      if (isPlaying.value) {
        audio.pause();
      } else {
        audio.play();
      }

      // Setze den globalen Zustand
      props.globalAudioPlayer.audio = audio;
      props.globalAudioPlayer.isPlaying = !isPlaying.value;
      isPlaying.value = !isPlaying.value;
    };


    onMounted(() => {
      loadAudio(props.song.id);
    });

    onUnmounted(() => {
      if (audioUrl.value) {
        URL.revokeObjectURL(audioUrl.value);
      }
    });


    return {
      isPlaying,
      playSong
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
