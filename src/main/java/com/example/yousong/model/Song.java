package com.example.yousong.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;


@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    @NotNull(message = "Artist is required")
    private Artist artist;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    @Min(value = 1, message = "Length must be at least 1 second")
    private int length;

    //------ 9 ------
    //automatisch von Spring Data JPA verwaltet und verwendet, um Versionskonflikte zu erkennen
    @Version
    private Integer version;

    // Getter und Setter
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    //----- 8 -------
    @Lob
    private String audioData;  //Feld für die Audiodaten (Datenbank)

    public Song() {}

    //----- 8 anpassen -------
    public Song(String title, Artist artist, String genre, int length, String audioData) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.length = length;
        this.audioData = audioData;
    }

    //----- 8 ------- Musikdaten werden in der Datenbank gespeichert
    public String getAudioData() {
        return audioData;
    }

    public void setAudioData(String audioData) {
        this.audioData = audioData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
