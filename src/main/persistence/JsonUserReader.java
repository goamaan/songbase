package persistence;

import model.User;
import model.UserDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

//Class that writes the given user database to a file
public class JsonUserReader {

    private String source;

    // EFFECTS: constructs reader to read from given source file
    public JsonUserReader(String source) {
        this.source = source;
    }

    /*
        EFFECTS: reads user JSON from file and returns true if the entered password matches
        the stored password, false if it doesn't match or user does not exist.
        throws IOException if an error occurs reading the file
    */
    public UserDatabase read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        UserDatabase udb = new UserDatabase();
        return parseUserData(udb, jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    public String readFile(String source) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(sb::append);
        }

        return sb.toString();
    }

    // EFFECTS: parses user data from JSON and returns true if user exists and password is correct
    // false otherwise
    private UserDatabase parseUserData(UserDatabase udb, JSONObject jsonObject) {
        JSONArray arr = jsonObject.getJSONArray("users");
        for (Object o : arr) {
            JSONObject user = (JSONObject) o;
            parseUser(udb, user);
        }
        return udb;
    }

    // MODIFIES: udb
    // EFFECTS: parses the user JSONObject to get fields. then adds the user to the given udb
    private void parseUser(UserDatabase udb, JSONObject user) {
        String username = user.getString("username");
        String hashedPassword = user.getString("hashedPassword");
        String storedSalt = user.getString("salt");
        udb.addUser(new User(username, hashedPassword, storedSalt));
    }
}