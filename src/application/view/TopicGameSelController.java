package application.view;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import application.model.Topic;
import application.persistence.SQLFactoryDAO;
import application.util.FatalError;
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TopicGameSelController implements IController {


	public final static int CATEGORIZATOR = 0;
	public final static int TEST = 1;
	public final static int LISTEN_TO_IT = 2;

	public final static String[] NAMES = {
		"Categorizator",
		"WDIM?",
		"Listen to it"
	};

	private final static int MIN = 0;
	private final static int MAX = 1;

	private int gameMode;

	// Max and min values for each game:
	private final static int[][] VALUES = {
		{2, 4},
		{1, 20},
		{1, 20}
	};


	@FXML
	private VBox container;
	@FXML
	private Label label;


	private CheckBox[] boxes;
	private List<Topic> topics;

	private MenuController mc;

	public TopicGameSelController(MenuController mc) {
		this.mc = mc;
	}


	public boolean init(Object arg) {
		if (!(arg instanceof Integer))
			return false;
		this.gameMode = (int) arg;

		try {
			topics = SQLFactoryDAO.getInstance().getTopicDAO().findAll();
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_GET);
		}

		if (topics.size() < VALUES[gameMode][MIN]) {
			SevereError.show(SevereError.NOT_ENOUGH_TOPICS);
			return false;
		}

		label.setText("Choose between " + VALUES[gameMode][MIN] + " and " + VALUES[gameMode][MAX] + " topics.");

		// Clean the pane:
		container.getChildren().clear();

		boxes = new CheckBox[topics.size()];

		for (int i = 0; i < topics.size(); i++)
			boxes[i] = new CheckBox(topics.get(i).getName());

		container.getChildren().addAll(boxes);

		return true;
	}

	public void handleOkButton() {

		List<Topic> selectedTopics = new LinkedList<Topic>();
		for (int i = 0; i < topics.size(); i++)
			if (boxes[i].isSelected())
				selectedTopics.add(topics.get(i));

		int size = selectedTopics.size();

		if (size > VALUES[gameMode][MAX] || size < VALUES[gameMode][MIN]) {
			SevereError.show(SevereError.INCORRECT_TOPIC_SELECTION);
			return;
		}

		mc.setTopicsForGame(selectedTopics);
		switch (gameMode) {
		case CATEGORIZATOR:
			mc.changeView(MenuController.CATEG_VIEW);
			break;
		case TEST:
			mc.changeView(MenuController.TEST_VIEW);
			break;
		default:
			mc.changeView(MenuController.LISTEN_VIEW);
		}
	}


	@Override
	public boolean onlyFirstTime() {
		return false;
	}


	@Override
	public int backPane() {
		return MenuController.GAME_MENU;
	}
}
