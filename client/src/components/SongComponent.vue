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
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { globalAudioPlayer } from '../globalAudioPlayer.js'; // Importiere den globalen Audio-Player

export default {
  props: {
    song: Object
  },

  setup(props) {
    const audioUrl = ref(null);
    const isPlaying = ref(false);

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
        audioUrl.value = URL.createObjectURL(response.data);  // Blob URL für den Audio-Player
      } catch (error) {
        console.error("Error loading audio:", error);
      }
    };

    // Funktion zum Abspielen/Pausieren des Songs
    const playSong = () => {
      if (!audioUrl.value) return; // Wenn keine URL für das Audio existiert, breche ab

      // Wenn der globale Player ein anderes Audio abspielt, stoppen
      if (globalAudioPlayer.audio && globalAudioPlayer.audio !== audioUrl.value) {
        console.log("button klick aber andere audio läuft schon!");
        // Stoppe das aktuell laufende Audio und setze den Buttonzustand auf "Play"
        globalAudioPlayer.audio.pause();
        globalAudioPlayer.audio.currentTime = 0;
        globalAudioPlayer.isPlaying = false;

        // Setze den Buttonzustand des vorherigen Songs auf "Play"
        globalAudioPlayer.previousSong.isPlaying = false;
        globalAudioPlayer.previousSong.buttonText = "Play"; // Button zurücksetzen
      }

      // Wenn noch kein Audio vorhanden ist oder der Song geändert wurde, lade die neue URL
      if (!globalAudioPlayer.audio || globalAudioPlayer.audio.src !== audioUrl.value) {
        // Stelle sicher, dass der alte Player gestoppt wurde, bevor ein neuer Player erstellt wird
        if (globalAudioPlayer.audio) {
          globalAudioPlayer.audio.pause();
          globalAudioPlayer.audio.currentTime = 0;
        }

        globalAudioPlayer.audio = new Audio(audioUrl.value);
        globalAudioPlayer.audio.onended = () => {
          isPlaying.value = false; // Stoppe den Song, wenn er zu Ende ist
          globalAudioPlayer.isPlaying = false;
        };
      }

      // Starte oder pausiere den Song je nach aktuellem Zustand
      if (isPlaying.value) {
        globalAudioPlayer.audio.pause();
      } else {
        globalAudioPlayer.audio.play();
      }

      // Setze den globalen Zustand für das aktuelle Audio und den Button
      globalAudioPlayer.isPlaying = !isPlaying.value;
      isPlaying.value = !isPlaying.value;

      // Markiere diesen Song als aktuell abgespielt
      globalAudioPlayer.previousSong = {
        id: props.song.id,
        isPlaying: true,
        buttonText: "Pause",  // Setze den Buttontext für den aktuellen Song auf "Pause"
      };
    };

    return {
      audioUrl,
      isPlaying,
      playSong,
    };
  }
};
</script>
