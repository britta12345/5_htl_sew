<template>
  <div class="song">
    <h3>{{ song.title }}</h3>
    <p>Artist: {{ song.artist ? song.artist.name : 'Unknown' }}</p>
    <p>Genre: {{ song.genre }}</p>
    <p>Length: {{ song.length }} seconds</p>

    <!-- Audio-Komponente, die auf den geladenen Audio-Blob zugreift -->
    <audio v-if="audioUrl" ref="audioPlayer" :src="audioUrl" controls></audio>

    <!-- Button für Play/Pause -->
    <button @click="playSong">{{ isPlaying ? 'Pause' : 'Play' }}</button>

    <button @click="$emit('edit')">Edit</button>
    <button @click="$emit('delete')">Delete</button>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { globalAudioPlayer } from '../globalAudioPlayer.js'; // Importiere den globalen Audio-Player

export default {
  props: {
    song: Object,
  },

  setup(props) {
    const audioUrl = ref(null);
    const isPlaying = ref(false);

    // Beobachte den globalen Zustand und aktualisiere die lokale Anzeige
    watch(
        () => globalAudioPlayer.currentSongId,
        (newId) => {
          isPlaying.value = newId === props.song.id && globalAudioPlayer.isPlaying;
        }
    );

    // Lädt den Song und das Audio, wenn die Komponente geladen wird
    onMounted(() => {
      if (props.song && props.song.id) {
        loadAudio(props.song.id);
      }
    });

    // Lädt den Audio-Blob vom Server
    const loadAudio = async (songId) => {
      try {
        const response = await axios.get(`http://localhost:8082/api/songs/${songId}/audio`, {
          responseType: 'blob',
        });
        audioUrl.value = URL.createObjectURL(response.data); // Blob URL für den Audio-Player
      } catch (error) {
        console.error('Error loading audio:', error);
      }
    };

    // Funktion zum Abspielen/Pausieren des Songs
    const playSong = () => {
      if (!audioUrl.value) return; // Wenn keine URL für das Audio existiert, breche ab

      // Wenn der globale Player ein anderes Audio abspielt, stoppen
      if (globalAudioPlayer.audio && globalAudioPlayer.audio.src !== audioUrl.value) {
        globalAudioPlayer.audio.pause();
        globalAudioPlayer.audio.currentTime = 0;
        globalAudioPlayer.isPlaying = false;
      }

      // Wenn ein neuer Song abgespielt wird, initialisiere den Audio-Player
      if (!globalAudioPlayer.audio || globalAudioPlayer.audio.src !== audioUrl.value) {
        globalAudioPlayer.audio = new Audio(audioUrl.value);
        globalAudioPlayer.audio.onended = () => {
          globalAudioPlayer.isPlaying = false; // Wenn der Song endet, setze Zustand zurück
          globalAudioPlayer.currentSongId = null;
          isPlaying.value = false;
        };
      }

      // Umschalten zwischen Play und Pause
      if (isPlaying.value) {
        // Song pausieren
        globalAudioPlayer.audio.pause();
        globalAudioPlayer.isPlaying = false;
        isPlaying.value = false;
      } else {
        // Song abspielen
        globalAudioPlayer.audio.play();
        globalAudioPlayer.isPlaying = true;
        isPlaying.value = true;

        // Setze den aktuellen Song im globalen Zustand
        globalAudioPlayer.currentSongId = props.song.id;
      }
    };


    return {
      audioUrl,
      isPlaying,
      playSong,
    };
  },
};
</script>
