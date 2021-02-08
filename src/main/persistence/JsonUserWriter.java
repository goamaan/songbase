package persistence;

import model.UserDatabase;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
    Citation - This file has been modelled after the JsonWriter class in
    the JsonSerializationDemo project on the CPSC 210 Github Repo
*/

//Class that writes the given user database to a file
public class JsonUserWriter {

    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonUserWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer stream
    // throws FileNotFoundException if destination file is not found/cannot be opened
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes user database in JSON format to file
    public void write(UserDatabase udb) {
        JSONObject json = udb.toJson();
        // 2 space tabs are superior :))
        saveToFile(json.toString(2));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }

}
