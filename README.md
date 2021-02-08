# SongBase - Personal Project

## Application Features  

The application will allow multiple registered users to add their favorite songs (as text input) into different categories 
(Hip-hop, Rap, Pop, RnB, Jazz, etc.). Users will be able to **view the songs in each category**, and **add their own**. 
Users have the option of providing a URL for a song. 
Clicking on songs will redirect the user to a URL of the song (if provided).

All user data will be stored locally with encryption(hashing) in a json file. Song data will be stored locally in a json file,
without hashing. 

This project can be used by multiple users to share songs with each other and have an easy way of accessing them in a 
single database, split into different categories. It can serve as a shared playlist of songs. This codebase could be expanded into a web application which has 
proper user authentication and can be used as a database among friends who want to share songs. 

This project is of interest to me because like most people I spend a lot of time listening to music and sharing it
with my friends. Having a database with a user interface where I can see all the songs that people have added, split
by genres and having a way to access them from the application itself is ideal.  


## User Stories

#### As a user:

- I want to be able to authenticate with the application by either:
    - registering as a new user
    - logging in as an existing user
- I want my user authentication info to be saved securely.     
- I want to be able to add my songs to one of many genres -> Pop, Rock, Hip-hop, etc.
- I want to be able to view all songs in each genre.
    - I want to be able to click on a button to redirect me to the song audio/video if added by the original user
- I want to be able to search for all songs by an artist.
- I want to be able to delete songs, but only the ones that are added by **me**.
- I want to be able to view which user added a particular song.
- I want to be able to save the current song database.
    - I want to be reminded to save my database before quitting the application
- I want to be able to load my song database from a file.

## Phase 4: Task 2

Option 1 - Test and design a class in your model package that is robust.

Relevant Classes - 
- InvalidUrlException in the exceptions package
- Song in the model package: constructor checks whether initialized url is valid or not. Throws exception if it is not.
- GenreTest in the model package: contains 2 tests, one initializing a song constructor with valid urls, one with invalid.
- checkUrl method in the Song class: uses the UrlValidator class from the apache commons library to validate url.

## Phase 4: Task 3

- Have the list of genre (hip-hop, rock, etc.) be an enum instead of declaring them individually 
  in my UI classes (as seen in GUI) and then adding them as a list into the Song Database. 
    - I could use the default toString in an enum to declare a genre with the respective enum name. 
    - This way I would be able to iterate over all enums, create a genre object, and then add it to the database.
- Split MainView into smaller sub views
    - Although not visible in the UML Diagram, the MainView class is over 700 lines.
    - I would split this into smaller components that share the same UI logic.
    - For example, the "delete song" and "search songs" views are identical, so the UI could be captured in a single 
    component whereas the action listener logic could be different. 