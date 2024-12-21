package com.example.yousong.controller;

import com.example.yousong.exception.ResourceNotFoundException;
import com.example.yousong.model.Artist;
import com.example.yousong.model.Song;
import com.example.yousong.projection.SongProjection;
import com.example.yousong.repository.ArtistRepository;
import com.example.yousong.repository.SongRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController // Markiert Klasse als Controller für REST-APIs
@CrossOrigin(origins = "*")  // Für Cross-Origin-Zugriffe
public class SongController {

    private final SongRepository songRepository;
    //-------- 8 ---------
    private final ArtistRepository artistRepository;

    public SongController(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    //----- 8 -------!!!!
        // Methode zum Hochladen eines Songs mit Audiodatei
    @PostMapping(value = "/api/songs/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Song> uploadSong( //konvertiert Datei in Base64 und speichert diese im Song-Objekt
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam("genre") String genre,
            @RequestParam("length") int length,
            @RequestParam("audioFile") MultipartFile audioFile) {
        try {
            // Überprüfe, ob die Datei leer ist
            if (audioFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // MIME-Typ Überprüfung
            String contentType = audioFile.getContentType();
            if (!contentType.startsWith("audio/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            // Datei in Base64-String umwandeln
            byte[] audioDataBytes = audioFile.getBytes();
            String audioDataBase64 = Base64.getEncoder().encodeToString(audioDataBytes);

            // Artist und Song speichern
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
            Song song = new Song(title, artist, genre, length, audioDataBase64);
            songRepository.save(song);

            return ResponseEntity.status(HttpStatus.CREATED).body(song);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //Daten aus Repository mit Projection abrufen und an Client senden
    @GetMapping("/api/songs/projection")
    public ResponseEntity<Page<SongProjection>> getAllSongsProjection(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SongProjection> songs = songRepository.findAllProjectedBy(pageable);
        return ResponseEntity.ok(songs);
    }


    @GetMapping("/api/songs/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> {
                    song.setAudioData(null); // Entfernt die Audiodaten aus der Antwort
                    return ResponseEntity.ok(song);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Hinzufügen einer Methode, um nur Audiodaten zu erhalten
    @GetMapping(value = "/api/songs/{id}/audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getSongAudio(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> {
                    if (song.getAudioData() == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    }

                    byte[] audioData = Base64.getDecoder().decode(song.getAudioData());  // Decode Base64
                    return ResponseEntity.ok(audioData);  // Gebe die Audiodaten als byte[] zurück
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/api/songs")
    public ResponseEntity<Page<SongProjection>> getAllSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SongProjection> songs = songRepository.findAllProjectedBy(pageable);
        return ResponseEntity.ok(songs);
    }


    //----neue Songs erstellen----
    // CREATE
    @PostMapping("/api/songs")
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) {
        if (song.getArtist() == null || song.getArtist().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(songRepository.save(song));
    }


    //----vorhandene Songs bearbeiten----
    // UPDATE ---hier was ändern
    @PutMapping(value = "/api/songs/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSong(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam("genre") String genre,
            @RequestParam("length") int length,
            @RequestParam(/*value = */"audioFile"/*, required = true*/) MultipartFile audioFile) {

        System.out.println("Gerade wrude eiene audio bearbeitet");

        Logger logger = LoggerFactory.getLogger(SongController.class);

        try {
            // Song suchen
            Song song = songRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Song not found with ID: " + id));

            // Song-Daten aktualisieren
            song.setTitle(title);
            song.setGenre(genre);
            song.setLength(length);

            // Künstler setzen
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));
            song.setArtist(artist);

            // Audiodatei aktualisieren (falls übergeben)
            if (audioFile != null && !audioFile.isEmpty()) {
                // MIME-Typ überprüfen
                String contentType = audioFile.getContentType();
                if (contentType == null || !contentType.startsWith("audio/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Unsupported file type. Please upload an audio file.");
                }

                // Datei in Base64-String umwandeln und speichern
                byte[] audioDataBytes = audioFile.getBytes();
                String audioDataBase64 = Base64.getEncoder().encodeToString(audioDataBytes);

                logger.info("audioDataBase64" + audioDataBase64);

                song.setAudioData(audioDataBase64);
            } else {
                // Keine neue Datei: Behalte die vorhandenen Audiodaten
                if (song.getAudioData() == null || song.getAudioData().isEmpty()) {
                    logger.warn("No audio file provided and no existing audio data found.");
                } else {
                    logger.info("No new audio file provided. Existing audio data will remain unchanged.");
                }
            }

            // Song speichern
            songRepository.save(song);
            return ResponseEntity.ok(song);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the audio file: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    //----Löschen von Songs----
    // DELETE
    @DeleteMapping("/api/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //---04--Neuer Endpunkt für die Suche nach Songs----
    @GetMapping("/api/songs/search")
    public ResponseEntity<List<SongProjection>> searchSongs(@RequestParam(required = false) String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Suche nach Titel oder Künstler mit Projections
        List<SongProjection> result = songRepository
                .findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCase(query, query);

        return ResponseEntity.ok(result);
    }

}

