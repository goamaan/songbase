package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a SongDatabase having a list of genre.
public class SongDatabase implements Writable {

    private final List<Genre> listOfGenre;

    // EFFECTS: constructs a SongDatabase with the provided list of genre
    public SongDatabase(List<Genre> listOfGenre) {
        this.listOfGenre = listOfGenre;
    }

    // EFFECTS: returns total number of songs in database
    public int getNumberOfSongs() {
        int sum = 0;
        for (Genre g : listOfGenre) {
            sum += g.getNumberOfSongsInGenre();
        }
        return sum;
    }

    // EFFECTS: returns all songs in database
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        for (Genre g : listOfGenre) {
            songs.addAll(g.getSongsInGenre());
        }
        return songs;
    }

    // EFFECTS: returns all songs by an artist (provided as string parameter).
    //          If no songs are found then returns empty list
    public List<Song> getSongsByArtist(String artist) {
        List<Song> allSongs = getAllSongs();
        List<Song> foundSongs = new ArrayList<>();
        for (Song s : allSongs) {
            if (s.getArtist().equals(artist)) {
                foundSongs.add(s);
            }
        }
        return foundSongs;
    }

    // MODIFIES: this
    // EFFECTS: deletes a song based on a user provided name of the song
    public void deleteSongBySearch(String song) {
        Genre toDeleteFrom = null;
        Song toDelete = null;
        for (Genre g : listOfGenre) {
            for (Song s : g.getSongsInGenre()) {
                if (s.getName().equals(song)) {
                    toDeleteFrom = g;
                    toDelete = s;
                }
            }
        }
        if (toDeleteFrom != null) {
            toDeleteFrom.removeSong(toDelete);
        }
    }

    public List<Genre> getListOfGenre() {
        return listOfGenre;
    }

    // MODIFIES: this
    // EFFECTS: returns a json object of the current song database
    @Override
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        for (Genre g : listOfGenre) {
            obj.put(g.getName(), genreToJson(g));
        }

        return obj;
    }

    // MODIFIES: this
    // EFFECTS: returns a json array of all the genre with songs in it
    private JSONArray genreToJson(Genre g) {
        JSONArray arr = new JSONArray();

        for (Song s : g.getSongsInGenre()) {
            arr.put(s.toJson());
        }

        return arr;
    }
}