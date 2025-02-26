package com.example.yousong.repository;

import com.example.yousong.model.Song;
import com.example.yousong.projection.SongProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<SongProjection> findProjectedByTitleContainingIgnoreCase(String title);

    List<SongProjection> findProjectedByArtist_NameContainingIgnoreCase(String artistName);

    //List<SongProjection> findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCase(
      //      String title, String artistName);

    //alte methode zum suchen:
    /*@Query("SELECT s FROM Song s WHERE " +
            "LOWER(s.title) LIKE %:title% OR " +
            "LOWER(s.artist.name) LIKE %:artistName% OR " +
            "EXISTS (SELECT g FROM s.genres g WHERE LOWER(g) LIKE %:title%)")
    List<SongProjection> findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCase(
            @Param("title") String title, @Param("artistName") String artistName);*/

    //neue Methode zum suchen:
    @Query("SELECT DISTINCT s FROM Song s JOIN s.genres g " +
            "WHERE LOWER(s.title) LIKE %:title% " +
            "OR LOWER(s.artist.name) LIKE %:artistName% " +
            "OR g IN :genres")
    List<SongProjection> findProjectedByTitleContainingIgnoreCaseOrArtist_NameContainingIgnoreCaseOrGenresIn(
            @Param("title") String title,
            @Param("artistName") String artistName,
            @Param("genres") List<String> genres);

    Page<SongProjection> findAllProjectedBy(Pageable pageable);

    //------ 11 Collections
    @Query("SELECT s FROM Song s WHERE :genre MEMBER OF s.genres")
    List<Song> findByGenresContaining(@Param("genre") String genre);

    // neue Methode
    //List<SongProjection> findAllProjectedBy();
}