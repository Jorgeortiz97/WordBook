package application.model;

import application.persistence.IModelObj;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Word implements IModelObj {

	private long id;
	private StringProperty name;
	private StringProperty translation;
	private StringProperty other;
	private IntegerProperty difficulty;

	public Word(String name, String translation, String other) {
		this(name, translation, other, 0);
	}

	public Word(String name, String translation, String other, int difficulty) {
		this.name = new SimpleStringProperty(name);
		this.translation = new SimpleStringProperty(translation);
		this.other = new SimpleStringProperty(other);
		this.difficulty = new SimpleIntegerProperty(difficulty);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public StringProperty nameProperty() {
		return name;
	}
	public String getName() {
		return name.get();
	}

	public StringProperty translationProperty() {
		return translation;
	}
	public String getTranslation() {
		return translation.get();
	}

	public StringProperty otherProperty() {
		return other;
	}
	public String getOther() {
		return other.get();
	}

	public IntegerProperty difficultyProperty() {
		return difficulty;
	}
	public int getDifficulty() {
		return difficulty.get();
	}

	public void applyChanges(Word other) {
		setName(other.getName());
		this.translation.set(other.getTranslation());
		this.other.set(other.getOther());
		this.difficulty.set(other.getDifficulty());
	}

	@Override
	public void setName(String name) {
		this.name.set(name);
	}
}
