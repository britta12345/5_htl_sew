package com.example.yousong.projection;

// ------8-------
public interface SongProjection {
    Long getId();
    String getTitle();
    String getGenres();
    int getLength();

    interface ArtistInfo {
        Long getId();
        String getName();
    }

    ArtistInfo getArtist();
}
