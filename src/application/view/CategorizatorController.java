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

public class CategorizatorController implements IController {

	@FXML
	private ScrollPane scrollPane;

	// Attributes
	private int[] solutions;
	private RadioButton[][] rb;

	private MenuController mc;

	private List<Long> topicsId = new LinkedList<Long>();

	public CategorizatorController(MenuController mc) {
		this.mc = mc;
	}

	public boolean init(Object argument) {
		if (!(argument instanceof List))
			return false;

		List<Topic> topics = (List<Topic>) argument;

		int size = topics.size(); // Number of columns
		@SuppressWarnings("unchecked")
		LinkedList<Word>[] lists = new LinkedList[size];

		SQLTopicDAO instance = SQLFactoryDAO.getInstance().getTopicDAO();
		Topic topic;

		int elements = 20; // Maximum number of elements (rows)

		topicsId.clear();
		long id;

		try {
			for (int i = 0; i < size; i++) {
				id = topics.get(i).getId();
				topicsId.add(id);
				topic = instance.findById(id);
				lists[i] = topic.getWords();
				elements = Math.min(elements, lists[i].size());
			}
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_GET);
		}

		if (elements < 5) {
			SevereError.show(SevereError.NOT_ENOUGH_ENTRIES);
			return false;
		}

		// Clear pane:
		GridPane board = new GridPane();
		scrollPane.setContent(board);

		solutions = new int[elements];
		rb = new RadioButton[elements][size];

		Random rnd = new Random();

		List<Word> sel;
		int selWord;
		Word w;
		ToggleGroup tg;

		for (int i = 0; i < elements; i++) {
			// Solution
			solutions[i] = rnd.nextInt(size);

			sel = lists[solutions[i]];
			selWord = rnd.nextInt(sel.size());
			w = sel.get(selWord);
			sel.remove(selWord);

			// FX
			tg = new ToggleGroup();

			board.setVgap(10.0);
			board.add(new Label(w.getName()), 0, i, 1, 1);
			for (int o = 0; o < size; o++) {
				rb[i][o] = new RadioButton(topics.get(o).getName());
				board.add(rb[i][o], o + 1, i, 1, 1);
				rb[i][o].setToggleGroup(tg);
			}

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

		Stat stat = new Stat(topicsId, hits, solutions.length - hits, Stat.CATEGORIZATOR);
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
