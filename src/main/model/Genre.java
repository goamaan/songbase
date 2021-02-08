package model;

import java.util.ArrayList;
import java.util.List;

// Represents a Genre having a list of songs
public class Genre {

    private String name;
    private final List<Song> songsInGenre;

    // EFFECTS: constructs a genre with list to store list of songs in it
    public Genre(String name) {
        songsInGenre = new ArrayList<>();
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: adds a song to the list of songs
    public void addSong(Song song) {
        songsInGenre.add(song);
    }

    // REQUIRES: song must be in genre already
    // MODIFIES: this
    // EFFECTS: removes a song from the list of songs provided the song
    public void removeSong(Song song) {
        songsInGenre.remove(song);
    }

    // MODIFIES: this
    // EFFECTS: removes a song from the list of songs provided the name of the song
    public void removeSong(String song) { // OVERLOADED METHOD
        songsInGenre.removeIf(s -> song.equals(s.getName()));
    }

    // EFFECTS: returns the list of songs in this genre
    public List<Song> getSongsInGenre() {
        return songsInGenre;
    }

    // EFFECTS: returns the name of the genre
    public String getName() {
        return name;
    }

    // EFFECTS: returns number of songs in genre
    public int getNumberOfSongsInGenre() {
        return songsInGenre.size();
    }

}
