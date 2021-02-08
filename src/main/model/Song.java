package model;

import org.json.JSONObject;
import persistence.Writable;
import exceptions.InvalidUrlException;
import org.apache.commons.validator.routines.UrlValidator;

// Represents a Song having a name, artist, genre, and optionally a URL
public class Song implements Writable {

    private String name;
    private String artist;
    private String genre;
    private String url;
    private String user;

    // EFFECTS: constructs a song with name, artist, and genre
    public Song(String name, String artist, String genre) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
    }

    // EFFECTS: constructs a song with name, artist, genre, url, and user who adds the song
    // throws InvalidUrlException if the entered URL is not a valid url
    public Song(String name, String artist, String genre, String url, String user) throws InvalidUrlException {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.url = url;
        this.user = user;
        checkUrl(this.url);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setUrl(String url) throws InvalidUrlException {
        this.url = url;
        checkUrl(this.url);
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    private void checkUrl(String url) throws InvalidUrlException {
        UrlValidator validator = new UrlValidator();
        if (!url.trim().isEmpty() && !validator.isValid(url)) {
            throw new InvalidUrlException("Provided URL is not a valid URL");
        }
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject().put("name", name)
                .put("artist", artist)
                .put("genre", genre)
                .put("url", url)
                .put("user", user);
    }
}