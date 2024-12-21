import { reactive } from 'vue';

// Mach das Objekt reaktiv, damit Änderungen erkannt werden
export const globalAudioPlayer = reactive({
    audio: null, // Globale Referenz für den Audio-Player
    isPlaying: false, // Ob ein Song gerade abgespielt wird
    currentSongId: null, // ID des aktuell abgespielten Songs
});
