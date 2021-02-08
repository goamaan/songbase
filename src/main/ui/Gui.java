package ui;

import model.Genre;
import model.SongDatabase;
import model.User;
import model.UserDatabase;
import persistence.JsonReader;
import persistence.JsonUserReader;
import persistence.JsonUserWriter;
import persistence.JsonWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Represents a GUI class that builds a JFrame to implement the GUI - initializes login view
public class Gui extends JFrame implements ActionListener {

    protected static int xsize;
    protected static int ysize;


    static Genre hipHop = new Genre("Hip-hop");
    static Genre rock = new Genre("Rock");
    static Genre pop = new Genre("Pop");
    static Genre jazz = new Genre("Jazz");
    static Genre rnb = new Genre("R&B");
    static Genre electronic = new Genre("Electronic");
    static Genre country = new Genre("Country");
    static Genre other = new Genre("Other");

    final Genre[] genres = {hipHop, rock, pop, jazz, rnb, electronic, country, other};

    protected List<Genre> listOfGenre;
    protected static SongDatabase db;
    protected static UserDatabase udb;
    protected static String currentUser;
    protected static boolean authenticated;
    protected static JsonWriter writer;
    protected static JsonReader reader;
    protected static JsonUserWriter userWriter;
    protected static JsonUserReader userReader;

    protected static final String SONG_STORE = "./data/database.json";
    protected static final String USER_STORE = "./data/users.json";

    JPanel loginPanel;

    private JLabel heading;
    Font font;
    Border padding;
    JLabel userLabel;
    JLabel passwordLabel;
    JTextField username;
    JTextField password;
    GridBagConstraints gbc;
    JButton login;
    JButton register;
    JLabel message;

