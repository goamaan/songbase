package model;

import exceptions.InvalidUrlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GenreTest {

    Genre anyGenreHere;

    @BeforeEach
    public void runBefore() {
        anyGenreHere = new Genre("Some Genre");
    }

    @Test
    public void testAddSong() {
        //Test Song Constructor without URL
        Song testSong1 = new Song("Easy", "Mac Ayres", "R&B");
        anyGenreHere.addSong(testSong1);
        List<Song> returnedSongs = anyGenreHere.getSongsInGenre();
        assertEquals(1, returnedSongs.size());
        assertEquals(testSong1, returnedSongs.get(0));
    }

    @Test
    public void testAddMultipleSongs() throws InvalidUrlException {
        //Test Song constructor with URL

        Song testSong1 = new Song("Easy", "Mac Ayres", "R&B", "https://www.youtube.com/watch?v=PWK8EuUSMSI", "testUser");
        Song testSong2 = new Song("SWIM", "Brockhampton", "R&B", "https://www.youtube.com/watch?v=B50Ye_-yEHU", "testUser");
        anyGenreHere.addSong(testSong1);
        anyGenreHere.addSong(testSong2);
        List<Song> returnedSongs = anyGenreHere.getSongsInGenre();

        //test getNumberOfSongsInGenre by using it to get size of list
        int returnedSize = anyGenreHere.getNumberOfSongsInGenre();
        assertEquals(2, returnedSize);

        // Test all fields of each song. Implicitly test getters as well
        assertEquals(testSong1.getArtist(), returnedSongs.get(0).getArtist());
        assertEquals(testSong1.getGenre(), returnedSongs.get(0).getGenre());
        assertEquals(testSong1.getName(), returnedSongs.get(0).getName());
        assertEquals(testSong1.getUrl(), returnedSongs.get(0).getUrl());
        assertEquals(testSong1.getUser(), returnedSongs.get(0).getUser());

        assertEquals(testSong2.getArtist(), returnedSongs.get(1).getArtist());
        assertEquals(testSong2.getGenre(), returnedSongs.get(1).getGenre());
        assertEquals(testSong2.getName(), returnedSongs.get(1).getName());
        assertEquals(testSong2.getUrl(), returnedSongs.get(1).getUrl());
        assertEquals(testSong2.getUser(), returnedSongs.get(1).getUser());
    }

    @Test
    public void testRemoveMultipleSongs() throws InvalidUrlException {
        Song testSong1 = new Song("Easy", "Mac Ayres", "R&B", "https://www.youtube.com/watch?v=PWK8EuUSMSI", "testUser");
        Song testSong2 = new Song("SWIM", "Brockhampton", "R&B", "https://www.youtube.com/watch?v=B50Ye_-yEHU", "testUser");
        Song testSong3 = new Song("Corners of my mind", "Emotional Oranges", "R&B", "https://www.youtube.com/watch?v=2uqvMn81HXg", "testUser");
        //Test Song Setters
        testSong3.setName("Afterthought");
        testSong3.setArtist("Joji");
        testSong3.setGenre("R&B");
        testSong3.setUrl("https://www.youtube.com/watch?v=ujriV3vkC9w");

        anyGenreHere.addSong(testSong1);
        anyGenreHere.addSong(testSong2);
        anyGenreHere.addSong(testSong3);
        // 3 songs added, therefore list size should be 3
        assertEquals(3, anyGenreHere.getNumberOfSongsInGenre());
        anyGenreHere.removeSong(testSong2);
        // Removed 1 song, therefore list size should be 2
        assertEquals(2, anyGenreHere.getNumberOfSongsInGenre());
        // Using overloaded remove using song search to test
        anyGenreHere.removeSong("Afterthought");
        assertEquals(1, anyGenreHere.getNumberOfSongsInGenre());
        // Make sure the correct songs remain
        List<Song> returnedSongs = anyGenreHere.getSongsInGenre();
        assertEquals(testSong1.getName(), returnedSongs.get(0).getName());
    }

    @Test
    public void testCreateSongValidUrl() {
        try {
            Song testSong1 = new Song("Easy", "Mac Ayres", "R&B", "https://www.youtube.com/watch?v=PWK8EuUSMSI", "testUser");
            Song testSong2 = new Song("Easy2", "Mac Ayres2", "R&B");
            // empty url should not throw error
            Song testSong3 = new Song("Easy3", "Mac Ayres3", "R&B", "", "testUser");
            // test setter as well
            testSong2.setUrl("https://www.youtube.com/watch?v=PWK8EuUSMSI");
        } catch (InvalidUrlException e) {
            fail("not expected here");
        }
    }

    @Test
    public void testCreateSongInvalidUrl() {
        try {
            Song testSong1 = new Song("Easy", "Mac Ayres", "R&B", "htww.youtube.com/watch?v=PWK8EuUSMSI", "testUser");
            fail("not expected");
        } catch (InvalidUrlException e) {
            //expected
        }
    }
}
