package application.view;

import java.sql.SQLException;
import java.util.List;

import application.model.Topic;
import application.persistence.SQLFactoryDAO;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;
import application.util.FatalError;
import application.util.MultipleInputAnswer;
import application.util.MultipleInputDialog;
import application.util.SevereError;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TopicController implements IController {

	// GUI Attributes
	@FXML
	private TableView<Topic> table;
	@FXML
	private TableColumn<Topic, String> topicColumn;
	@FXML
	private Button openButton;
	@FXML
	private Button createButton;
	@FXML
	private Button deleteButton;
	@FXML
	private TextField textField;

	private MenuController mc;

	// Logic Attributes
	private List<Topic> topics;

	public TopicController(MenuController mc) {
		this.mc = mc;
	}

	public boolean init(Object arg) {

		topicColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());

		List<Topic> items = table.getItems();
		items.clear();
		try {
			topics = SQLFactoryDAO.getInstance().getTopicDAO().findAll();
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_INIT);
		}

		for (Topic t : topics)
			items.add(t);

		return true;
	}

	public void handleOpenButton() {
		Topic selectedItem = table.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		Topic t = null;
		try {
			t = SQLFactoryDAO.getInstance().getTopicDAO().findById(selectedItem.getId());
		} catch (SQLException e) {
			FatalError.show(FatalError.DB_GET);
		}
		mc.changeTopic(t);
		if (mc.isVocabularySelected())
			mc.changeView(MenuController.VOCAB_VIEW);
		else
			mc.changeView(MenuController.ESSAY_VIEW);
	}

	public void handleCreateButton() {
		String name = textField.getText();
		if (name.equals(""))
			return;
		Topic topic = new Topic(name);
		try {
			SQLFactoryDAO.getInstance().getTopicDAO().insert(topic);
			table.getItems().add(topic);
			textField.setText("");
			topics.add(topic);
		} catch (SQLException | SQLGeneratedKeyException e) {
			SevereError.show(SevereError.DB_ADD);
		} catch (SQLDuplicateEntityException e) {
			SevereError.show(SevereError.DB_DUP);
		}
	}

	public void handleEditButton() {
		Topic selectedItem = table.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		MultipleInputAnswer mia = MultipleInputDialog.show(MultipleInputDialog.TOPIC_EDIT,
				new String[] { selectedItem.getName() });
		if (!mia.isAnswered())
			return;

		String newName = mia.getAnswers()[0];

		long id = selectedItem.getId();
		Topic newTopic = new Topic(newName);
		newTopic.setId(id);

		try {
			SQLFactoryDAO.getInstance().getTopicDAO().update(newTopic);
			table.getSelectionModel().getSelectedItem().setName(newName);
		} catch (SQLException e) {
			SevereError.show(SevereError.DB_UPD);
		} catch (SQLDuplicateEntityException e) {
			SevereError.show(SevereError.DB_DUP);
		}

	}

	public void handleDeleteButton() {
		Topic selectedItem = table.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;
		table.getItems().remove(selectedItem);
		long id = selectedItem.getId();
		try {
			SQLFactoryDAO.getInstance().getTopicDAO().delete(id);
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
		return MenuController.INITIAL_VIEW;
	}

}
