package persistence;

import exceptions.InvalidUrlException;
import model.Genre;
import model.Song;
import model.SongDatabase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest {

    List<Genre> listOfGenre = new ArrayList<>();
    static Genre hipHop = new Genre("Hip-hop");
    static Genre rock = new Genre("Rock");
    static Genre pop = new Genre("Pop");
    static Genre jazz = new Genre("Jazz");
    static Genre rnb = new Genre("R&B");
    static Genre electronic = new Genre("Electronic");
    static Genre country = new Genre("Country");
    static Genre other = new Genre("Other");
    final Genre[] genres = {hipHop, rock, pop, jazz, rnb, electronic, country, other};


    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyDatabase() throws InvalidUrlException {
        try {
            listOfGenre.addAll(Arrays.asList(genres));
            SongDatabase db = new SongDatabase(listOfGenre);
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyDatabase.json");
            writer.open();
            writer.write(db);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyDatabase.json");
            db = reader.read();
            // no songs
            assertEquals(0, db.getNumberOfSongs());
            // all 8 genre still there
            assertEquals(8, db.getListOfGenre().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterFilledDatabase() throws InvalidUrlException {
        try {
            listOfGenre.addAll(Arrays.asList(genres));
            for (Genre g : listOfGenre) {
                g.addSong(new Song("test", "test", "test", "", "admin"));
            }
            SongDatabase db = new SongDatabase(listOfGenre);
            JsonWriter writer = new JsonWriter("./data/testWriterFilledDatabase.json");
            writer.open();
            writer.write(db);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterFilledDatabase.json");
            db = reader.read();
            // 8 songs - 1 per genre
            assertEquals(8, db.getNumberOfSongs());
            // all 8 genre still there
            assertEquals(8, db.getListOfGenre().size());
            for (Genre g : db.getListOfGenre()) {
                // 1 song in each genre
                assertEquals(1, g.getNumberOfSongsInGenre());
            }
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

}
