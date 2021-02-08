package persistence;

import exceptions.InvalidUrlException;
import model.Genre;
import model.SongDatabase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonReaderTest {

    @Test
    public void testNonExistentFile() throws InvalidUrlException {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            SongDatabase db = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyGenres() throws InvalidUrlException {
        JsonReader reader = new JsonReader("./data/testReaderEmptyGenres.json");
        try {
            SongDatabase db = reader.read();
            // no songs
            assertEquals(0, db.getNumberOfSongs());
            // all 8 genre still there
            assertEquals(8, db.getListOfGenre().size());
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }

    @Test
    void testReaderFilledGenres() throws InvalidUrlException {
        JsonReader reader = new JsonReader("./data/testReaderFilledGenres.json");
        try {
            SongDatabase db = reader.read();
            // 8 songs - 1 per genre
            assertEquals(8, db.getNumberOfSongs());
            // all 8 genre still there
            assertEquals(8, db.getListOfGenre().size());
            for (Genre g : db.getListOfGenre()) {
                // 1 song in each genre
                assertEquals(1, g.getNumberOfSongsInGenre());
            }
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }

}
