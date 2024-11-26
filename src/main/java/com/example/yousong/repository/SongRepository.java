package com.example.yousong.repository;

import com.example.yousong.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

// REST Repository mit Spring Data JPA

public interface SongRepository extends JpaRepository<Song, Long> {

    // Suche nach Titel
    List<Song> findByTitleContainingIgnoreCase(String title);

    // Suche nach Künstler
    List<Song> findByArtist_NameContainingIgnoreCase(String artistName);


    //Methode zum Suchen nach Titel oder Künstler
    List<Song> findByTitleContainingIgnoreCaseOrArtistNameContainingIgnoreCase(String title, String artistName);

    //  --------- 6. paginierte Ergebnisse ---------
    // ermöglicht Liste von Song-Objekten aus Datenbank abzurufen
    // Pageable-Objekt -> um Anzahl zu bekommen (Seite zu steuern)
    Page<Song> findAll(Pageable pageable);
}


// ja es ist einen ----name basded--- suche -> der name sagt es aus wonach gesucht wird
// wenn es anders heissen wuerde wuerde es ned danach suchen yk
/*
Spring Boot Annotations: Metadaten, um Funktionen wie REST-APIs zu aktivieren (z.B. @RestController).
CRUD: Verwaltung von Daten über HTTP-Methoden (Create: POST, Read: GET, Update: PUT, Delete: DELETE).
Spring Boot: Framework für die einfache Entwicklung von Java-Anwendungen.
Query Methods: Automatisch generierte Datenbankabfragen durch methodenbasierte Konventionen.
REST Repository: Ein Repository, das Datenbankoperationen für REST-APIs bereitstellt.*/