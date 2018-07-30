package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import application.MainApp;
import application.model.Essay;
import application.model.Grammar;
import application.model.Stat;
import application.model.Topic;
import application.persistence.SQLEssayDAO;
import application.persistence.SQLFactoryDAO;
import application.persistence.SQLGrammarDAO;
import application.persistence.SQLTopicDAO;
import application.style.Style;
import application.util.ConfirmDialog;
import application.util.InfoDialog;
import application.util.LineChartDialog;
import application.util.MultSelectionDialog;
import application.util.RTFCreator;
import application.util.SelectionDialog;
import application.util.SevereError;
import application.util.XMLReader;
import application.util.XMLWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuController implements IStylish, IController {

	// Public Attributes
	public final static int INITIAL_VIEW = 0;
	public final static int TOPIC_VIEW = 1;
	public final static int VOCAB_VIEW = 2;
	public final static int ESSAY_VIEW = 3;
	public final static int GRAMMAR_VIEW = 4;
	public final static int GAME_MENU = 5;
	public final static int TOPIC_GAME = 6;
	public final static int CATEG_VIEW = 7;
	public final static int TEST_VIEW = 8;
	public final static int LISTEN_VIEW = 9;

	private int current = INITIAL_VIEW;

	private FullPane[] pane = { new FullPane(this, "InitialMenu"),
			new FullPane(new TopicController(this), "TopicSelection"),
			new FullPane(new VocabularyController(), "VocabularyView"),
			new FullPane(new EssayController(), "EssayView"), new FullPane(new GrammarController(), "GrammarView"),
			new FullPane(this, "GameMenu"), new FullPane(new TopicGameSelController(this), "TopicGameSelection"),
			new FullPane(new CategorizatorController(this), "Categorizator"),
			new FullPane(new TestController(this), "Test"),
			new FullPane(new ListenToItController(this), "ListenToIt"), };

	// GUI Attributes
	private AnchorPane root;
	@FXML
	private Button backButton;
	private FileChooser fileChooser = new FileChooser();
	private FileChooser printFileChooser = new FileChooser();
	private Stage stage;

	@FXML
	private Label version;

	// Logic Attributes
	private boolean vocabOption;
	private Topic topic;

	private List<Topic> topicsForGame;
	private int gameOption;

	public Pane getView(FullPane fp) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(fp.getStyle() + ".fxml"));

		loader.setController(fp.getController());
		try {
			return loader.load();
		} catch (IOException e) {
			return null;
		}
	}

	public MenuController(Stage stage) {
		this.stage = stage;
	};

	public boolean init(Object arg) {
		if (!(arg instanceof Pane))
			return false;

		root = (AnchorPane) arg;

		Style.getInstance().addListener(this);
		styleChange(Style.getInstance().getStyle());

		Pane p;
		for (int i = 0; i < pane.length; i++) {
			p = getView(pane[i]);
			if (p == null)
				return false;
			pane[i].setPane(p);
		}

		// Set version
		version.setText(MainApp.VERSION);
		version.setStyle("-fx-font-size:8pt;");

		// Show default pane
		changeView(current);

		// Initialize grammar pane
		pane[GRAMMAR_VIEW].getController().init(null);

		// Initialize import & export "filechooser"
		fileChooser.setTitle("Choose a XML file");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Initialize import & export "filechooser"
		printFileChooser.setTitle("Choose a XML file");
		FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("RTF files (*.rtf)", "*.rtf");
		printFileChooser.getExtensionFilters().add(extFilter2);

		return true;
	}

	@Override
	public boolean onlyFirstTime() {
		return true;
	}

	@Override
	public int backPane() {
		return MenuController.INITIAL_VIEW;
	}

	private void setNewView(int newView) {
		root.getChildren().remove(pane[current].getPane());
		Pane newPane = pane[newView].getPane();
		root.getChildren().add(newPane);
		AnchorPane.setTopAnchor(newPane, 30.0);
		AnchorPane.setLeftAnchor(newPane, 30.0);
		AnchorPane.setRightAnchor(newPane, 30.0);
		AnchorPane.setBottomAnchor(newPane, 80.0);
		// Update 'current' value
		current = newView;
	}

	public void changeView(int newView) {

		if (newView == INITIAL_VIEW)
			backButton.setText("Exit");
		else {
			boolean ok = true;
			IController con = pane[newView].getController();
			if (!con.onlyFirstTime()) {
				Object arg = null;

				switch (newView) {
				case VOCAB_VIEW:
				case ESSAY_VIEW:
					arg = topic;
					break;
				case TOPIC_GAME:
					arg = gameOption;
					break;
				case CATEG_VIEW:
				case TEST_VIEW:
				case LISTEN_VIEW:
					arg = topicsForGame;
					break;
				default:
					break;
				}
				ok = con.init(arg);
			}
			if (!ok)
				return;
			backButton.setText("Back");
		}
		setNewView(newView);
	}

	public void handleBackButton() {
		// If it is main menu: exit program.
		if (current == INITIAL_VIEW)
			handleExitButton();

		// Else
		changeView(pane[current].getController().backPane());
	}

	public void handleButtonVocabulary() {
		vocabOption = true;
		changeView(TOPIC_VIEW);
	}

	public void handleButtonEssays() {
		vocabOption = false;
		changeView(TOPIC_VIEW);
	}

	public void handleButtonGrammar() {
		changeView(GRAMMAR_VIEW);
	}

	public void handleButtonPractice() {
		changeView(GAME_MENU);
	}

	public void handleExitButton() {
		if (ConfirmDialog.show("Exit", "Do you really want to exit?"))
			System.exit(0);
	}

	public boolean isVocabularySelected() {
		return vocabOption;
	}

	public void changeTopic(Topic newTopic) {
		topic = newTopic;
	}

	// Handle menu events:
	public void handleImport() {
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			XMLReader.readXMLFile(file.getAbsolutePath());
		}
	}

	public void handleExport() {
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			XMLWriter.writeXMLFile(file.getAbsolutePath());
		}
	}

	public void handlePrintDoc() {

		File file = printFileChooser.showSaveDialog(stage);
		if (file == null)
			return;

		boolean noTopics = false, noGrammar = false;
		List<String> names = new LinkedList<String>();

		try {
			List<Topic> topics = SQLFactoryDAO.getInstance().getTopicDAO().findAll();
			List<Topic> finalTopics = new LinkedList<Topic>();

			if (topics.size() == 0)
				noTopics = true;
			else {
				for (Topic t : topics)
					names.add(t.getName());

				boolean topicsSel[] = MultSelectionDialog.show("Topic's selection",
						"Select the topics that you want to print in the document", names);
				if (topicsSel == null)
					return; // User closed the selection window

				SQLTopicDAO topicSQL = SQLFactoryDAO.getInstance().getTopicDAO();
				SQLEssayDAO essaySQL = SQLFactoryDAO.getInstance().getEssayDAO();

				for (int i = 0; i < topicsSel.length; i++)
					if (topicsSel[i]) {
						Topic t = topicSQL.findById(topics.get(i).getId());
						for (Essay e : t.getEssays())
							e.setContent(essaySQL.getContent(e.getId()));
						finalTopics.add(t);
					}

				if (finalTopics.size() == 0)
					noTopics = true;
			}

			List<Grammar> entries = SQLFactoryDAO.getInstance().getGrammarDAO().findAll();
			List<Grammar> finalEntries = new LinkedList<Grammar>();

			if (entries.size() == 0)
				noGrammar = true;
			else {
				names.clear();
				for (Grammar g : entries)
					names.add(g.getName());

				boolean entriesSel[] = MultSelectionDialog.show("Grammar entries selection",
						"Select the grammar entries that you want to print in the document", names);
				if (entriesSel == null)
					return; // User closed the selection window

				SQLGrammarDAO grammarSQL = SQLFactoryDAO.getInstance().getGrammarDAO();

				for (int i = 0; i < entriesSel.length; i++)
					if (entriesSel[i]) {
						Grammar g = entries.get(i);
						g.setContent(grammarSQL.getContent(g.getId()));
						finalEntries.add(g);
					}

				if (finalEntries.size() == 0)
					noGrammar = true;
			}

			if (noTopics && noGrammar) {
				SevereError.show(SevereError.NO_CONTENT_TO_PRINT);
				return;
			}

			if (RTFCreator.writeRTFDocument(file.getAbsolutePath(), finalTopics, finalEntries))
				InfoDialog.show("Document generated successfully",
						"The document was successfully generated in the following path: '" + file.getAbsolutePath()
								+ "'.");
			else
				SevereError.show(SevereError.PRINT_ERROR);

		} catch (SQLException e) {
			SevereError.show(SevereError.DB_GET);
		}
	}

	public void handleHistorical() {
		try {
			List<Stat> stats = SQLFactoryDAO.getInstance().getStatDAO().getHistorical();
			if (stats.isEmpty()) {
				SevereError.show(SevereError.NOT_ENOUGH_GAME_DATA);
				return;
			}
			String[] tags = new String[stats.size()];
			double[] values = new double[stats.size()];
			Stat s;
			for (int i = 0; i < stats.size(); i++) {
				tags[i] = String.valueOf(i + 1);
				s = stats.get(i);
				values[i] = (double) s.getHits() / (s.getMisses() + s.getHits());
			}
			LineChartDialog.show("Historical progression", "Time", "Hit rate", tags, values);
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_GET);
		}
	}

	public void handleTopicsStats() {
		try {
			List<Topic> topics = SQLFactoryDAO.getInstance().getTopicDAO().findAll();
			List<String> topicsName = new LinkedList<String>();
			for (Topic t : topics)
				topicsName.add(t.getName());

			int sel = SelectionDialog.show("Stats for a topic", "Select one topic", topicsName);
			if (sel != -1) {
				Topic t = topics.get(sel);
				List<Stat> stats = SQLFactoryDAO.getInstance().getStatDAO().findAllByTopic(t.getId());

				if (stats.isEmpty()) {
					SevereError.show(SevereError.NOT_ENOUGH_GAME_DATA);
					return;
				}
				String[] tags = new String[stats.size()];
				double[] values = new double[stats.size()];
				Stat s;
				for (int i = 0; i < stats.size(); i++) {
					tags[i] = String.valueOf(i + 1);
					s = stats.get(i);
					values[i] = (double) s.getHits() / (s.getMisses() + s.getHits());
				}

				LineChartDialog.show("Progression for " + t.getName(), "Game", "Hit rate", tags, values);
			}

		} catch (SQLException e) {
			SevereError.show(SevereError.DB_GET);
		}
	}

	public void handleGameStats() {
		try {
			List<Stat> stats = SQLFactoryDAO.getInstance().getStatDAO().getHistorical();
			if (stats.isEmpty()) {
				SevereError.show(SevereError.NOT_ENOUGH_GAME_DATA);
				return;
			}

			double[] values = new double[3];
			int[] repeat = new int[3];

			for (int i = 0; i < 3; i++) {
				values[i] = 0;
				repeat[i] = 0;
			}

			Stat s;
			for (int i = 0; i < stats.size(); i++) {
				s = stats.get(i);
				values[s.getGame()] += (double) s.getHits() / (s.getHits() + s.getMisses());
				repeat[s.getGame()]++;
			}

			int count = 0;
			for (int i = 0; i < 3; i++)
				if (repeat[i] != 0) {
					values[i] = (double) values[i] / repeat[i];
					count++;
				}

			// Games played
			double[] finalValues = new double[count];
			String[] tags = new String[count];

			int g = 0;
			for (int i = 0; i < 3; i++)
				if (repeat[i] != 0) {
					finalValues[g] = values[i];
					tags[g] = TopicGameSelController.NAMES[i] + " (" + repeat[i] + ")";
					g++;
				}

			LineChartDialog.showNoRegression("Historical progression", "Game", "Hit rate", tags, finalValues);
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_GET);
		}
	}

	public void handleResetProfile() {
		try {
			if (ConfirmDialog.show("Reset profile?", "All your stats will be removed. Do you want to continue?"))
				SQLFactoryDAO.getInstance().getStatDAO().deleteAll();
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_DELE);
		}
	}

	// About Menu

	public void handleHelpButton() {
		try {
			if (ConfirmDialog.show("Wordbook manual",
					"You can find some useful information in the wordbook manual page."
							+ " Do you want to open the browser?"))
				Desktop.getDesktop().browse(new URI("http://jorgeortizesc.com/wordbook-manual.html"));
		} catch (Exception e1) {
			SevereError.show(SevereError.CANT_OPEN_BROWSER);
		}
	}

	public void handleAuthorButton() {
		try {
			if (ConfirmDialog.show("Wordbook author", "Do you want to visit the author's website?"))
				Desktop.getDesktop().browse(new URI("http://jorgeortizesc.com"));
		} catch (Exception e1) {
			SevereError.show(SevereError.CANT_OPEN_BROWSER);
		}
	}

	// Game Menu controller

	public void setTopicsForGame(List<Topic> topics) {
		topicsForGame = topics;
	}

	public void handleCategorizatorButton() {
		gameOption = TopicGameSelController.CATEGORIZATOR;
		changeView(TOPIC_GAME);
	}

	public void handleTestButton() {
		gameOption = TopicGameSelController.TEST;
		changeView(TOPIC_GAME);
	}

	public void handleListenButton() {
		gameOption = TopicGameSelController.LISTEN_TO_IT;
		changeView(TOPIC_GAME);
	}

	// Style management
	private void viewChanged(int newStyle) {
		Style.getInstance().changeStyle(newStyle);
		try {
			SQLFactoryDAO.getInstance().getProfileDAO().updateStyle(newStyle);
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_GET);
		}
	}

	public void handleDarkTheme() {
		viewChanged(Style.DARK_THEME);
	}

	public void handleLima() {
		viewChanged(Style.LIMA);
	}

	public void handleBlueSky() {
		viewChanged(Style.BLUE_SKY);
	}

	public void handleSoftPink() {
		viewChanged(Style.SOFT_PINK);
	}

	public void handleColorful() {
		viewChanged(Style.COLORFUL);
	}

	@Override
	public void styleChange(String newStyle) {
		root.getStylesheets().clear();
		root.getStylesheets().add(newStyle);
	}

}
