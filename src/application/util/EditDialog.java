package application.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import application.style.Style;
import application.view.IStylish;
import javafx.geometry.*;

public class EditDialog implements IStylish {

	public final static int ESSAY_ADD = 0;
	public final static int ESSAY_EDIT = 1;
	public final static int GRAMMAR_ADD = 2;
	public final static int GRAMMAR_EDIT = 3;

	private final static String[][] messages = {
		{ "New essay", "Enter values for the new essay" },
		{ "Edit essay", "Enter new values for the essay" },
		{ "New grammar entry", "Enter values for the new grammar entry" },
		{ "Edit grammar entry", "Enter new values for the grammar entry" },
	};

	private static boolean answer;

	private static EditDialog instance = new EditDialog();


	private Stage window = new Stage();
	private Scene scene;
	private TextField[] fields =  {new TextField(), new TextField()};;
	private Label label = new Label();
	private TextArea content = new TextArea();
	private Button actionButton = new Button();
	private Button cancelButton = new Button("Cancel");

	// Default font size
	private int size = 18;

	public EditDialog() {
		answer = false;

		AnchorPane layout = new AnchorPane();
		layout.setId("pane");
		scene = new Scene(layout);

		Style style = Style.getInstance();
		styleChange(style.getStyle());
		style.addListener(this);

		window.initModality(Modality.APPLICATION_MODAL);
		window.setMinWidth(600);
		window.setMinHeight(600);
		window.setScene(scene);

		actionButton.setOnAction(e -> {
			answer = true;
			window.close();
		});
		cancelButton.setOnAction(e -> {
			answer = false;
			window.close();
		});


		Label[] labels = {new Label("Title:"), new Label("Difficulty:")};


		AnchorPane.setTopAnchor(label, 10.0);
		AnchorPane.setLeftAnchor(label, 20.0);
		AnchorPane.setRightAnchor(label, 20.0);

		HBox inputLay = new HBox(20);
		inputLay.setAlignment(Pos.CENTER);
		fields[0].setPrefWidth(140.0);
		fields[1].setPrefWidth(50.0);
		inputLay.getChildren().addAll(labels[0], fields[0], labels[1], fields[1]);
		AnchorPane.setTopAnchor(inputLay, 40.0);
		AnchorPane.setLeftAnchor(inputLay, 20.0);
		AnchorPane.setRightAnchor(inputLay, 20.0);


		AnchorPane.setTopAnchor(content, 100.0);
		AnchorPane.setLeftAnchor(content, 10.0);
		AnchorPane.setRightAnchor(content, 10.0);
		AnchorPane.setBottomAnchor(content, 100.0);

		content.setFont(new Font("Segoe UI Light", size));

		HBox sizeLay = new HBox(20);
		Label sizeLbl = new Label("Change font size:");
		Button increaseBtn = new Button("+");
		increaseBtn.setOnAction(e -> {
			content.setFont(new Font("Segoe UI Light", ++size));
		});
		Button decreaseBtn = new Button("-");
		decreaseBtn.setOnAction(e -> {
			if (size > 10)
				content.setFont(new Font("Segoe UI Light", --size));
		});
		sizeLay.setAlignment(Pos.CENTER);
		sizeLay.getChildren().addAll(sizeLbl, increaseBtn, decreaseBtn);
		AnchorPane.setBottomAnchor(sizeLay, 60.0);
		AnchorPane.setLeftAnchor(sizeLay, 20.0);
		AnchorPane.setRightAnchor(sizeLay, 20.0);

		HBox btnLay = new HBox(20);
		btnLay.setAlignment(Pos.CENTER);
		btnLay.getChildren().addAll(actionButton, cancelButton);
		AnchorPane.setBottomAnchor(btnLay, 20.0);
		AnchorPane.setLeftAnchor(btnLay, 20.0);
		AnchorPane.setRightAnchor(btnLay, 20.0);

		layout.getChildren().addAll(label, inputLay, content, sizeLay, btnLay);

	}

	public static MultipleInputAnswer show(int noMsg, String[] data) {

		answer = false;

		// Change GUI Info:
		instance.window.setTitle(messages[noMsg][0]);
		instance.actionButton.setText(messages[noMsg][0]);
		instance.label.setText(messages[noMsg][1]);

    	if (data != null) {
    		instance.fields[0].setText(data[0]);
    		instance.fields[1].setText(data[1]);
    		instance.content.setText(data[2]);
    	}

		instance.window.showAndWait();

		MultipleInputAnswer mia;
		if (answer) {
			String[] answers = {
				instance.fields[0].getText(),
				instance.fields[1].getText(),
				instance.content.getText()
			};
			mia = new MultipleInputAnswer(true, answers);
		} else
			mia = new MultipleInputAnswer(false);

		return mia;
	}

	@Override
	public void styleChange(String newStyle) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(newStyle);
	}

}
