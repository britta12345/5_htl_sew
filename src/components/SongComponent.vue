<template>
  <div class="song">
    <h3>{{ song.title }}</h3>
    <p>Artist: {{ song.artist ? song.artist.name : 'Unknown' }}</p>
    <p>Genre: {{ song.genre }}</p>
    <p>Length: {{ song.length }} seconds</p>
    <button @click="$emit('edit')">Edit</button>
    <button @click="$emit('delete')">Delete</button>
  </div>
</template>

<script>
export default {
  props: {
    song: Object
  }
}
</script>

<style scoped>
.song {
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px 0;
}
</style>
