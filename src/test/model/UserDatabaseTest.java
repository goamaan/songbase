package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseTest {

    UserDatabase udb;

    @BeforeEach
    public void runBefore() {
        udb = new UserDatabase();
    }

    @Test
    public void testAddUser() throws NoSuchAlgorithmException {
        assertTrue(udb.addUser(new User("test", "password")));
        // should have 1 user
        assertEquals(1, udb.getUsers().size());
        // verify user
        User user = udb.getUsers().get(0);
        assertEquals("test",user.getUsername());
        assertEquals("password",user.getPassword());
        // verify that hashed password is actually working and is different from password
        assertNotEquals("password",user.getHashedPassword());
        // salt length > 0 (not empty)
        assertTrue(user.getSalt().length()>0);
    }

    @Test
    public void testAddDuplicateUser() throws NoSuchAlgorithmException {
        assertTrue(udb.addUser(new User("test", "password")));
        assertTrue(udb.addUser(new User("test2", "password")));
        // test the constructor that will be called when fetching from database with hashed password and salt
        assertTrue(udb.addUser(new User("test3", "5fef41bec75fb2b6bf7223862c304eb48dc0ae51", "7f642750bde348e0bd0dafb78aa5683a")));
        // should have 3 user
        assertEquals(3, udb.getUsers().size());
        // should return false as database already has user with that username
        assertFalse(udb.addUser(new User("test", "password")));
        // should have 3 user
        assertEquals(3, udb.getUsers().size());
    }
}
