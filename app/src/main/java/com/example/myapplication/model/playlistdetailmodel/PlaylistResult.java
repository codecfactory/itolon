
package com.example.myapplication.model.playlistdetailmodel;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistResult implements Serializable {

    @SerializedName("songs")
    @Expose
    private List<Song> songs = null;
    @SerializedName("artists")
    @Expose
    private List<Artist> artists = null;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

}
