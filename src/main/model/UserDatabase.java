package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a UserDatabase having a list of users.
public class UserDatabase implements Writable {

    private List<User> users;

    // EFFECTS: constructs a UserDatabase with an empty user list
    public UserDatabase() {
        users = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds a User to the user list if there is no user with the same username and returns true,
    // false otherwise
    public boolean addUser(User user) {
        if (contains(user)) {
            return false;
        }
        return users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    // EFFECTS: returns true if there exists a user in the list of users with the same username,
    // false otherwise
    public boolean contains(User user) {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    // EFFECTS: returns a JSONObject of the current user database
    @Override
    public JSONObject toJson() {
        return new JSONObject().put("users", usersJson());
    }

    // EFFECTS: returns a JSONArray of all the users in the user list
    public JSONArray usersJson() {
        JSONArray arr = new JSONArray();

        for (User u : users) {
            arr.put(u.toJson());
        }

        return arr;
    }
}