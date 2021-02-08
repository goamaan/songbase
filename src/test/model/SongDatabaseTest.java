package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SongDatabaseTest {

    final Genre HIP_HOP = new Genre("Hip-Hop");
    final Genre ROCK = new Genre("Rock");
    final Genre POP = new Genre("Pop");
    final Genre JAZZ = new Genre("Jazz");
    final Genre RNB = new Genre("R&B");
    final Genre ELECTRONIC = new Genre("Electronic");
    final Genre COUNTRY = new Genre("Country");
    final Genre OTHER = new Genre("Other");

    List<Genre> listOfGenre = new ArrayList<>();
    SongDatabase db;

    @BeforeEach
    public void runBefore() {
        listOfGenre.add(HIP_HOP);
        listOfGenre.add(ROCK);
        listOfGenre.add(POP);
        listOfGenre.add(JAZZ);
        listOfGenre.add(RNB);
        listOfGenre.add(ELECTRONIC);
        listOfGenre.add(COUNTRY);
        listOfGenre.add(OTHER);
        db = new SongDatabase(listOfGenre);
    }

    @Test
    public void testConstructor() {
        List<Genre> list = db.getListOfGenre();
        assertEquals(8, list.size());
        // Make sure all the genre names are also the same
        assertEquals(HIP_HOP.getName(), list.get(0).getName());
        assertEquals(ROCK.getName(), list.get(1).getName());
        assertEquals(POP.getName(), list.get(2).getName());
        assertEquals(JAZZ.getName(), list.get(3).getName());
        assertEquals(RNB.getName(), list.get(4).getName());
        assertEquals(ELECTRONIC.getName(), list.get(5).getName());
        assertEquals(COUNTRY.getName(), list.get(6).getName());
        assertEquals(OTHER.getName(), list.get(7).getName());
    }

    @Test
    public void testNumberOfSongs() {
        HIP_HOP.addSong(new Song("test1", "test1", "test1"));
        JAZZ.addSong(new Song("test2", "test2", "test2"));
        COUNTRY.addSong(new Song("test3", "test3", "test3"));

        assertEquals(3, db.getNumberOfSongs());
    }

    @Test
    public void testAllSongs() {
        Song test1 = new Song("test1", "test1", "test1");
        Song test2 = new Song("test2", "test2", "test2");
        Song test3 = new Song("test3", "test3", "test3");
        HIP_HOP.addSong(test1);
        JAZZ.addSong(test2);
        COUNTRY.addSong(test3);

        List<Song> songsInDb = db.getAllSongs();

        //Check that all 3 songs are present
        assertEquals(test1.getName(), songsInDb.get(0).getName());
        assertEquals(test2.getName(), songsInDb.get(1).getName());
        assertEquals(test3.getName(), songsInDb.get(2).getName());
    }

    @Test
    public void testSongsByArtist() {
        Song test1 = new Song("test1", "sameArtist", "test1");
        Song test2 = new Song("test2", "differentArtist", "test2");
        Song test3 = new Song("test3", "sameArtist", "test3");
        HIP_HOP.addSong(test1);
        JAZZ.addSong(test2);
        COUNTRY.addSong(test3);

        List<Song> songsInDb = db.getSongsByArtist("sameArtist");

        //Should have 2 songs in db by "sameArtist" artist
        assertEquals(2, songsInDb.size());
        //Check that correct songs are present
        assertEquals(test1.getName(), songsInDb.get(0).getName());
        assertEquals(test3.getName(), songsInDb.get(1).getName());
    }

    @Test
    public void testDeleteSongBySearch() {
        Song test1 = new Song("test1", "test1", "test1");
        Song test2 = new Song("test2", "test2", "test2");
        Song test3 = new Song("test3", "test3", "test3");
        HIP_HOP.addSong(test1);
        JAZZ.addSong(test2);
        COUNTRY.addSong(test3);

        db.deleteSongBySearch("test1");
        // Should only have 2 songs remaining
        assertEquals(2, db.getNumberOfSongs());
        List<Song> songsInDb = db.getAllSongs();
        //Check that correct songs are present
        assertEquals(test2.getName(), songsInDb.get(0).getName());
        assertEquals(test3.getName(), songsInDb.get(1).getName());
    }

    @Test
    public void testDeleteSongBySearchFail() {
        Song test1 = new Song("test1", "test1", "test1");
        Song test2 = new Song("test2", "test2", "test2");
        HIP_HOP.addSong(test1);
        JAZZ.addSong(test2);

        db.deleteSongBySearch("test1123");
        // Should have both songs remaining
        assertEquals(2, db.getNumberOfSongs());

    }
}
