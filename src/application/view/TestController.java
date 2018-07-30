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
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class TestController implements IController {

	private final static int NUM_OPT = 3;

	@FXML
	private ScrollPane scrollPane;

	// Attributes
	private int[] solutions;
	private RadioButton[][] rb;
	private List<Long> topicsId = new LinkedList<Long>();

	private MenuController mc;

	public TestController(MenuController mc) {
		this.mc = mc;
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

		if (allWords.size() < 5 * NUM_OPT) {
			SevereError.show(SevereError.NOT_ENOUGH_ENTRIES);
			return false;
		}

		int totRows = Math.min(allWords.size() / NUM_OPT, 20);
		int row = 0;

		// Clear pane:
		GridPane board = new GridPane();
		scrollPane.setContent(board);

		solutions = new int[totRows];
		rb = new RadioButton[totRows][NUM_OPT];

		Random rnd = new Random();

		while (row < totRows) {

			Word[] words = new Word[NUM_OPT];
			ToggleGroup tg = new ToggleGroup();
			int r;

			Label l = new Label();
			board.add(l, 0, row, 1, 1);

			for (int i = 0; i < NUM_OPT; i++) {
				r = rnd.nextInt(allWords.size());
				words[i] = allWords.get(r);
				allWords.remove(r);
				rb[row][i] = new RadioButton(words[i].getTranslation());
				rb[row][i].setToggleGroup(tg);
				board.add(rb[row][i], i + 1, row, 1, 1);
			}

			solutions[row] = rnd.nextInt(NUM_OPT);
			l.setText(words[solutions[row]].getName());

			row++;

		}

		return true;
	}

	public void handleGoButton() {

		int hits = 0;
		for (int i = 0; i < solutions.length; i++)
			if (rb[i][solutions[i]].isSelected())
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

	@Override
	public boolean onlyFirstTime() {
		return false;
	}

	@Override
	public int backPane() {
		return MenuController.TOPIC_GAME;
	}
}
