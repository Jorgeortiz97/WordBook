package application.view;

import java.sql.SQLException;
import java.util.List;

import application.model.Essay;
import application.model.Topic;
import application.persistence.SQLFactoryDAO;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;
import application.util.EditDialog;
import application.util.FatalError;
import application.util.MultipleInputAnswer;
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EssayController implements IController {

	// GUI Attributes
	@FXML
	private Label label;

	@FXML
	private TableView<Essay> essayTable;

	@FXML
	private TableColumn<Essay, String> essayColumn;
	@FXML
	private TableColumn<Essay, Integer> difficultyColumn;

	@FXML
	private Button newButton;
	@FXML
	private Button openButton;
	@FXML
	private Button deleteButton;

	// Logic Attributes
	private List<Essay> essays;
	private Topic topic;

	public EssayController() {
	}

	public boolean init(Object arg) {
		if (!(arg instanceof Topic))
			return false;

		this.topic = (Topic) arg;

		label.setText(topic.getName() + ":");

		essayColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
		difficultyColumn.setCellValueFactory(cell -> cell.getValue().difficultyProperty().asObject());

		try {
			essays = SQLFactoryDAO.getInstance().getEssayDAO().findAllByTopic(topic);
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_INIT);
		}

		List<Essay> items = essayTable.getItems();
		items.clear();

		for (Essay e : essays)
			items.add(e);

		return true;
	}

	public void handleNewButton() {

		boolean error;
		String[] defaultValues = { "", "", "" };

		do {
			error = false;
			MultipleInputAnswer mia = EditDialog.show(EditDialog.ESSAY_ADD, defaultValues);

			if (mia.isAnswered()) {
				String[] essayValues = mia.getAnswers();

				defaultValues = essayValues;

				int diff = 0;
				if (!essayValues[1].equals("")) {
					try {
						diff = Integer.parseInt(essayValues[1]);
					} catch (NumberFormatException nfe) {
						SevereError.show(SevereError.DIFFICULTY_FIELD);
						error = true;
					}
				}

				if (!error) {
					Essay essay = new Essay(essayValues[0], diff);

					try {
						SQLFactoryDAO.getInstance().getEssayDAO().insertEssay(topic, essay, essayValues[2]);

						essays.add(essay);
						essayTable.getItems().add(essay);
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

		Essay selectedEssay = essayTable.getSelectionModel().getSelectedItem();
		if (selectedEssay == null)
			return;
		String content = null;
		try {
			content = SQLFactoryDAO.getInstance().getEssayDAO().getContent(selectedEssay.getId());
		} catch (SQLException e) {
		}

		if (content == null) {
			SevereError.show(SevereError.DB_GET);
			return;
		}

		boolean error;
		String[] defaultValues = { selectedEssay.getName(), String.valueOf(selectedEssay.getDifficulty()), content };

		do {
			error = false;
			MultipleInputAnswer mia = EditDialog.show(EditDialog.ESSAY_EDIT, defaultValues);

			if (mia.isAnswered()) {
				String[] essayValues = mia.getAnswers();

				defaultValues = essayValues;

				int diff = 0;
				if (!essayValues[1].equals("")) {
					try {
						diff = Integer.parseInt(essayValues[1]);
					} catch (NumberFormatException nfe) {
						SevereError.show(SevereError.DIFFICULTY_FIELD);
						error = true;
					}
				}
				if (!error) {
					Essay essay = new Essay(essayValues[0], diff);

					try {
						SQLFactoryDAO.getInstance().getEssayDAO().updateEssay(selectedEssay.getId(), essay,
								essayValues[2]);
					} catch (SQLException e) {
						SevereError.show(SevereError.DB_UPD);
						error = true;
					} catch (SQLDuplicateEntityException e) {
						SevereError.show(SevereError.DB_DUP);
						error = true;
					}

					if (!error) {
						selectedEssay.setName(essay.getName());
						selectedEssay.setDifficulty(essay.getDifficulty());
					}
				}
			}

		} while (error);

	}

	public void handleDeleteButton() {
		Essay selectedEssay = essayTable.getSelectionModel().getSelectedItem();
		if (selectedEssay == null)
			return;

		essayTable.getItems().remove(selectedEssay);
		try {
			SQLFactoryDAO.getInstance().getEssayDAO().deleteEssay(selectedEssay.getId());
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_DELE);
		}
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
