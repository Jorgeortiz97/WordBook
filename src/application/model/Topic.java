package application.model;

import java.util.LinkedList;

import application.persistence.IModelObj;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Topic implements IModelObj {

	private LinkedList<Word> words = new LinkedList<>();
	private LinkedList<Essay> essays = new LinkedList<>();
	private long id;
	private StringProperty name;

	public Topic(String name) {
		this(name, -1);
	}

	public Topic(String name, long id) {
		this.name = new SimpleStringProperty(name);
		this.id = id;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public LinkedList<Word> getWords() {
		return words;
	}
	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void addWord(Word word) {
		words.add(word);
	}

	public void addEssay(Essay essay) {
		essays.add(essay);
	}

	public LinkedList<Essay> getEssays() {
		return essays;
	}

	@Override
	public void setName(String name) {
		this.name.set(name);
	}


}
