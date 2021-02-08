package persistence;

import model.User;
import model.UserDatabase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUserWriterTest {

    @Test
    void testUserWriterInvalidFile() {
        try {
            JsonUserWriter writer = new JsonUserWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testUserWriterSingleUser() throws NoSuchAlgorithmException {
        try {
            UserDatabase udb = new UserDatabase();
            User user = new User("abcde", "password");
            User user2 = new User("abcde2", "password");
            udb.addUser(user);
            JsonUserWriter writer = new JsonUserWriter("./data/testUserWriterSingleUser.json");
            writer.open();
            writer.write(udb);
            writer.close();
            JsonUserReader reader = new JsonUserReader("./data/testUserWriterSingleUser.json");
            udb = reader.read();
            // 1 user
            assertEquals(1, udb.getUsers().size());
            // verify that user is present
            assertTrue(udb.contains(user));
            // verify that other user is not present
            assertFalse(udb.contains(user2));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testUserWriterMultipleUsers() throws NoSuchAlgorithmException {
        try {
            UserDatabase udb = new UserDatabase();
            User user = new User("abcde", "password");
            User user2 = new User("abcde2", "password");
            udb.addUser(user);
            udb.addUser(user2);
            JsonUserWriter writer = new JsonUserWriter("./data/testUserWriterMultipleUsers.json");
            writer.open();
            writer.write(udb);
            writer.close();
            JsonUserReader reader = new JsonUserReader("./data/testUserWriterMultipleUsers.json");
            udb = reader.read();
            // 1 user
            assertEquals(2, udb.getUsers().size());
            // verify that user is present
            assertTrue(udb.contains(user));
            // verify that other user is also present
            assertTrue(udb.contains(user2));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
