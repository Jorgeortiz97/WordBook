package application.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FatalError {

	public final static int DB_INIT = 0;
	public final static int DB_GET = 1;
	public final static int RES_NOT_FOUND = 2;


	private final static String[][] messages = {
		{"Database error", "There was an error when the database was starting."},
		{"Database error", "There was an error when attempting to retrieve an object."},
		{"Resource not found", "There was an error when attempting to initialize the view."},
	};

	public static void show(int errorNo) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fatal error!");
		alert.setHeaderText(messages[errorNo][0]);
		alert.setContentText(messages[errorNo][1]);
		alert.showAndWait();
		System.exit(0);
	}
}
