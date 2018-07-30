package application.util;

import java.util.List;

import application.style.Style;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectionDialog {

	private static int answer;
	private static int selectedOpt;

	@SuppressWarnings("rawtypes")
	private static class OptButtonHandler implements EventHandler {
		private int opt;

		public OptButtonHandler(int opt) {
			this.opt = opt;
		}

		@Override
		public void handle(Event arg0) {
			selectedOpt = opt;
		}
	}

	public static int show(String title, String message, List<String> selection) {

		selectedOpt = -1;
		answer = -1;

		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setWidth(400);
		window.setHeight(500);
		window.setResizable(true);
		Label label = new Label();
		label.setText(message);

		ToggleGroup tg = new ToggleGroup();

		RadioButton[] opts = new RadioButton[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			opts[i] = new RadioButton(selection.get(i));
			opts[i].setOnAction(new OptButtonHandler(i));
			opts[i].setToggleGroup(tg);
		}
		VBox optionsPane = new VBox(15);
		optionsPane.setId("pane");
		optionsPane.getChildren().addAll(opts);

		ScrollPane spane = new ScrollPane();
		spane.setContent(optionsPane);

		Button okButton = new Button("Ok");

		okButton.setOnAction(e -> {
			answer = selectedOpt;
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

		return answer;
	}

}
