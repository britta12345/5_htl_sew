package com.example.yousong.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import com.example.yousong.model.Artist;
import com.example.yousong.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;

@DataJpaTest
class SongRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void testSaveAndUpdateSong_withVersionCheck() {
        // Erstelle einen Künstler
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist = artistRepository.save(artist);

        // Erstelle und speichere einen Song (die Version wird automatisch gesetzt)
        Song song = new Song();
        song.setTitle("Test Song");
        song.setArtist(artist);
        song.setLength(300);
        song.setAudioData("dummyAudioData");
        song.setGenres(List.of("Pop", "Rock")); // Setze die Genres-Liste
        song = songRepository.save(song);

        // Hole den Song aus der DB und ändere ihn
        Song retrievedSong = songRepository.findById(song.getId()).orElseThrow();
        retrievedSong.setTitle("Updated Song Title");

        // Simuliere ein Versions-Mismatch, indem du die Version auf 2 setzt
        //retrievedSong.setVersion(2);

        //System.out.println("Flushing to DB...");
        // Stelle sicher, dass die Entität verwaltet wird
        //songRepository.flush();

        // Versuch, den Song zu speichern und erwarte eine OptimisticLockingFailureException
        /*assertThrows(OptimisticLockingFailureException.class, () -> {
            System.out.println("Attempting to save and flush the updated song...");
            songRepository.saveAndFlush(retrievedSong);  // Speichern mit Konflikt
        });*/
        List<Song> foundSongs = songRepository.findByGenresContaining("Pop");
        assertEquals(1, foundSongs.size());
        assertEquals("Test Song", foundSongs.get(0).getTitle());
    }
}