package persistence;

import exceptions.InvalidUrlException;
import model.Genre;
import model.Song;
import model.SongDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/*
    Citation - This class has been modelled after the JsonReader class in
    the JsonSerializationDemo project on the CPSC 210 Github Repo
*/

//Class that writes the given song database to a file
public class JsonReader {

    private final String[] genreList = {"Pop", "Rock", "Jazz", "Hip-hop", "Country", "Electronic", "R&B", "Other"};
    private String source;

    // EFFECTS: constructs reader to read from given source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads song database from file and returns it
    // throws IOException if an error occurs reading the file
    public SongDatabase read() throws IOException, InvalidUrlException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseSongDatabase(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(sb::append);
        }

        return sb.toString();
    }

    // EFFECTS: parses song database from JSON format and returns it
    private SongDatabase parseSongDatabase(JSONObject jsonObject) throws InvalidUrlException {
        List<Genre> listOfGenre = buildGenreList(jsonObject);
        return new SongDatabase(listOfGenre);
    }

    // EFFECTS: parses genre from JSON object and returns listOfGenre to build database
    private List<Genre> buildGenreList(JSONObject jsonObject) throws InvalidUrlException {
        List<JSONArray> listOfGenreJson = new ArrayList<>();
        for (String s : genreList) {
            listOfGenreJson.add(jsonObject.getJSONArray(s));
        }
        List<Genre> loadedList = new ArrayList<>();
        for (String s : genreList) {
            loadedList.add(new Genre(s));
        }
        for (int i = 0; i < listOfGenreJson.size(); i++) {
            Genre g = new Genre(genreList[i]);
            for (Object json : listOfGenreJson.get(i)) {
                JSONObject song = (JSONObject) json;
                loadedList.set(i, addSongsToGenre(g, song));
            }
        }
        return loadedList;
    }

    // MODIFIES: Genre g
    // EFFECTS: parses the song from the JSONArray and adds to genre
    private Genre addSongsToGenre(Genre g, JSONObject s) throws InvalidUrlException {
        String name = s.getString("name");
        String artist = s.getString("artist");
        String genre = s.getString("genre");
        String url = s.getString("url");
        String user = s.getString("user");
        Song song = new Song(name, artist, genre, url, user);
        g.addSong(song);
        return g;
    }
}
