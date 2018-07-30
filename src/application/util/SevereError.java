package application.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SevereError {

    public final static int DB_DELE = 0;
    public final static int DB_ADD = 1;
    public final static int DB_DUP = 2;
    public final static int DB_ECR = 3;
    public final static int DB_UPD = 4;
    public final static int WORDTRAN_REQFIELDS = 5;
    public final static int DIFFICULTY_FIELD = 6;
    public final static int DB_GET = 7;
    public final static int IMPORT_FAILED = 8;
    public final static int NOT_ENOUGH_TOPICS = 9;
    public final static int INCORRECT_TOPIC_SELECTION = 10;
    public final static int NOT_ENOUGH_ENTRIES = 11;
    public final static int PLAY_ERROR = 12;
    public final static int NOT_ENOUGH_GAME_DATA = 13;
    public final static int CANT_OPEN_BROWSER = 14;
    public final static int NO_CONTENT_TO_PRINT = 15;
    public final static int PRINT_ERROR = 16;

    private final static String[][] messages = {
        {"Database error", "There was an error when attempting to delete an object."},
        {"Database error", "There was an error when attempting to create an object."},
        {"Database error", "Impossible to create a new object. Reason: duplicate."},
        {"Database error", "Impossible to create a new essay. Reason: I/O error."},
        {"Database error", "There was an error when attempting to update an object."},
        {"Input error", "'word' and 'translation' are required fields."},
        {"Input error", "'difficulty' must be a positive integer."},
        {"Database error", "There was an error when attempting to recover an object."},
        {"Error importing file", "There was an error when attempting to import a XML file."},
        {"Need more topics", "You can not choose this game: not enough topics!"},
        {"Incorrect topic selection amount", "You must select the indicated amount of topics."},
        {"Need more entries", "Selected topics don't have the enought amount of words."},
        {"Play error", "There was an error when attempting to listen to the word."},
        {"Error showing stats", "There is not enough information to show statistics."},
        {"Error opening browser", "There was an error when attempting to open the browser."},
        {"No content", "There is no content for printing the document."},
        {"Error printing", "There was an error when attempting to print the document."},
    };

    public static void show(int errorNo) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Severe error!");
        alert.setHeaderText(messages[errorNo][0]);
        alert.setContentText(messages[errorNo][1]);
        alert.showAndWait();
    }
}
