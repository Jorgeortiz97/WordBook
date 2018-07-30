package application.util;

import java.util.List;

import application.style.Style;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MultSelectionDialog {

	private static boolean[] selectedOpts;

	public static boolean[] show(String title, String message, List<String> selection) {

		selectedOpts = null;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setWidth(500);
		window.setHeight(500);
		window.setResizable(true);
		Label label = new Label();
		label.setText(message);

		CheckBox[] opts = new CheckBox[selection.size()];
		for (int i = 0; i < selection.size(); i++)
			opts[i] = new CheckBox(selection.get(i));

		VBox optionsPane = new VBox(15);
		optionsPane.setId("pane");
		optionsPane.getChildren().addAll(opts);

		ScrollPane spane = new ScrollPane();
		spane.setContent(optionsPane);

		Button okButton = new Button("Ok");

		okButton.setOnAction(e -> {
			selectedOpts = new boolean[selection.size()];
			for (int i = 0; i < selectedOpts.length; i++)
				selectedOpts[i] = opts[i].isSelected();
			window.close();
		});

		VBox layout = new VBox(18);
		layout.setAlignment(Pos.CENTER);
		okButton.setAlignment(Pos.CENTER_RIGHT);

		layout.getChildren().addAll(label, spane, okButton);

		layout.setId("pane");
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(Style.getInstance().getStyle());
		window.setScene(scene);
		window.showAndWait();

		return selectedOpts;
	}

}