    // MODIFIES: this
    // EFFECTS: adds the Lato font to the local GraphicsEnvironment
    public void runBefore() {
        /*
            Code for adding a custom font was taken from this stackoverflow post
            https://stackoverflow.com/questions/5652344/how-can-i-use-a-custom-font-in-java
        */
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/Lato-Light.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/Lato-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/Lato-Thin.ttf")));
            font = new Font("Lato-Light", Font.PLAIN, 78);
        } catch (IOException | FontFormatException e) {
            System.out.println("error loading font");
        }
    }

    // EFFECTS: constructs a Gui JFrame, sets defaults, and loads resources
    public Gui() {
        super("SongBase");
        runBefore();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initSize();
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(2, 2, 2, 2));
        getContentPane().setBackground(Color.getHSBColor(0, 0, (float) 0.15));
        try {
            setIconImage(ImageIO.read(new File("res/headphones.png")));
        } catch (IOException e) {
            System.out.println("error");
        }
        init();
        udb = loadUsers();
        centreOnScreen();
        pack();
        setVisible(true);
        saveOnExit();
    }

    // MODIFIES: this
    // EFFECTS: sets the size of the window
    private void initSize() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        xsize = ((int) tk.getScreenSize().getWidth());
        ysize = ((int) tk.getScreenSize().getHeight());
        xsize = (int) (Math.round(xsize * 0.80));
        ysize = (int) (Math.round(ysize * 0.70));
        setSize(xsize, ysize);
    }

    // MODIFIES: this
    // EFFECTS:  centres the JFrame on the screen
    private void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    // MODIFIES: this
    // EFFECTS: initializes global vars
    private void init() {
        authenticated = false;
        listOfGenre = new ArrayList<>();
        listOfGenre.addAll(Arrays.asList(genres).subList(0, 8));
        db = new SongDatabase(listOfGenre);
        udb = new UserDatabase();
        writer = new JsonWriter(SONG_STORE);
        reader = new JsonReader(SONG_STORE);
        userWriter = new JsonUserWriter(USER_STORE);
        userReader = new JsonUserReader(USER_STORE);
        initLoginPanel();
    }

    // MODIFIES: this
    // EFFECTS: sets the current content pane to the login panel
    private void initLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(xsize, ysize));
        loginPanel.setBackground(Color.getHSBColor(0, 0, (float) 0.15));
        loginPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        initLogo();
        initHeading();
        initFields();
        initMessage();
        initButtons();
        setContentPane(loginPanel);
    }

    // MODIFIES: this
    // EFFECTS: initializes the logo for the login panel
    private void initLogo() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("res/logo.png"));
        } catch (IOException e) {
            System.out.println("error loading logo");
        }
        assert img != null;
        Image dimg = img.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(dimg);
        JLabel logo = new JLabel("", icon, JLabel.CENTER);
        gbc.gridy = 0;
        loginPanel.add(logo, gbc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the heading for the login panel
    private void initHeading() {
        JPanel wrapper = new JPanel();
        padding = BorderFactory.createEmptyBorder(50, 10, 10, 10);
        heading = new JLabel("Login or Register to continue");
        heading.setFont(font);
        heading.setForeground(Color.WHITE);
        heading.setBorder(padding);
        wrapper.add(heading);
        wrapper.setBackground(null);
        gbc.insets = new Insets(10, 10, 50, 10);
        gbc.gridy = 1;
        loginPanel.add(wrapper, gbc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the wrapper panel for the login fields
    private void initFields() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        wrapper.setBackground(null);
        GridBagConstraints gc = new GridBagConstraints();
        initUsername(wrapper, gc);
        initPassword(wrapper, gc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        loginPanel.add(wrapper, gbc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the username field and label
    private void initUsername(JPanel wrapper, GridBagConstraints gc) {
        userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Lato-Light", Font.PLAIN, 60));
        userLabel.setForeground(Color.WHITE);
        username = new JTextField(10);
        username.setFont(new Font("Lato-Regular", Font.PLAIN, 32));
        username.setPreferredSize(new Dimension(600, 50));
        gc.insets = new Insets(10, 10, 30, 10);
        wrapper.add(userLabel, gc);
        wrapper.add(username, gc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the password field and label
    private void initPassword(JPanel wrapper, GridBagConstraints gc) {
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Lato-Light", Font.PLAIN, 60));
        passwordLabel.setForeground(Color.WHITE);
        password = new JPasswordField(10);
        password.setFont(new Font("Lato-Regular", Font.PLAIN, 32));
        password.setPreferredSize(new Dimension(600, 50));
        gc.insets = new Insets(10, 10, 10, 10);
        gc.gridy = 1;
        wrapper.add(passwordLabel, gc);
        wrapper.add(password, gc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the response message
    private void initMessage() {
        JPanel wrapper = new JPanel();
        GridBagConstraints gc = new GridBagConstraints();
        message = new JLabel("");
        message.setFont(new Font("Lato-Light", Font.PLAIN, 30));
        message.setForeground(Color.WHITE);
        wrapper.setBackground(null);
        wrapper.add(message, gc);
        gbc.gridy = 3;
        loginPanel.add(wrapper, gbc);
    }

    // MODIFIES: this
    // EFFECTS: initializes the username field and label
    private void initButtons() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        login = new JButton("Sign in");
        register = new JButton("Register");
        login.addActionListener(this);
        register.addActionListener(this);
        login.setPreferredSize(new Dimension(400, 100));
        register.setPreferredSize(new Dimension(400, 100));
        login.setFont(new Font("Lato-Light", Font.PLAIN, 30));
        register.setFont(new Font("Lato-Light", Font.PLAIN, 30));
        login.setForeground(Color.WHITE);
        register.setForeground(Color.WHITE);
        login.setBorder(BorderFactory.createEmptyBorder());
        login.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
        register.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
        wrapper.add(login, gc);
        wrapper.add(register, gc);
        gbc.gridy = 4;
        loginPanel.add(wrapper, gbc);
    }

    // MODIFIES: this
    // EFFECTS: invokes either the login or register procedure based on the caller
    @Override
    public void actionPerformed(ActionEvent e) {
        String user = username.getText().toLowerCase().trim();
        String pass = password.getText().trim();
        if (e.getSource() == login) {
            login(user, pass);
            if (authenticated) {
                resetView();
            }
        } else if (e.getSource() == register) {
            register(user, pass);
            if (authenticated) {
                resetView();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: validates the login fields and then authenticates, showing a message if invalid.
    // else sets the current user to this user
    private void login(String user, String pass) {
        if (user.isEmpty() || pass.isEmpty()) {
            message.setText("Username and Password cannot be empty");
        } else {
            boolean isValid = checkCredentials(user, pass);
            if (!isValid) {
                message.setText("Invalid Username/Password");
            } else {
                currentUser = user;
                authenticated = true;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: validates the register fields and then authenticates, showing a message if user already exists.
    // else invokes method to save this new user.
    private void register(String username, String password) {
        if (username.length() < 5 || password.length() < 5) {
            message.setText("Username/Password must be at least 5 characters");
        } else {
            User user = null;
            try {
                user = new User(username, password);
            } catch (NoSuchAlgorithmException e) {
                message.setText("error!");
            }
            boolean success = udb.addUser(user);
            if (!success) {
                message.setText("User with that username already exists!");
            } else {
                if (user != null) {
                    saveUsers(user);
                    message.setText("success");
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: returns false if the user does not exist in the database or
    //          the password entered is incorrect, else returns true.
    //          show a "Something went wrong" message if NoSuchAlgorithmException is caught
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
    // EFFECTS: saves the current user database to the file at the relative path USER_STORE
    // if FileNotFoundException is caught - prints out Unable to write to file: <Address of file>
    // else sets currentUser to the entered user's username, sets authenticated to true.
    private void saveUsers(User user) {
        try {
            userWriter.open();
            userWriter.write(udb);
            authenticated = true;
            currentUser = user.getUsername();
            userWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + USER_STORE);
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

    // EFFECTS: saves the current database to the file at the relative path SONG_STORE
    //          prints out Unable to write to file: <Address of file> if FileNotFoundException is caught
    private void saveDialog() {
        try {
            writer.open();
            writer.write(db);
            writer.close();
            JOptionPane.showMessageDialog(this, "Database Saved!", "Success!", JOptionPane.PLAIN_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to write database!", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // EFFECTS: prompts user to save database before closing, saves and closes if response is yes, else just closes
    private void saveOnExit() {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Save Database before closing?", "Save Database?",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveDialog();
                }
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: repaints the content pane and revalidates the component tree, adding the main view.
    private void resetView() {
        setContentPane(new MainView());
        validate();
        repaint();
    }
}