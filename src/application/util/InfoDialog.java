package application.util;

import application.style.Style;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InfoDialog {

	public static void show(String title, String message) {

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setResizable(false);
		Label label = new Label();
		label.setText(message);
		label.setWrapText(true);

		// Create two buttons
		Button okButton = new Button("Ok");

		// Clicking will set answer and close window
		okButton.setOnAction(e -> window.close());

		VBox layout = new VBox(18);
		layout.setAlignment(Pos.CENTER);
		okButton.setAlignment(Pos.CENTER_RIGHT);
		layout.getChildren().addAll(label, okButton);

		layout.setId("pane");
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(Style.getInstance().getStyle());
		window.setScene(scene);
		window.showAndWait();
	}

}
