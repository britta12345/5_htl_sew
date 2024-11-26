import { createStore } from 'vuex';
import axios from 'axios';

export const store = createStore({
    state: {
        artists: []
    },
    mutations: {
        setArtists(state, artists) {
            console.log('Setting artists in store:', artists); // Debugging
            state.artists = artists;
        }
    },
    actions: {
        async fetchArtists({ commit }) {
            const response = await axios.get('http://localhost:8082/api/artists');
            console.log('Fetched artists:', response.data); // Debugging
            commit('setArtists', response.data);
        }
    }
});
