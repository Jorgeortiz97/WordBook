package application.view;

import java.sql.SQLException;
import java.util.List;

import application.model.Grammar;
import application.persistence.SQLFactoryDAO;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;
import application.util.EditDialog;
import application.util.FatalError;
import application.util.MultipleInputAnswer;
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GrammarController implements IController {

	// GUI Attributes
	@FXML
	private TableView<Grammar> grammarTable;

	@FXML
	private TableColumn<Grammar, String> nameColumn;
	@FXML
	private TableColumn<Grammar, Integer> difficultyColumn;

	@FXML
	private Button newButton;
	@FXML
	private Button openButton;
	@FXML
	private Button deleteButton;

	// Logic Attributes
	private List<Grammar> entries;

	public boolean init(Object arg) {

		nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		difficultyColumn.setCellValueFactory(cell -> cell.getValue().difficultyProperty().asObject());

		try {
			entries = SQLFactoryDAO.getInstance().getGrammarDAO().findAll();
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_INIT);
		}

		List<Grammar> items = grammarTable.getItems();
		items.clear();

		for (Grammar g : entries)
			items.add(g);

		return true;
	}

	public void handleNewButton() {

		boolean error;
		String[] defaultValues = { "", "", "" };

		do {
			error = false;
			MultipleInputAnswer mia = EditDialog.show(EditDialog.GRAMMAR_ADD, defaultValues);

			if (mia.isAnswered()) {
				String[] grammarValues = mia.getAnswers();

				defaultValues = grammarValues;

				int diff = 0;
				if (!grammarValues[1].equals("")) {
					try {
						diff = Integer.parseInt(grammarValues[1]);
					} catch (NumberFormatException nfe) {
						SevereError.show(SevereError.DIFFICULTY_FIELD);
						error = true;
					}
				}

				if (!error) {
					Grammar entry = new Grammar(grammarValues[0], diff);

					try {
						SQLFactoryDAO.getInstance().getGrammarDAO().insert(entry, grammarValues[2]);

						entries.add(entry);
						grammarTable.getItems().add(entry);
					} catch (SQLException | SQLGeneratedKeyException e) {
						SevereError.show(SevereError.DB_ADD);
						error = true;
					} catch (SQLDuplicateEntityException e) {
						SevereError.show(SevereError.DB_DUP);
						error = true;
					}
				}
			}

		} while (error);
	}

	public void handleOpenButton() {

		Grammar selectedEntry = grammarTable.getSelectionModel().getSelectedItem();
		if (selectedEntry == null)
			return;
		String content = null;
		try {
			content = SQLFactoryDAO.getInstance().getGrammarDAO().getContent(selectedEntry.getId());
		} catch (SQLException e) {
		}

		if (content == null) {
			SevereError.show(SevereError.DB_GET);
			return;
		}

		boolean error;
		String[] defaultValues = { selectedEntry.getName(), String.valueOf(selectedEntry.getDifficulty()), content };

		do {
			error = false;
			MultipleInputAnswer mia = EditDialog.show(EditDialog.GRAMMAR_EDIT, defaultValues);

			if (mia.isAnswered()) {
				String[] grammarValues = mia.getAnswers();

				defaultValues = grammarValues;

				int diff = 0;
				if (!grammarValues[1].equals("")) {
					try {
						diff = Integer.parseInt(grammarValues[1]);
					} catch (NumberFormatException nfe) {
						SevereError.show(SevereError.DIFFICULTY_FIELD);
						error = true;
					}
				}

				if (!error) {
					Grammar entry = new Grammar(grammarValues[0], diff);

					try {
						SQLFactoryDAO.getInstance().getGrammarDAO().update(selectedEntry.getId(), entry,
								grammarValues[2]);
					} catch (SQLException e) {
						SevereError.show(SevereError.DB_UPD);
						error = true;
					} catch (SQLDuplicateEntityException e) {
						SevereError.show(SevereError.DB_DUP);
						error = true;
					}

					if (!error) {
						selectedEntry.setName(entry.getName());
						selectedEntry.setDifficulty(entry.getDifficulty());
					}
				}
			}

		} while (error);

	}

	public void handleDeleteButton() {
		Grammar selectedEntry = grammarTable.getSelectionModel().getSelectedItem();
		if (selectedEntry == null)
			return;

		grammarTable.getItems().remove(selectedEntry);
		try {
			SQLFactoryDAO.getInstance().getGrammarDAO().delete(selectedEntry.getId());
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_DELE);
		}
	}

	@Override
	public boolean onlyFirstTime() {
		return true;
	}

	@Override
	public int backPane() {
		return MenuController.INITIAL_VIEW;
	}

}
