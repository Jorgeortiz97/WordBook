package application.view;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import application.model.Stat;
import application.model.Topic;
import application.model.Word;
import application.persistence.SQLFactoryDAO;
import application.persistence.SQLTopicDAO;
import application.persistence.util.SQLGeneratedKeyException;
import application.util.FatalError;
import application.util.InfoDialog;
import application.util.Player;
import application.util.SevereError;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ListenToItController implements IController {

	@FXML
	private VBox board;

	// Attributes
	private String[] solutions;
	private TextField[] answers;
	private List<Long> topicsId = new LinkedList<Long>();

	private MenuController mc;

	public ListenToItController(MenuController mc) {
		this.mc = mc;
	}

	private class PlayButtonHandler implements EventHandler<ActionEvent> {
		private int row;

		public PlayButtonHandler(int row) {
			this.row = row;
		}

		@Override
		public void handle(ActionEvent event) {
			play(row);
		}
	}

	public boolean init(Object argument) {
		if (!(argument instanceof List))
			return false;

		topicsId.clear();

		@SuppressWarnings("unchecked")
		List<Topic> topics = (List<Topic>) argument;

		List<Word> allWords = new LinkedList<Word>();
		Topic topic;
		SQLTopicDAO instance = SQLFactoryDAO.getInstance().getTopicDAO();
		try {
			for (Topic t : topics) {
				topicsId.add(t.getId());
				topic = instance.findById(t.getId());
				allWords.addAll(topic.getWords());
			}
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_GET);
		}

		if (allWords.size() < 5) {
			SevereError.show(SevereError.NOT_ENOUGH_ENTRIES);
			return false;
		}

		int totRows = Math.min(allWords.size(), 20);

		// Clear pane:
		board.getChildren().clear();

		solutions = new String[totRows];
		answers = new TextField[totRows];
		HBox[] boxes = new HBox[totRows];

		Random rnd = new Random();

		Image image = new Image("file:img/speaker.png");

		for (int row = 0; row < totRows; row++) {
			int r = rnd.nextInt(allWords.size());
			Word w = allWords.get(r);
			allWords.remove(r);
			solutions[row] = w.getName().toLowerCase();

			Button button = new Button();
			button.setGraphic(new ImageView(image));
			button.setOnAction(new PlayButtonHandler(row));
			answers[row] = new TextField();

			boxes[row] = new HBox(15);
			boxes[row].getChildren().addAll(button, answers[row]);
		}

		board.getChildren().addAll(boxes);
		return true;
	}

	public void handleGoButton() {

		int hits = 0;
		for (int i = 0; i < solutions.length; i++)
			if (answers[i].getText().toLowerCase().equals(solutions[i]))
				hits++;
		InfoDialog.show("Final score",
				"You have successfully answered " + hits + " of " + solutions.length + " questions.");

		Stat stat = new Stat(topicsId, hits, solutions.length - hits, Stat.LISTEN_TO_IT);
		try {
			SQLFactoryDAO.getInstance().getStatDAO().insert(stat);
		} catch (SQLException | SQLGeneratedKeyException e) {
			SevereError.show(SevereError.DB_ADD);
		}

		mc.changeView(MenuController.GAME_MENU);
	}

	public void play(int row) {
		if (!Player.play(solutions[row])) {
			SevereError.show(SevereError.PLAY_ERROR);
			mc.changeView(MenuController.GAME_MENU);
		}
	}

	@Override
	public boolean onlyFirstTime() {
		return false;
	}

	@Override
	public int backPane() {
		return MenuController.TOPIC_GAME;
	}

}
