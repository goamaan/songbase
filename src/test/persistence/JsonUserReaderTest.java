package persistence;

import model.User;
import model.UserDatabase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUserReaderTest {

    @Test
    public void testNonExistentFile() {
        JsonUserReader reader = new JsonUserReader("./data/noSuchFile.json");
        try {
            UserDatabase udb = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testUserReaderSingleUser() {
        JsonUserReader reader = new JsonUserReader("./data/testUserReaderSingleUser.json");
        try {
            UserDatabase udb = reader.read();
            // 1 user
            assertEquals(1, udb.getUsers().size());
            // verify that all fields are present on user
            for (User u: udb.getUsers()) {
                assertTrue(u.getUsername().length()>0);
                assertTrue(u.getHashedPassword().length()>0);
                assertTrue(u.getSalt().length()>0);
            }
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }

    @Test
    void testUserReaderMultipleUsers() {
        JsonUserReader reader = new JsonUserReader("./data/testUserReaderMultipleUsers.json");
        try {
            UserDatabase udb = reader.read();
            // 5 user
            assertEquals(5, udb.getUsers().size());
            // verify that all fields are present on all users
            for (User u: udb.getUsers()) {
                assertTrue(u.getUsername().length()>0);
                assertTrue(u.getHashedPassword().length()>0);
                assertTrue(u.getSalt().length()>0);
            }
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }

}
