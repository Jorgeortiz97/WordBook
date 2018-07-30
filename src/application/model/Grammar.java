package application.model;


import application.persistence.IModelObj;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Grammar implements IModelObj {

	private long id;
	private StringProperty name;
	private IntegerProperty difficulty;

	// Only for printing
	private String content;

	public Grammar(String name, int difficulty) {
		this.name = new SimpleStringProperty(name);
		this.difficulty = new SimpleIntegerProperty(difficulty);
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public int getDifficulty() {
		return difficulty.get();
	}

	public IntegerProperty difficultyProperty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty.set(difficulty);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
