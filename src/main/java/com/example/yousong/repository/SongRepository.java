package com.example.yousong.repository;

import com.example.yousong.model.Song;
import com.example.yousong.projection.SongProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

// REST Repository mit Spring Data JPA

public interface SongRepository extends JpaRepository<Song, Long> {

    // Suche nach Titel
    //List<Song> findByTitleContainingIgnoreCase(String title);
    // Suche nach Künstler
    //List<Song> findByArtist_NameContainingIgnoreCase(String artistName);
    //Methode zum Suchen nach Titel oder Künstler
    //List<Song> findByTitleContainingIgnoreCaseOrArtistNameContainingIgnoreCase(String title, String artistName);
    // Pageable-Objekt -> um Anzahl zu bekommen (Seite zu steuern)
    //Page<Song> findAll(Pageable pageable);

    //--------8.----------
    // Neue Methode, die die SongProjection zurückgibt
    //List<SongProjection> findAllProjectedBy();

    // Optional: Suche basierend auf Titel mit Projektion
    //List<SongProjection> findProjectedByTitleContainingIgnoreCase(String title);

    // Suche nach Titel mit Projections
    List<SongProjection> findProjectedByTitleContainingIgnoreCase(String title);

    // Suche nach Künstlername mit Projections
    List<SongProjection> findProjectedByArtist_NameContainingIgnoreCase(String artistName);

    // Kombination von Titel- und Künstlersuche (mit Projections)
    List<SongProjection> findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCase(
            String title, String artistName);

    // Paginierte Projektionen abrufen
    Page<SongProjection> findAllProjectedBy(Pageable pageable);
}
