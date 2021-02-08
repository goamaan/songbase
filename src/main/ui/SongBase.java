package ui;

import exceptions.InvalidUrlException;
import model.*;
import persistence.JsonReader;
import persistence.JsonUserReader;
import persistence.JsonUserWriter;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//Song Sharing Database Application
public class SongBase {

    static Genre hipHop = new Genre("Hip-hop");
    static Genre rock = new Genre("Rock");
    static Genre pop = new Genre("Pop");
    static Genre jazz = new Genre("Jazz");
    static Genre rnb = new Genre("R&B");
    static Genre electronic = new Genre("Electronic");
    static Genre country = new Genre("Country");
    static Genre other = new Genre("Other");

    final Genre[] genres = {hipHop, rock, pop, jazz, rnb, electronic, country, other};

    List<Genre> listOfGenre;
    SongDatabase db;
    UserDatabase udb;
    String currentUser;

    private static final String SONG_STORE = "./data/database.json";
    private static final String USER_STORE = "./data/users.json";
    private JsonWriter writer;
    private JsonReader reader;
    private JsonUserWriter userWriter;
    private JsonUserReader userReader;
    private boolean authenticated;
    Scanner sc;

    // EFFECTS: starts the SongBase application
    public SongBase() throws InvalidUrlException {
        startApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void startApp() throws InvalidUrlException {
        boolean quit = false;
        String response;
        init();
        udb = loadUsers();

        authenticationOptions();
        while (!quit) {
            if (authenticated) {
                options();
            }
            response = sc.nextLine().toLowerCase();
            if (response.equals("q")) {
                quit = true;
            } else if (!authenticated) {
                authenticate(response);
            } else {
                processResponse(response);
            }
        }
        processExitResponse();
        System.out.println("\n Thank you for using SongBase!");
    }

    // MODIFIES: this
    // EFFECTS: initializes scanner, SongDatabase with a list of genre, empty user database
    private void init() {
        listOfGenre = new ArrayList<>();
        listOfGenre.addAll(Arrays.asList(genres).subList(0, 8));
        db = new SongDatabase(listOfGenre);
        udb = new UserDatabase();
        writer = new JsonWriter(SONG_STORE);
        reader = new JsonReader(SONG_STORE);
        userWriter = new JsonUserWriter(USER_STORE);
        userReader = new JsonUserReader(USER_STORE);
        sc = new Scanner(System.in);
        authenticated = false;
    }

    // EFFECTS: Separates authentication options from processResponse, asks user to either login or signup, invoking
    // the respective procedure
    private void authenticate(String response) {
        if (response.equals("l")) {
            login();
        } else if (response.equals("r")) {
            register();
        } else {
            System.out.println("Invalid Input!");
        }
    }

    // EFFECTS: login procedure - asks user input for username and password. If valid then validate() is invoked,
    // else message saying Invalid username/password! is printed.
    private void login() {
        boolean isValid = false;
        boolean goBack = false;
        String username = "";
        String password;
        while (!isValid) {
            System.out.println("\nEnter Username:  - [q] to go back");
            username = sc.nextLine().toLowerCase().trim();
            if (username.equals("q")) {
                goBack = true;
                break;
            }
            System.out.println("\nEnter Password: ");
            password = sc.nextLine().toLowerCase().trim();
            isValid = checkCredentials(username, password);
            if (!isValid) {
                System.out.println("Invalid username/password!");
            }
        }
        if (goBack) {
            authenticationOptions();
        }
        validate(isValid, username);
    }

    // MODIFIES: this
    // EFFECTS: if isValid, then sets the current user to the username entered, and authenticated to true.
    private void validate(boolean isValid, String username) {
        if (isValid) {
            this.currentUser = username;
            authenticated = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: returns false if the user does not exist in the database or
    //          the password entered is incorrect, else returns true.
    //          prints out a "Something went wrong" message if NoSuchAlgorithmException is caught
    private boolean checkCredentials(String username, String password) {
        User user = null;
        try {
            user = new User(username, password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        if (!udb.contains(user)) {
            return false;
        }
        for (User u : udb.getUsers()) {
            if (u.getUsername().equals(username)) {
                try {
                    if (u.generateHash(u.getSalt() + password).equals(u.getHashedPassword())) {
                        return true;
                    }
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Something went wrong!" + e.getMessage());
                }
            }
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: If a user with the given username already exists then prints out
    //          "User with that username already exists!", else it adds the user
    //          with username and password to the user database
    private void register() {
        User user = printRegisterOptions();
        if (user == null) {
            authenticationOptions();
        } else {
            boolean success = udb.addUser(user);
            if (!success) {
                System.out.println("User with that username already exists!");
                authenticationOptions();
            } else {
                saveUsers(user);
            }
        }
    }

    // EFFECTS: returns null if the user input was "q" to go back to the previous menu
    //          else returns a User object with username length >= 4 and password length >= 5
    //          prints "Something went wrong!" if NoSuchAlgorithmException is caught
    private User printRegisterOptions() {
        System.out.println("\nEnter Username (min 5 characters): - [q] to go back ");
        String username = sc.nextLine().toLowerCase().trim();
        if (username.equals("q")) {
            return null;
        }

        while (username.length() < 5) {
            System.out.println("Minimum 5 characters needed!");
            username = sc.nextLine().toLowerCase().trim();
        }
        System.out.println("\nEnter Password (min 5 characters): ");
        String password = sc.nextLine().toLowerCase().trim();
        while (password.length() < 5) {
            System.out.println("Minimum 5 characters needed!");
            password = sc.nextLine().toLowerCase().trim();
        }
        try {
            return new User(username, password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        return null;
    }

    // EFFECTS: prompts the user to save their database before quitting if they are authenticated
    private void processExitResponse() {
        boolean quitForSure = false;
        while (authenticated && !quitForSure) {
            System.out.println("Make sure to save any unsaved changes, they will be lost!");
            System.out.println("\t [s] -> Save my database and quit");
            System.out.println("\t [q] -> Let me just quit!!");
            String response = sc.nextLine().trim().toLowerCase();
            if (response.equals("s")) {
                saveProcedure();
                quitForSure = true;
            } else if (response.equals("q")) {
                quitForSure = true;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the user database at the relative path USER_STORE into udb
    //          prints out Unable to read users from file: <Address of file>  - if IOException is caught
    private UserDatabase loadUsers() {
        try {
            udb = userReader.read();
        } catch (IOException e) {
            System.out.println("Unable to read users from file: " + USER_STORE);
        }
        return udb;
    }

    // MODIFIES: this
    // EFFECTS: saves the current user database to the file at the relative path USER_STORE
    //          if FileNotFoundException is caught - prints out Unable to write to file: <Address of file>
    //          else sets currentUser to the entered user's username, sets authenticated to true, and prints Welcome!
    private void saveUsers(User user) {
        try {
            userWriter.open();
            userWriter.write(udb);
            authenticated = true;
            this.currentUser = user.getUsername();
            System.out.println("Welcome!");
            userWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + USER_STORE);
        }
    }


    // EFFECTS: prints out all options available to user pre-authentication - login, register, or quit the app
    private void authenticationOptions() {
        System.out.println("\nHi! Log in or Register to proceed!");
        System.out.println("\t[l] => Log in");
        System.out.println("\t[r] => Register");
        System.out.println("\t[q] => Quit :( ");
    }

    // MODIFIES: this
    // EFFECTS: shows possible options to user
    private void options() {
        System.out.println("\nEnter a command!");
        System.out.println("\t[a] => Add Song");
        System.out.println("\t[d] => Delete Song");
        System.out.println("\t[v] => View Songs from Genre");
        System.out.println("\t[va] => View All Songs");
        System.out.println("\t[s] => Search Songs by Artist");
        System.out.println("\t[sv] => Save Current Database (Overwrite)");
        System.out.println("\t[ld] => Load Existing Database");
        System.out.println("\t[q] => Quit the App :(");
        System.out.println("\n\tReminder to load your database if it exists!");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processResponse(String response) throws InvalidUrlException {
        switch (response) {
            case "a":
                addSongProcedure();
                break;
            case "d":
                deleteSongProcedure();
                break;
            case "v":
                viewSongProcedure();
                break;
            case "va":
                viewAllProcedure();
                break;
            case "s":
                searchByArtistProcedure();
                break;
            default:
                processResponseSaveOrLoad(response);
        }
    }

    // MODIFIES: this
    // EFFECTS: processes user command of saving or loading the database
    private void processResponseSaveOrLoad(String response) throws InvalidUrlException {
        if (response.equals("sv")) {
            saveProcedure();
        } else if (response.equals("ld")) {
            loadProcedure();
        } else {
            System.out.println("\n Invalid Input!");
        }
    }

    // EFFECTS: saves the current database to the file at the relative path SONG_STORE
    //          prints out Unable to write to file: <Address of file> if FileNotFoundException is caught
    private void saveProcedure() {
        try {
            writer.open();
            writer.write(db);
            writer.close();
            System.out.println("Saved database to " + SONG_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + SONG_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the database at the relative path SONG_STORE into db
    //          prints out Unable to write to file: <Address of file> if FileNotFoundException is caught
    private void loadProcedure() throws InvalidUrlException {
        try {
            db = reader.read();
            List<Genre> updated = db.getListOfGenre();
            hipHop = updated.get(0);
            rock = updated.get(1);
            pop = updated.get(2);
            jazz = updated.get(3);
            rnb = updated.get(4);
            electronic = updated.get(5);
            country = updated.get(6);
            other = updated.get(7);
            System.out.println("Loaded database from " + SONG_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + SONG_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: starts addSong procedure
    private void addSongProcedure() throws InvalidUrlException {
        String res = askForGenre();
        if (!res.equals("bac")) {
            Song song = songDetailsProcedure();
            addToGenrePartOne(res, song);
            System.out.println("\n Added a song!");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a song to a specific genre (part 1)
    private void addToGenrePartOne(String res, Song song) {
        switch (res) {
            case "hip":
                hipHop.addSong(song);
                break;
            case "roc":
                rock.addSong(song);
                break;
            case "pop":
                pop.addSong(song);
                break;
            case "jaz":
                jazz.addSong(song);
                break;
            default:
                addToGenrePartTwo(res, song);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a song to a specific genre (part 2)
    private void addToGenrePartTwo(String res, Song song) { //(split because line limit)
        switch (res) {
            case "rnb":
                rnb.addSong(song);
                break;
            case "ele":
                electronic.addSong(song);
                break;
            case "cou":
                country.addSong(song);
                break;
            case "oth":
                other.addSong(song);
                break;
            default:
        }
    }

    // MODIFIES: this
    // EFFECTS: starts deleteSong procedure - deletes a song from a genre based on user entered string
    private void deleteSongProcedure() {
        System.out.println("\n Enter name of song to delete:");
        String songToDelete = sc.nextLine().trim();
        while (songToDelete.isEmpty()) {
            System.out.println("\nInvalid Input!");
            songToDelete = sc.nextLine();
        }
        boolean found = inDatabase(songToDelete);
        Song song = findSongByName(songToDelete);
        if (!found) {
            System.out.println("\n No such song in the database!");
        } else if (song != null && !song.getUser().equals(this.currentUser)) {
            System.out.println("You don't have permission to delete this song!");
        } else {
            deleteSongConfirm(songToDelete);
        }
    }

    // EFFECTS: returns a Song object with name equal to the parameter name, if found.
    //          else returns null
    private Song findSongByName(String name) {
        List<Song> songs = db.getAllSongs();
        for (Song s : songs) {
            if (s.getName().toLowerCase().equals(name)) {
                return s;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: checks to see if a particular song is in the database, returns true if found, false otherwise
    private boolean inDatabase(String song) {
        List<Song> songs = db.getAllSongs();
        boolean found = false;
        for (Song s : songs) {
            if (s.getName().equals(song)) {
                found = true;
                break;
            }
        }
        return found;
    }

    // EFFECTS: deletes a song with name songToDelete if the user confirms through user input that they want to
    private void deleteSongConfirm(String songToDelete) {
        System.out.println("Are you sure you want to delete this song? [y] => Yes [n] => No");
        String confirm = sc.nextLine().trim();
        while (!confirm.equals("y") && !confirm.equals("n")) {
            System.out.println("\nInvalid Input!");
            confirm = sc.nextLine();
        }
        if (confirm.equals("y")) {
            db.deleteSongBySearch(songToDelete);
            System.out.println("\n Song Deleted!");
        }
    }

    // MODIFIES: this
    // EFFECTS: returns all songs in user chosen genre - prompts user to choose a genre
    private void viewSongProcedure() {
        String res = askForGenre();
        List<Song> songsInGenre = viewSongFromGenrePartOne(res, new ArrayList<>());
        if (songsInGenre.isEmpty()) {
            songsInGenre = viewSongFromGenrePartTwo(res, new ArrayList<>());
        }

        if (songsInGenre.isEmpty()) {
            System.out.println("\n There are no songs in this genre!");
        }

        for (Song s : songsInGenre) {
            System.out.println("\n" + s.getName());
            System.out.println("\tArtist: " + s.getArtist());
            System.out.println("\tAdded by - " + s.getUser());
            if (!s.getUrl().isEmpty()) {
                System.out.println("\tAccessible at: " + s.getUrl());
            }
        }

    }

    // MODIFIES: this, songsInGenre
    // EFFECTS: prints all songs from one of the genres (part 1)
    private List<Song> viewSongFromGenrePartOne(String res, List<Song> songsInGenre) {
        switch (res) {
            case "hip":
                songsInGenre = hipHop.getSongsInGenre();
                break;
            case "roc":
                songsInGenre = rock.getSongsInGenre();
                break;
            case "pop":
                songsInGenre = pop.getSongsInGenre();
                break;
            case "jaz":
                songsInGenre = jazz.getSongsInGenre();
                break;
            default:
        }
        return songsInGenre;
    }

    // MODIFIES: this, songsInGenre
    // EFFECTS: prints all songs from one of the genres (part 2)
    private List<Song> viewSongFromGenrePartTwo(String res, List<Song> songsInGenre) { //(split because line limit)
        switch (res) {
            case "rnb":
                songsInGenre = rnb.getSongsInGenre();
                break;
            case "ele":
                songsInGenre = electronic.getSongsInGenre();
                break;
            case "cou":
                songsInGenre = country.getSongsInGenre();
                break;
            case "oth":
                songsInGenre = other.getSongsInGenre();
                break;
            default:
        }
        return songsInGenre;
    }

    // MODIFIES: this
    // EFFECTS: starts view all song procedure
    private void viewAllProcedure() {
        List<Song> allSongs = db.getAllSongs();
        if (allSongs.isEmpty()) {
            System.out.println("\n There are no songs in the database!");
        }
        for (Song s : allSongs) {
            System.out.println("\n" + s.getName());
            System.out.println("\tArtist: " + s.getArtist());
            System.out.println("\tGenre: " + s.getGenre());
            System.out.println("\tAdded by - " + s.getUser());
            if (!s.getUrl().isEmpty()) {
                System.out.println("\tAccessible at: " + s.getUrl());
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: searches for all songs by artist - prompts user for artist name entry
    private void searchByArtistProcedure() {
        System.out.println("\n Enter name of artist to filter songs:");
        String artist = sc.nextLine().trim();
        while (artist.isEmpty()) {
            System.out.println("\nInvalid Input!");
            artist = sc.nextLine();
        }
        List<Song> songsByArtist = db.getSongsByArtist(artist);

        if (songsByArtist.isEmpty()) {
            System.out.println("\n No Songs by that Artist in the database!");
        }
        for (Song s : songsByArtist) {
            System.out.println("\n" + s.getName());
            System.out.println("\tGenre:" + s.getGenre());
            if (!s.getUrl().isEmpty()) {
                System.out.println("\tAccessible at: " + s.getUrl());
            }
        }
    }

    // EFFECTS: prompts user for genre selection
    private String askForGenre() {
        printGenre();
        String res = sc.nextLine().toLowerCase();
        while (!res.equals("hip") && !res.equals("roc")
                && !res.equals("pop") && !res.equals("jaz")
                && !res.equals("rnb") && !res.equals("ele")
                && !res.equals("cou") && !res.equals("oth")
                && !res.equals("bac")) {
            System.out.println("\n Invalid selection!");
            printGenre();
            res = sc.nextLine().toLowerCase();
        }
        return res;
    }

    // EFFECTS: prints all genres
    private void printGenre() {
        System.out.println("\n Select a genre!");
        System.out.println("\t[hip] => Hip-hop");
        System.out.println("\t[roc] => Rock");
        System.out.println("\t[pop] => Pop");
        System.out.println("\t[jaz] => Jazz");
        System.out.println("\t[rnb] => R&B");
        System.out.println("\t[ele] => Electronic");
        System.out.println("\t[cou] => Country");
        System.out.println("\t[oth] => Other");
        System.out.println("\t[bac] => Back to Main Menu");
    }

    // EFFECTS: prompts user for entering song details and returns the created Song object
    private Song songDetailsProcedure() throws InvalidUrlException {
        System.out.println("\n Name of song:");
        String song = sc.nextLine().trim();
        while (song.isEmpty()) {
            System.out.println("\n Name can't be empty!");
            song = sc.nextLine().trim();
        }
        System.out.println("\n Artist of song:");
        String artist = sc.nextLine().trim();
        while (artist.isEmpty()) {
            System.out.println("\n Artist can't be empty!");
            artist = sc.nextLine().trim();
        }
        System.out.println("\n Specific Genre of song:");
        String genre = sc.nextLine().trim();
        while (genre.isEmpty()) {
            System.out.println("\n Genre can't be empty!");
            genre = sc.nextLine().trim();
        }
        String urlRes = optionalURL();
        return new Song(song, artist, genre, urlRes, this.currentUser);
    }

    // EFFECTS: prompts user for optional URL entry
    private String optionalURL() {
        System.out.println("\n Provide optional URL for song playback:");
        System.out.println("\t Press Enter to skip");
        String urlRes = sc.nextLine().trim();
        if (urlRes.isEmpty()) {
            return "";
        }
        return urlRes;
    }


}
