package com.example.yousong.repository;

import com.example.yousong.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Datenbankzugriffsschicht
// Repository stellt CRUD-Operationen für Künstler bereit + Suche nach Künstlern

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
}
