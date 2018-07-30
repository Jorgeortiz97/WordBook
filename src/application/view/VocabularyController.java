package application.view;

import java.sql.SQLException;
import java.util.List;

import application.model.Topic;
import application.model.Word;
import application.persistence.SQLFactoryDAO;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;
import application.util.FatalError;
import application.util.MultipleInputAnswer;
import application.util.MultipleInputDialog;
import application.util.Player;
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class VocabularyController implements IController {

	// GUI Attributes
	@FXML
	private TableView<Word> wordTable;

	@FXML
	private Label label;

	@FXML
	private TableColumn<Word, String> wordColumn;
	@FXML
	private TableColumn<Word, String> translationColumn;
	@FXML
	private TableColumn<Word, String> otherColumn;
	@FXML
	private TableColumn<Word, Integer> difficultyColumn;

	@FXML
	private Button addButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;

	// Logic Attributes
	private List<Word> words;
	private Topic topic;

	public VocabularyController() {

	}

	public boolean init(Object arg) {
		if (!(arg instanceof Topic))
			return false;

		topic = (Topic) arg;

		label.setText(topic.getName() + ":");

		wordColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		translationColumn.setCellValueFactory(cell -> cell.getValue().translationProperty());
		otherColumn.setCellValueFactory(cell -> cell.getValue().otherProperty());
		difficultyColumn.setCellValueFactory(cell -> cell.getValue().difficultyProperty().asObject());

		wordTable.setEditable(true);

		try {
			words = SQLFactoryDAO.getInstance().getWordDAO().findAllByTopic(topic);
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_INIT);
		}

		List<Word> items = wordTable.getItems();
		items.clear();

		for (Word w : words)
			items.add(w);

		return true;
	}

	public void handleAddButton() {

		String[] defaultStr = { "Word", "Translation", "Other", "Difficulty" };

		MultipleInputAnswer mia = MultipleInputDialog.show(MultipleInputDialog.WORD_ADD, defaultStr);

		if (!mia.isAnswered())
			return;

		String[] answers = mia.getAnswers();

		if (answers[0].equals("") || answers[1].equals("")) {
			SevereError.show(SevereError.WORDTRAN_REQFIELDS);
			return;
		}

		int diff = 0;
		if (!answers[3].equals("")) {
			try {
				diff = Integer.parseInt(answers[3]);
			} catch (NumberFormatException nfe) {
				diff = -1;
			}
			if (diff < 0) {
				SevereError.show(SevereError.DIFFICULTY_FIELD);
				return;
			}
		}

		Word word = new Word(answers[0], answers[1], answers[2], diff);

		try {
			SQLFactoryDAO.getInstance().getWordDAO().insertWord(topic, word);
			wordTable.getItems().add(word);
			words.add(word);
		} catch (SQLException | SQLGeneratedKeyException e) {
			SevereError.show(SevereError.DB_ADD);
		} catch (SQLDuplicateEntityException e) {
			SevereError.show(SevereError.DB_DUP);
		}
	}

	public void handleEditButton() {
		Word word = wordTable.getSelectionModel().getSelectedItem();
		if (word == null)
			return;

		String[] defaultStr = { word.getName(), word.getTranslation(), word.getOther(),
				String.valueOf(word.getDifficulty()) };

		MultipleInputAnswer mia = MultipleInputDialog.show(MultipleInputDialog.WORD_EDIT, defaultStr);

		if (!mia.isAnswered())
			return;

		String[] answers = mia.getAnswers();

		if (answers[0].equals("") || answers[1].equals("")) {
			SevereError.show(SevereError.WORDTRAN_REQFIELDS);
			return;
		}

		int diff = 0;
		if (!answers[3].equals("")) {
			try {
				diff = Integer.parseInt(answers[3]);
			} catch (NumberFormatException nfe) {
				diff = -1;
			}
			if (diff < 0) {
				SevereError.show(SevereError.DIFFICULTY_FIELD);
				return;
			}
		}

		Word newWord = new Word(answers[0], answers[1], answers[2], diff);

		try {
			SQLFactoryDAO.getInstance().getWordDAO().updateWord(word.getId(), newWord);
			word.applyChanges(newWord);

		} catch (SQLException e) {
			SevereError.show(SevereError.DB_UPD);
		} catch (SQLDuplicateEntityException e) {
			SevereError.show(SevereError.DB_DUP);
		}
	}

	public void handleDeleteButton() {
		Word selectedWord = wordTable.getSelectionModel().getSelectedItem();
		if (selectedWord == null)
			return;
		wordTable.getItems().remove(selectedWord);
		try {
			SQLFactoryDAO.getInstance().getWordDAO().deleteWord(selectedWord.getId());
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_DELE);
		}
	}

	public void handleListenButton() {
		Word selectedWord = wordTable.getSelectionModel().getSelectedItem();
		if (selectedWord == null)
			return;
		if (!Player.play(selectedWord.getName()))
			SevereError.show(SevereError.PLAY_ERROR);
	}

	@Override
	public boolean onlyFirstTime() {
		return false;
	}

	@Override
	public int backPane() {
		return MenuController.TOPIC_VIEW;
	}

}
