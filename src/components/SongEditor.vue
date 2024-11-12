<template>
  <!-- 7 ------ Validierung auf der Client-Seite ----->

  <div>
    <!-- Song Editor Form -->
    <h2>{{ isEdit ? 'Edit Song' : 'Create a New Song' }}</h2>
    <form @submit.prevent="submitSong">

      <!-- Title Eingabe -->
      <input v-model="localSong.title" placeholder="Title" required />
      <span v-if="v$.localSong.title.$pending">Validating...</span>
      <span v-if="!v$.localSong.title.$pending && v$.localSong.title.$invalid" class="error-message">Du brauchst einen Titel!</span>

      <!-- Dropdown for Artist Selection -->
      <select v-model="localSong.artistId" required>
        <option value="" disabled>Select Artist</option>
        <option v-for="artist in artists" :key="artist.id" :value="artist.id">
          {{ artist.name }}
        </option>
      </select>
      <span v-if="v$.localSong.artistId.$invalid" class="error-message">Du musst einen Artist auswählen!</span>

      <!-- Genre Eingabe -->
      <input v-model="localSong.genre" placeholder="Genre" required />
      <span v-if="!v$.localSong.genre.$pending && v$.localSong.genre.$invalid" class="error-message">Du brauchst ein Genre!</span>

      <!-- Länge Eingabe -->
      <input v-model="localSong.length" placeholder="Length" type="number" required />
      <!-- Validierungsstatus anzeigen -->
      <span v-if="v$.localSong.length.$pending">Validating...</span>
      <span v-if="!v$.localSong.length.$pending && v$.localSong.length.$invalid" class="error-message">Du brauchst eine Länge!</span>
      <span v-if="localSong.length <= 0 && !v$.localSong.length.$pending" class="error-message">Die Länge muss mindestens 1 Sekunde sein!</span>


      <button type="submit">{{ isEdit ? 'Save Changes' : 'Add Song' }}</button>
    </form>

    <!-- Fehlernachricht für Song -->
    <div v-if="message" class="error-message">
      {{ message }}
    </div>

    <!-- New Artist Form -->
    <h2>Add New Artist</h2>
    <form @submit.prevent="addArtist">
      <input v-model="newArtistName" placeholder="Artist Name" required />
      <!--<span v-if="v$.newArtist.name.$pending">Validating...</span>
      <span v-if="!v$.newArtist.name.$pending && v$.newArtist.name.$invalid" class="error-message">Du brauchst einen Artist Namen!</span>-->
      <span v-if="v$.newArtistName.$pending">Validating...</span>
      <span v-if="!v$.newArtistName.$pending && v$.newArtistName.$invalid" class="error-message">Du brauchst einen Artist Namen!</span>

      <!-- Submit-Button -->
      <button type="submit" :disabled="v$.newArtistName.$invalid">Add Artist</button>
    </form>

    <!-- Fehlernachricht für Artist -->
    <div v-if="artistMessage" class="error-message">
      {{ artistMessage }}
    </div>

    <!-- Artist List for Editing/Deleting -->
    <h2>Edit or Delete Artists</h2>
    <ul>
      <li v-for="artist in artists" :key="artist.id">
        <div v-if="editedArtistId === artist.id">
          <input v-model="editedArtistName" />
          <button @click="saveArtistEdit(artist.id)">Save</button>
          <button @click="cancelEdit">Cancel</button>
        </div>
        <div v-else>
          {{ artist.name }}
          <button @click="enterEditMode(artist)">Edit</button>
          <button @click="deleteArtist(artist.id)">Delete</button>
        </div>
      </li>
    </ul>

    <!--<div v-if="artistMessage">{{ artistMessage }}</div>
    <div v-if="message">{{ message }}</div>-->

    <!-- Erfolgs- oder Fehlermeldung -->
    <!--<div v-if="message">{{ message }}</div>-->
  </div>
</template>


<script>
import axios from 'axios';
import { required, minLength } from '@vuelidate/validators';
import { useVuelidate } from '@vuelidate/core';
import { ref, onMounted } from 'vue';
//import { ref, onMounted, watch } from 'vue';

