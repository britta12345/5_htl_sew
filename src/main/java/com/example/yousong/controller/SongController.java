package com.example.yousong.controller;

import com.example.yousong.exception.ResourceNotFoundException;
import com.example.yousong.model.Artist;
import com.example.yousong.model.Song;
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

    //----- 8 -------
        // Methode zum Hochladen eines Songs mit Audiodatei
    @PostMapping(value = "/api/songs/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Song> uploadSong(
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


    @GetMapping("/api/songs/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> {
                    song.setAudioData(null); // Entfernt Audiodaten aus der Antwort
                    return ResponseEntity.ok(song);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Hinzufügen einer Methode, um nur Audiodaten zu erhalten
    @GetMapping(value = "/api/songs/{id}/audio", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getSongAudio(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(song -> {
                    byte[] audioData = Base64.getDecoder().decode(song.getAudioData());  // Decode Base64
                    return ResponseEntity.ok(audioData);  // Gebe die Audiodaten als byte[] zurück
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //-----------


    @GetMapping("/api/songs")
    public ResponseEntity<Page<Song>> getAllSongs(
            // Liest Seitenparameter aus der Anfrage
            // kein Wert angegeben ->  Seite auf 0 (die erste Seite) gesetzt
            @RequestParam(defaultValue = "0") int page,
            // Liest Anzahl der Songs pro Seite -> Standardmäßig 5
            @RequestParam(defaultValue = "5") int size) {
        // Pageable-Objekt wird erstellt (aktuelle Seite + Anzahl der Elemente pro Seite)
        Pageable pageable = PageRequest.of(page, size);
        // mit Pageable-Objekt (Songs von der Datenbank abgerufen)
        // Rückgabe = Page<Song>  enthält (aktuellen Songs/Paginierungsinformationen)
        Page<Song> songs = songRepository.findAll(pageable);
        // Gibt Seite mit Songs zurück (verpackt in ein ResponseEntity) -> enthält (HTTP-Status + Daten)
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
    @PutMapping(value ="/api/songs/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Song> updateSong(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam("genre") String genre,
            @RequestParam("length") int length,
            @RequestParam("audioFile") MultipartFile audioFile) {
            //@RequestBody Song updatedSong

        System.out.println("Received PUT request to update song with ID: " + id);

        // Prüfen, ob ID korrekt übergeben wurde
        if (id == null) {
            System.out.println("Error: Song ID is missing in the request.");
            return ResponseEntity.badRequest().build();
        }

        return songRepository.findById(id)
                .map(song -> {
                    // Titel, Künstler, Genre und Länge aktualisieren
                   song.setTitle(title);

                    Artist artist = artistRepository.findById(artistId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
                    song.setArtist(artist);
                    song.setGenre(genre);
                    song.setLength(length);

                    // Prüfen, ob Audiodaten im updatedSong übergeben wurden
                    if (audioFile != null && !audioFile.isEmpty()) {
                        try {
                            String contentType = audioFile.getContentType();
                            if (!contentType.startsWith("audio/")) {
                                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
                            }

                            //datei in base 64 string umwandeln
                           // byte[] audioDataBytes = audioFile.getBytes();
                            String audioDataBase64 = ""; //Base64.getEncoder().encodeToString(audioDataBytes);
                            song.setAudioData(audioDataBase64);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    }

                    // Speichern der Änderungen
                   songRepository.save(song);

                    return ResponseEntity.ok(song);
                })
                .orElse(ResponseEntity.notFound().build());
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
    @GetMapping("/search")
    public ResponseEntity<List<Song>> searchSongs(@RequestParam(required = false) String query) {
        Logger logger = LoggerFactory.getLogger(getClass());

        // Log die Suchparameter
        logger.debug("Searching for songs with title: {} and artist name: {}", query);

        try {
            List<Song> result;

            result = songRepository.findByTitleContainingIgnoreCaseOrArtistNameContainingIgnoreCase(query, query);

            logger.debug("Number of songs found: {}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error occurred while searching for songs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

