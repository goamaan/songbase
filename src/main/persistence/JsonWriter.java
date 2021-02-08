package persistence;

import model.SongDatabase;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/*
    Citation - This file has been modelled after the JsonWriter class in
    the JsonSerializationDemo project on the CPSC 210 Github Repo
*/

//Class that writes the given song database to a file
public class JsonWriter {

    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer stream
    // throws FileNotFoundException if destination file is not found/cannot be opened
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes song database in JSON format to file
    public void write(SongDatabase db) {
        JSONObject json = db.toJson();
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
