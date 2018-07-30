package application.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import application.style.Style;
import javafx.geometry.*;

public class MultipleInputDialog {

    public final static int WORD_ADD = 0;
    public final static int WORD_EDIT = 1;
	public final static int TOPIC_EDIT = 2;

    private final static String[][] messages = {
        {"New word", "Enter values for the new word"},
        {"Edit word", "Enter new values for the word"},
        {"New topic name", "Please enter a new name for the topic."},
    };

    private final static String[][] labelsStr = {
            {"Word:", "Translation:", "Other:", "Difficulty:"},
            {"Word:", "Translation:", "Other:", "Difficulty:"},
            {"New name:"},
        };

    private static boolean answer;

    public static MultipleInputAnswer show(int noMsg, String[] inputs) {

    	answer = false;
        if (inputs == null || inputs.length == 0 || inputs.length != labelsStr[noMsg].length) throw new IllegalArgumentException();
        int length = inputs.length;

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(messages[noMsg][0]);
        window.setWidth(400);
        window.setHeight(400);
        window.setResizable(false);
        Label label = new Label();
        label.setText(messages[noMsg][1]);


        // Create two buttons
        Button editButton = new Button(messages[noMsg][0]);
        Button cancelButton = new Button("Cancel");

        //Clicking will set answer and close window
        editButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(18);

        layout.getChildren().add(label);
        HBox[] hboxes = new HBox[length];
        Label[] labels = new Label[length];
        TextField[] fields = new TextField[length];
        for (int i = 0; i < length; i++) {
        	hboxes[i] = new HBox(5);
        	hboxes[i].setAlignment(Pos.CENTER);
        	labels[i] = new Label(labelsStr[noMsg][i]);
        	fields[i] = new TextField(inputs[i]);
        	hboxes[i].getChildren().addAll(labels[i], fields[i]);
        	layout.getChildren().add(hboxes[i]);
        }

        HBox layoutBtn = new HBox(10);
        layoutBtn.setAlignment(Pos.CENTER);
        layoutBtn.getChildren().addAll(editButton, cancelButton);
        layout.getChildren().add(layoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setId("pane");
        Scene scene = new Scene(layout);
        scene.getStylesheets().add(Style.getInstance().getStyle());
        window.setScene(scene);
        window.showAndWait();

        MultipleInputAnswer mia;
        if (answer) {
            String[] answers = new String[length];
            for (int i = 0; i < length; i++)
            	answers[i] = fields[i].getText();
            mia = new MultipleInputAnswer(true, answers);
        } else
        	mia = new MultipleInputAnswer(false);

        return mia;
    }

}