export default {
  props: {
    song: {
      type: Object,
      default: () => ({ title: '', artistId: '', genre: '', length: 0 })
    },
    isEdit: Boolean
  },
  setup(props, { emit }) {
    const localSong = ref({ ...props.song, artistId: props.song.artistId || '' });
    const newArtistName = ref('');
    const artists = ref([]);
    const editedArtistId = ref(null);
    const editedArtistName = ref('');
    const message = ref('');
    const artistMessage = ref('');
    // Validierung für artistId hinzufügen
    const v$ = useVuelidate(
        {
      localSong: {
        title: { required },
        genre: { required },
        length: { required, min: minLength(1) },
        artistId: { required }
      },
      /*newArtist: { // Objekt für den Künstler
        name: { required, minLength: minLength(1) }
      }*/
      newArtistName: { required, minLength: minLength(1) },

    },
        {
          localSong, /*newArtist: { name:*/
          newArtistName/*}*/
        }
    );

    console.log('newArtistName', newArtistName);
    console.log('v$ object:', v$);
    //console.log('newArtistName validation:', v$.newArtistName);

    const fetchArtists = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/artists');
        artists.value = response.data;
      } catch (error) {
        console.error('Error fetching artists:', error);
      }
    };

    const addArtist = async () => {
      // console.log('Is newArtistName valid?', v$.newArtistName.$invalid);
      // if (v$.newArtistName.$invalid) {
      //   artistMessage.value = 'Please provide a valid artist name!';
      //   return;
      // }
      console.log("test")
      try {
        const response = await axios.post('http://localhost:8082/api/artists', { name: newArtistName.value });
        console.log('newArtistName.value: ', newArtistName.value);
        artistMessage.value = 'Artist added successfully!';
        newArtistName.value = ''; // Reset the input field
        await fetchArtists();
        localSong.value.artistId = response.data.id;
      } catch (error) {
        artistMessage.value = 'Error adding artist:' + error.response.data.name ;
        console.log('Error adding artist:', error.response ? error.response.data : error.message);
      }
    };


    const submitSong = async () => {
      if (v$.$invalid) {
        message.value = 'Please fill in all required fields correctly!';
        return;
      }
      try {
        if (props.isEdit) {
          if (!localSong.value.id) throw new Error('Song ID is missing.');
          await axios.put(`http://localhost:8082/api/songs/${localSong.value.id}`, localSong.value);
        } else {
          await axios.post('http://localhost:8082/api/songs', localSong.value);
        }
        message.value = 'Song saved successfully!';
        emit('refreshSongs');
        emit('closeEditor');
      } catch (error) {
        // Wenn der Server eine Fehlerantwort sendet
        if (error.response && error.response.data) {
          // Fehlermeldungen vom Server anzeigen (falls sie als Liste vorliegen)
          message.value = error.response.data.errors ? error.response.data.errors.join(', ') : 'Error saving song: ' + error.response.data.error;
        } else {
          message.value = 'Error saving song: ' + (error.message || 'Unknown error');
        }
        console.error('Error saving song:', error.response ? error.response.data : error.message);
      }
    };


    const enterEditMode = (artist) => {
      editedArtistId.value = artist.id;
      editedArtistName.value = artist.name;
    };

    const saveArtistEdit = async (artistId) => {
      try {
        await axios.put(`http://localhost:8082/api/artists/${artistId}`, { name: editedArtistName.value });
        artistMessage.value = 'Artist updated successfully!';
        editedArtistId.value = null;
        editedArtistName.value = '';
        await fetchArtists();
      } catch (error) {
        artistMessage.value = 'Error updating artist.';
        console.error('Error updating artist:', error);
      }
    };

    const cancelEdit = () => {
      editedArtistId.value = null;
      editedArtistName.value = '';
    };

    const deleteArtist = async (artistId) => {
      try {
        await axios.delete(`http://localhost:8082/api/artists/${artistId}`);
        artistMessage.value = 'Artist deleted successfully!';
        await fetchArtists();
      } catch (error) {
        artistMessage.value = 'Error deleting artist.';
        console.error('Error deleting artist:', error);
      }
    };

    onMounted(fetchArtists);

    return {
      localSong,
      newArtistName,
      artists,
      editedArtistId,
      editedArtistName,
      message,
      artistMessage,
      v$,
      addArtist,
      submitSong,
      enterEditMode,
      saveArtistEdit,
      cancelEdit,
      deleteArtist
    };
  }
};
</script>

