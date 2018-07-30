package application.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import application.style.Style;
import javafx.geometry.*;

public class ConfirmDialog {

	private static boolean answer = false;

    public static boolean show(String title, String message) {
    	answer = false;
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setWidth(500);
        window.setHeight(140);
        window.setResizable(false);
        Label label = new Label();
        label.setText(message);
        label.setWrapText(true);

        // Create two buttons
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");

        //Clicking will set answer and close window
        okButton.setOnAction(e -> {
        	answer = true;
        	window.close();
        });
        cancelButton.setOnAction(e -> {
        	answer = false;
        	window.close();
        });

        HBox buttons = new HBox(15);
        buttons.getChildren().addAll(cancelButton, okButton);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(18);
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(label, buttons);

        layout.setId("pane");
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Style.getInstance().getStyle());
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
