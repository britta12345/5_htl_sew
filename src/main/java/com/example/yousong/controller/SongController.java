package com.example.yousong.controller;

import com.example.yousong.exception.ResourceNotFoundException;
import com.example.yousong.model.Artist;
import com.example.yousong.model.Song;
import com.example.yousong.projection.SongProjection;
import com.example.yousong.repository.ArtistRepository;
import com.example.yousong.repository.SongRepository;
import jakarta.validation.Valid;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin
public class SongController {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    public SongController(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @PostMapping(value = "/api/songs/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Song> uploadSong(
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam("length") int length,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("genres") List<String> genres) {
        try {
            if (audioFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            String contentType = audioFile.getContentType();
            if (!contentType.startsWith("audio/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            byte[] audioDataBytes = audioFile.getBytes();
            String audioDataBase64 = Base64.getEncoder().encodeToString(audioDataBytes);

            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
            Song song = new Song(title, artist, length, audioDataBase64, genres);
            songRepository.save(song);

            return ResponseEntity.status(HttpStatus.CREATED).body(song);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/songs")
    public ResponseEntity<Song> createSong(@Valid @RequestBody Song song) {
        if (song.getArtist() == null || song.getArtist().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(songRepository.save(song));
    }

    @PutMapping(value = "/api/songs/{id}")
    public ResponseEntity<?> updateSong(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("artistId") Long artistId,
            @RequestParam("length") int length,
//            @RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam("version") Integer version,
            @RequestParam("genres") List<String> genres
    ) {


        try {
            Song song = songRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Song not found with ID: " + id));

            //------ 9 Concurrent Updates ------
            // **WICHTIG:** Versionsprüfung!
            // vergleicht die im Request empfangene Version
            // mit der aktuellen Version des Songs in der Datenbank
            // wenn sie nicht übereinstimmen -> 409 Conflict Statuscode mit Fehlermeldung
            if (!song.getVersion().equals(version)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Version mismatch. The song has been updated by another user. Please refresh the song to get the latest version.");
            }

            song.setTitle(title);
            song.setLength(length);
            song.setGenres(genres);

            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));
            song.setArtist(artist);

//            if (audioFile != null && !audioFile.isEmpty()) {
//                String contentType = audioFile.getContentType();
//                if (contentType == null || !contentType.startsWith("audio/")) {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                            .body("Unsupported file type. Please upload an audio file.");
//                }
//
//                byte[] audioDataBytes = audioFile.getBytes();
//                String audioDataBase64 = Base64.getEncoder().encodeToString(audioDataBytes);
//                song.setAudioData(audioDataBase64);
//            }

            //------ 9 Concurrent Updates ------
            // **WICHTIG:** OptimisticLockingFailureException
            // songRepository.save(song) Methode wirft OptimisticLockingFailureException,
            // wenn Version nicht stimmt (zb jemand anderes hat Song in der Zwischenzeit geändert)
            // Exception fangen und 409 Conflict zurückgeben
            songRepository.save(song);
            return ResponseEntity.ok(song);

        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Optimistic locking failure. The song has been updated by another user. Please refresh the song to get the latest version.");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error processing the audio file: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //songs anzeigen
    /*@GetMapping("/api/songs")
    public ResponseEntity<List<SongProjection>> getAllSongs() {
        List<SongProjection> songs = songRepository.findAllProjectedBy();
        return ResponseEntity.ok(songs);
    }*/

    //alte mathode:
    /*@GetMapping("/api/songs/search")
    public ResponseEntity<List<SongProjection>> searchSongs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> genres) {

        List<SongProjection> result = null;

        if (query != null && !query.isBlank()) {
            result = songRepository.findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCase(query, query);
        }

        if (genres != null && !genres.isEmpty()) {
            //Suche nach Songs, die *mindestens eines* der angegebenen Genres haben
            List<Song> genreSongs = genres.stream()
                    .flatMap(genre -> songRepository.findByGenresContaining(genre).stream())
                    .distinct() // Duplikate entfernen
                    .collect(Collectors.toList());

            if (result == null) {
                result = genreSongs.stream().map(song -> (SongProjection) song).collect(Collectors.toList());
            } else {
                //Kombiniere die Ergebnisse von Titel/Künstlersuche und Genrestuche
                List<SongProjection> genreSongProjections = genreSongs.stream().map(song -> (SongProjection) song).collect(Collectors.toList());
                result = result.stream().distinct().collect(Collectors.toList());
            }
        }

        if (result == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(result);
    }*/

    //neue methode:
    @GetMapping("/api/songs/search")
    public ResponseEntity<List<SongProjection>> searchSongs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> genres) {

        List<SongProjection> result = null;

        System.out.println(genres);

        if ((query != null && !query.isBlank()) || (genres != null && !genres.isEmpty())) {
            result = songRepository.findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCaseOrGenresIn(
                    query != null ? query.toLowerCase() : "%",  //Sicherstellen, dass query nicht null ist
                    query != null ? query.toLowerCase() : "%",  //Sicherstellen, dass query nicht null ist
                    genres != null ? genres : List.of() //Sicherstellen, dass genres nicht null ist
                    //  genres != null ? genres.stream().map(String::toLowerCase).collect(Collectors.toList()) : List.of() //Sicherstellen, dass genres nicht null ist
            );
        }

        if (result == null) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(result);
    }
}