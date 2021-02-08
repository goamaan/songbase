package ui;


import exceptions.InvalidUrlException;
import model.Genre;
import model.Song;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JPanel implements ActionListener {

    GridBagConstraints gbc;
    DefaultTableModel allSongsModel;
    DefaultTableModel genreSongsModel;
    DefaultTableModel searchSongsModel;
    JSplitPane splitPane;

    JPanel options;
    JPanel mainView;
    JPanel allSongsPanel;
    JPanel addSongPanel;
    JPanel genreSongsPanel;
    JPanel genreSongsPanelTable;
    JPanel deleteSongPanel;
    JPanel searchPanel;
    JPanel searchPanelTable;

    JButton viewAll;
    JButton viewGenre;
    JButton addSong;
    JButton deleteSong;
    JButton searchByArtist;
    JButton save;
    JButton load;
    JButton submit;
    JButton viewSongs;
    JButton deleteSongBtn;
    JButton searchSongBtn;

    JTextField songNameField;
    JTextField songArtistField;
    JTextField songGenreField;
    JTextField songUrlField;
    JComboBox<String> genres;
    JComboBox<String> selectGenre;

    JTextField songToDelete;

    JTextField artistToSearch;

    String[] genreList = {"Pop", "Rock", "Jazz", "Hip-hop", "Country", "Electronic", "R&B", "Other"};
    Object[] columns = {"Name", "Artist", "Genre", "Added By", "Available at"};

    JLabel songNameLabel;
    JLabel songArtistLabel;
    JLabel songSpecificGenreLabel;
    JLabel songUrlLabel;
    JLabel songGenreLabel;

    // EFFECTS: constructs a MainView JPanel, sets defaults, and loads panel(s)
    public MainView() {
        super();
        setPreferredSize(new Dimension(Gui.xsize, Gui.ysize));
        setBackground(Color.getHSBColor(0, 0, (float) 0.15));
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        initConfig();
        initOptions();
        initMainView();
        initPane();
    }

    // MODIFIES: this
    // EFFECTS: prompts user to load database, if yes is selected then invokes load procedure
    private void askToLoad() {
        String s = "Load Existing Database?";
        int result = JOptionPane.showConfirmDialog(this, s, "Load Data", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            loadDialog();
        }
    }

    // EFFECTS: configures root UIManager object with custom settings for the JOptionPane
    private void initConfig() {
        UIManager.put("OptionPane.minimumSize", new Dimension(Gui.xsize / 5, Gui.ysize / 10));
        UIManager.put("OptionPane.messageFont", new Font("Lato-Regular", Font.PLAIN, 42));
        UIManager.put("OptionPane.buttonFont", new Font("Lato-Regular", Font.PLAIN, 24));
        UIManager.put("Button.select", Color.GRAY);
    }

    // EFFECTS: returns a font from the Lato family with the given size and variant
    private Font configFont(int size, String version) {
        return new Font("Lato-" + version, Font.PLAIN, size);
    }

    // MODIFIES: this
    // EFFECTS: initializes and configures a JSplitPane for the main view
    private void initPane() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, options, mainView);
        splitPane.setPreferredSize(new Dimension(Gui.xsize, Gui.ysize));
        splitPane.setResizeWeight(0.1);
        splitPane.setEnabled(false);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        add(splitPane);
    }

    // MODIFIES: this
    // EFFECTS: initializes and configures the options to be put in the left side of the JSplitPane
    private void initOptions() {
        options = new JPanel();
        options.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.VERTICAL;
        viewAll = new JButton("View All Songs");
        viewGenre = new JButton("View Songs by Genre");
        addSong = new JButton("Add song to genre");
        deleteSong = new JButton("Delete Song");
        searchByArtist = new JButton("Search By Artist");
        save = new JButton("Save Database");
        load = new JButton("Load Database");
        JButton[] buttons = new JButton[]{viewAll, viewGenre, addSong, deleteSong, searchByArtist, save, load};
        gc.insets = new Insets(80, 10, 10, 10);
        for (JButton btn : buttons) {
            initButton(btn);
            gc.gridy++;
            options.add(btn, gc);
        }
        options.setBackground(Color.getHSBColor(0, 0, (float) 0.15));
    }

    // MODIFIES: this
    // EFFECTS: initializes and configures the main view to be put in the right side of the JSplitPane
    private void initMainView() {
        mainView = new JPanel();
        allSongsPanel = new JPanel();
        askToLoad();
        initViewAll();
        mainView.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        mainView.setBackground(Color.getHSBColor(0, 0, (float) 0.15));
        mainView.add(allSongsPanel, gc);
    }

    // MODIFIES: this, btn
    // EFFECTS: returns the given button after formatting and adding an action listener
    private void initButton(JButton btn) {
        btn.addActionListener(this);
        btn.setPreferredSize(new Dimension(400, 100));
        btn.setFont(configFont(30, "Regular"));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
    }

    // MODIFIES: this
    // EFFECTS: overloaded - initializes and returns a formatted JButton with the given text
    private JButton initButton(JButton btn, String text) {
        btn = new JButton(text);
        btn.addActionListener(this);
        btn.setPreferredSize(new Dimension(400, 100));
        btn.setFont(configFont(30, "Regular"));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
        return btn;
    }

    // MODIFIES: this
    // EFFECTS: invokes respective method based on the caller button
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == save) {
            saveDialog();
        } else if (obj == load) {
            loadDialog();
        } else if (obj == submit) {
            addProcedure();
        } else if (obj == viewSongs) {
            viewSongsGenreProcedure();
        } else if (obj == viewGenre) {
            initGenreSongs();
        } else {
            actionPerformedHelper(obj);
        }
    }

    // MODIFIES: this
    // EFFECTS: invokes either the login or register procedure based on the caller - split because of line limit
    private void actionPerformedHelper(Object obj) {
        if (obj == viewAll) {
            initViewAll();
        } else if (obj == addSong) {
            initAddSong();
        } else if (obj == deleteSong) {
            initDeleteSong();
        } else if (obj == deleteSongBtn) {
            deleteSongProcedure();
        } else if (obj == searchByArtist) {
            initSearchSongs();
        } else {
            searchSongProcedure();
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the view all songs panel and adds it to the main view.
    private void initViewAll() {
        allSongsModel = initModel();
        JTable table = new JTable(allSongsModel);
        JScrollPane scrollpane = initTable(table);
        allSongsPanel = initTablePanel(allSongsPanel, scrollpane);
        loadAllSongsData();
        scrollpane.setPreferredSize(allSongsPanel.getPreferredSize());
        resetMainView(allSongsPanel);
    }

    // MODIFIES: this
    // EFFECTS: initializes the add song panel and adds it to the main view.
    private void initAddSong() {
        addSongPanel = new JPanel(new GridBagLayout());
        addSongPanel.setBackground(null);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(50, 10, 50, 10);
        initAddSongLabels();
        genres = new JComboBox<>(genreList);
        submit = initButton(submit, "Submit");
        genres.setFont(configFont(40, "Regular"));
        genres.setPreferredSize(new Dimension(600, 100));
        gc.gridy++;
        addSongPanel.add(songGenreLabel, gc);
        addSongPanel.add(genres, gc);
        initAddSongFields(gc);
        gc.gridy++;
        gc.anchor = GridBagConstraints.LAST_LINE_END;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        addSongPanel.add(submit, gc);
        resetMainView(addSongPanel);
    }

    // MODIFIES: this
    // EFFECTS: initializes the labels for the add song panel
    private void initAddSongLabels() {
        songNameLabel = new JLabel("Name (Required): ");
        songArtistLabel = new JLabel("Artist (Required): ");
        songSpecificGenreLabel = new JLabel("Specific Genre (Required): ");
        songUrlLabel = new JLabel("Url (Optional): ");
        songGenreLabel = new JLabel("Genre (Required): ");
        songGenreLabel.setFont(configFont(60, "Light"));
        songGenreLabel.setForeground(Color.WHITE);
    }

    // MODIFIES: this
    // EFFECTS: initializes the fields for the add song panel
    private void initAddSongFields(GridBagConstraints gc) {
        songNameField = new JTextField(30);
        songArtistField = new JTextField(30);
        songGenreField = new JTextField(30);
        songUrlField = new JTextField(30);
        JLabel[] labels = {songNameLabel, songArtistLabel, songSpecificGenreLabel, songUrlLabel};
        JTextField[] fields = {songNameField, songArtistField, songGenreField, songUrlField};
        for (int i = 0; i < 4; i++) {
            gc.gridy++;
            labels[i].setFont(configFont(60, "Light"));
            labels[i].setForeground(Color.WHITE);
            fields[i].setFont(configFont(40, "Regular"));
            fields[i].setPreferredSize(new Dimension(600, 100));
            fields[i].setBackground(null);
            fields[i].setForeground(Color.WHITE);
            fields[i].setCaretColor(Color.WHITE);
            fields[i].setMargin(new Insets(10, 30, 10, 30));
            addSongPanel.add(labels[i], gc);
            addSongPanel.add(fields[i], gc);
        }
    }

    // MODIFIES: this
    // EFFECTS: first validates the fields, if invalid, then shows a prompt.
    // else adds the song to the database, playing a sound effect,
    // then resets all the given fields
    private void addProcedure() {
        String name = songNameField.getText();
        String artist = songArtistField.getText();
        String specificGenre = songGenreField.getText();
        String url = songUrlField.getText();
        String genre = (String) genres.getSelectedItem();
        if (name.isEmpty() || artist.isEmpty() || specificGenre.isEmpty() || genre == null) {
            String s = "Please Input All Required Fields!";
            JOptionPane.showMessageDialog(this, s, "Error!", JOptionPane.ERROR_MESSAGE);
        } else {
            Song song = null;
            try {
                song = new Song(name, artist, specificGenre, url, Gui.currentUser);
            } catch (InvalidUrlException e) {
                JOptionPane.showMessageDialog(this, "Invalid URL Entered!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
            addToGenrePartOne(genre, song);
            soundEffect();
            JOptionPane.showMessageDialog(this, "Added a song!", "Success!", JOptionPane.PLAIN_MESSAGE);
            resetFields(songNameField, songArtistField, songGenreField, songUrlField);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a song to a specific genre (part 1)
    private void addToGenrePartOne(String res, Song song) {
        switch (res) {
            case "Hip-hop":
                Gui.hipHop.addSong(song);
                break;
            case "Rock":
                Gui.rock.addSong(song);
                break;
            case "Pop":
                Gui.pop.addSong(song);
                break;
            case "Jazz":
                Gui.jazz.addSong(song);
                break;
            default:
                addToGenrePartTwo(res, song);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a song to a specific genre (part 2)
    private void addToGenrePartTwo(String res, Song song) { //(split because line limit)
        switch (res) {
            case "R&B":
                Gui.rnb.addSong(song);
                break;
            case "Electronic":
                Gui.electronic.addSong(song);
                break;
            case "Country":
                Gui.country.addSong(song);
                break;
            case "Other":
                Gui.other.addSong(song);
                break;
            default:
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the view songs from genre panel and adds it to the main view.
    private void initGenreSongs() {
        genreSongsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        selectGenre = new JComboBox<>(genreList);
        selectGenre.setFont(configFont(40, "Regular"));
        selectGenre.setPreferredSize(new Dimension(600, 100));
        viewSongs = new JButton("View Songs");
        initButton(viewSongs);
        JLabel label = new JLabel("Select Genre to view Songs from:");
        label.setFont(configFont(60, "Light"));
        label.setForeground(Color.WHITE);
        gc.insets = new Insets(50, 10, 50, 10);
        genreSongsPanel.setBackground(null);
        gc.gridy++;
        genreSongsPanel.add(label, gc);
        gc.gridy++;
        genreSongsPanel.add(selectGenre, gc);
        gc.gridy++;
        genreSongsPanel.add(viewSongs, gc);
        resetMainView(genreSongsPanel);
    }

    // MODIFIES: this
    // EFFECTS: loads all the songs from the users chosen genre and adds it to the table model.
    private void viewSongsGenreProcedure() {
        genreSongsModel = initModel();
        JTable table = new JTable(genreSongsModel);
        JScrollPane scrollpane = initTable(table);
        genreSongsPanelTable = initTablePanel(genreSongsPanelTable, scrollpane);
        String selectedGenre = (String) selectGenre.getSelectedItem();
        if (selectedGenre != null) {
            List<Song> songs = viewSongFromGenrePartOne(selectedGenre);
            if (songs.isEmpty()) {
                songs = viewSongFromGenrePartTwo(selectedGenre);
            }
            loadConditionalSongData(songs, genreSongsModel);
        }
        scrollpane.setPreferredSize(genreSongsPanelTable.getPreferredSize());
        resetMainView(genreSongsPanelTable);
    }

    // MODIFIES: this, songsInGenre
    // EFFECTS: prints all songs from one of the genres (part 1)
    private List<Song> viewSongFromGenrePartOne(String res) {
        List<Song> songsInGenre = new ArrayList<>();
        switch (res) {
            case "Hip-hop":
                songsInGenre = Gui.hipHop.getSongsInGenre();
                break;
            case "Rock":
                songsInGenre = Gui.rock.getSongsInGenre();
                break;
            case "Pop":
                songsInGenre = Gui.pop.getSongsInGenre();
                break;
            case "Jazz":
                songsInGenre = Gui.jazz.getSongsInGenre();
                break;
            default:

        }
        return songsInGenre;
    }

    // MODIFIES: this, songsInGenre
    // EFFECTS: prints all songs from one of the genres (part 2)
    private List<Song> viewSongFromGenrePartTwo(String res) { //(split because line limit)
        List<Song> songsInGenre = new ArrayList<>();
        switch (res) {
            case "R&B":
                songsInGenre = Gui.rnb.getSongsInGenre();
                break;
            case "Electronic":
                songsInGenre = Gui.electronic.getSongsInGenre();
                break;
            case "Country":
                songsInGenre = Gui.country.getSongsInGenre();
                break;
            case "Other":
                songsInGenre = Gui.other.getSongsInGenre();
                break;
            default:
        }
        return songsInGenre;
    }

    // MODIFIES: this
    // EFFECTS: initializes the delete song panel and adds it to the main view.
    private void initDeleteSong() {
        deleteSongPanel = new JPanel(new GridBagLayout());
        songToDelete = new JTextField(50);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(50, 20, 30, 20);
        JLabel label = new JLabel("Enter name of song to delete");
        label.setFont(configFont(60, "Light"));
        label.setForeground(Color.WHITE);
        songToDelete.setFont(configFont(40, "Regular"));
        songToDelete.setPreferredSize(new Dimension(600, 100));
        songToDelete.setBackground(null);
        songToDelete.setForeground(Color.WHITE);
        songToDelete.setCaretColor(Color.WHITE);
        songToDelete.setMargin(new Insets(10, 30, 10, 30));
        deleteSongBtn = new JButton("Delete Song");
        initButton(deleteSongBtn);
        gc.gridy++;
        deleteSongPanel.add(label, gc);
        gc.gridy++;
        deleteSongPanel.add(songToDelete, gc);
        gc.gridy++;
        deleteSongPanel.add(deleteSongBtn, gc);
        deleteSongPanel.setBackground(null);
        resetMainView(deleteSongPanel);
    }

    // MODIFIES: this
    // EFFECTS: first validates the field, shows prompt if invalid
    // else attempts to delete song. if not present in the database, shows appropriate prompt.
    // else if the current user lacks permissions to delete it, shows appropriate prompt
    // else deletes the song and shows prompt
    private void deleteSongProcedure() {
        String song = songToDelete.getText();
        if (song.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            boolean found = inDatabase(song);
            Song foundSong = findSongByName(song);
            if (!found) {
                String s = "No such song in the database!";
                JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
            } else if (foundSong != null && !foundSong.getUser().toLowerCase().trim().equals(Gui.currentUser)) {
                String s = "You don't have permission to delete this song!";
                JOptionPane.showMessageDialog(this, s, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Gui.db.deleteSongBySearch(song);
                JOptionPane.showMessageDialog(this, "Deleted!", "Success", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    // EFFECTS: returns a Song object with name equal to the parameter name, if found.
    //          else returns null
    private Song findSongByName(String name) {
        List<Song> songs = Gui.db.getAllSongs();
        for (Song s : songs) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: checks to see if a particular song is in the database, returns true if found, false otherwise
    private boolean inDatabase(String song) {
        List<Song> songs = Gui.db.getAllSongs();
        boolean found = false;
        for (Song s : songs) {
            if (s.getName().equals(song)) {
                found = true;
                break;
            }
        }
        return found;
    }

    // MODIFIES: f1, f2, f3, f4
    // EFFECTS: utility function to reset all given JTextFields.
    private void resetFields(JTextField f1, JTextField f2, JTextField f3, JTextField f4) {
        f1.setText("");
        f2.setText("");
        f3.setText("");
        f4.setText("");
    }

    // MODIFIES: this
    // EFFECTS: initializes the search song panel and adds it to the main view.
    private void initSearchSongs() {
        searchPanel = new JPanel(new GridBagLayout());
        artistToSearch = new JTextField(50);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(50, 20, 30, 20);
        JLabel label = new JLabel("Enter artist name");
        label.setFont(configFont(60, "Light"));
        label.setForeground(Color.WHITE);
        artistToSearch.setFont(configFont(40, "Regular"));
        artistToSearch.setPreferredSize(new Dimension(600, 100));
        artistToSearch.setBackground(null);
        artistToSearch.setForeground(Color.WHITE);
        artistToSearch.setCaretColor(Color.WHITE);
        artistToSearch.setMargin(new Insets(10, 30, 10, 30));
        searchSongBtn = new JButton("Search for songs");
        initButton(searchSongBtn);
        gc.gridy++;
        searchPanel.add(label, gc);
        gc.gridy++;
        searchPanel.add(artistToSearch, gc);
        gc.gridy++;
        searchPanel.add(searchSongBtn, gc);
        searchPanel.setBackground(null);
        resetMainView(searchPanel);
    }

    // MODIFIES: this
    // EFFECTS: first validates the search field, if invalid, shows prompt
    // else searches database for entered artist. if found, displays all songs by artist
    // else shows prompt
    private void searchSongProcedure() {
        String artist = artistToSearch.getText();
        if (artist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            List<Song> songsByArtist = Gui.db.getSongsByArtist(artist);

            if (songsByArtist.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No songs by that artist!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                searchSongsModel = initModel();
                JTable table = new JTable(searchSongsModel);
                JScrollPane scrollpane = initTable(table);
                searchPanelTable = initTablePanel(searchPanelTable, scrollpane);
                loadConditionalSongData(songsByArtist, searchSongsModel);
                scrollpane.setPreferredSize(searchPanelTable.getPreferredSize());
                resetMainView(searchPanelTable);
            }
        }
    }

    // MODIFIES: this, table
    // EFFECTS: returns a scrollPane with formatted given table and table header
    private JScrollPane initTable(JTable table) {
        table.setRowHeight(Gui.xsize / 20);
        table.setFont(configFont(30, "Regular"));
        JScrollPane scrollpane = new JScrollPane(table);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(configFont(30, "Regular"));
        table.setForeground(Color.WHITE);
        tableHeader.setForeground(Color.WHITE);
        table.setBackground(Color.getHSBColor(0, 0, (float) 0.18));
        tableHeader.setBackground(Color.getHSBColor(0, 0, (float) 0.18));
        scrollpane.setColumnHeader(new JViewport() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = Gui.ysize / 14;
                return d;
            }
        });
        table.setFillsViewportHeight(true);
        scrollpane.setBackground(Color.getHSBColor(0, 0, (float) 0.18));
        return scrollpane;
    }

    // MODIFIES: this, tablePanel
    // EFFECTS: initializes the wrapper panel for a table with the given scrollPane and returns it
    private JPanel initTablePanel(JPanel tablePanel, JScrollPane scrollpane) {
        tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setPreferredSize(new Dimension(3 * Gui.xsize / 4, 4 * Gui.ysize / 5));
        tablePanel.add(scrollpane, new GridBagConstraints());
        tablePanel.setBackground(Color.getHSBColor(0, 0, (float) 0.18));
        return tablePanel;
    }

    // MODIFIES: this
    // EFFECTS: initializes the view all songs panel and adds it to the main view.
    private DefaultTableModel initModel() {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: loads all songs from the song database into allSongsModel
    private void loadAllSongsData() {
        for (Song s : Gui.db.getAllSongs()) {
            allSongsModel.addRow(new Object[]{s.getName(), s.getArtist(), s.getGenre(), s.getUser(), s.getUrl()});
        }
    }

    // MODIFIES: this, model
    // EFFECTS: loads all songs from given filtered list into the given model
    private void loadConditionalSongData(List<Song> list, DefaultTableModel model) {
        for (Song s : list) {
            model.addRow(new Object[]{s.getName(), s.getArtist(), s.getGenre(), s.getUser(), s.getUrl()});
        }
    }

    // MODIFIES: this
    // EFFECTS: removes all components and adds the given component into mainView
    // then validates component tree and repaints
    private void resetMainView(Component component) {
        mainView.removeAll();
        mainView.add(component, new GridBagConstraints());
        mainView.validate();
        mainView.repaint();
        validate();
        repaint();
    }

    // EFFECTS: saves the current database to the file at the relative path SONG_STORE
    //          prints out Unable to write to file: <Address of file> if FileNotFoundException is caught
    private void saveDialog() {
        try {
            Gui.writer.open();
            Gui.writer.write(Gui.db);
            Gui.writer.close();
            JOptionPane.showMessageDialog(this, "Database Saved!", "Success!", JOptionPane.PLAIN_MESSAGE);
//            System.out.println("Saved database to " + SONG_STORE);
        } catch (FileNotFoundException e) {
//            System.out.println("Unable to write to file: " + SONG_STORE);
            JOptionPane.showMessageDialog(this, "Unable to write database!", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the database at the relative path SONG_STORE into db
    //          prints out Unable to write to file: <Address of file> if FileNotFoundException is caught
    private void loadDialog() {
        try {
            Gui.db = Gui.reader.read();
            List<Genre> updated = Gui.db.getListOfGenre();
            Gui.pop = updated.get(0);
            Gui.rock = updated.get(1);
            Gui.jazz = updated.get(2);
            Gui.hipHop = updated.get(3);
            Gui.country = updated.get(4);
            Gui.electronic = updated.get(5);
            Gui.rnb = updated.get(6);
            Gui.other = updated.get(7);
            JOptionPane.showMessageDialog(this, "Database Loaded!", "Success!", JOptionPane.PLAIN_MESSAGE);
            resetMainView(allSongsPanel);
        } catch (IOException | InvalidUrlException e) {
            String s = "Unable to read from file! New file created";
            JOptionPane.showMessageDialog(this, s, "Failed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: plays a sound, if any exception is caught, prints a message
    private void soundEffect() {
        try {
            File file = new File("res/success2.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            sound.start();
        } catch (Exception e) { // too many exceptions to catch so just did this lol
            System.out.println("sound didn't play :(");
        }
    }

}
