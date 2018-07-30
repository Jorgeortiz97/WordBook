package application.model;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Stat {

	public final static int CATEGORIZATOR = 0;
	public final static int TEST = 1;
	public final static int LISTEN_TO_IT = 2;

	private List<Long> topics = new LinkedList<Long>();
	private long id;
	private IntegerProperty hits;
	private IntegerProperty misses;
	private IntegerProperty game;

	public Stat(List<Long> topics, int hits, int misses, int game) {
		this.topics = topics;
		this.hits = new SimpleIntegerProperty(hits);
		this.misses = new SimpleIntegerProperty(misses);
		this.game = new SimpleIntegerProperty(game);
	}

	public Stat(int hits, int misses, int game) {
		this(null, hits, misses, game);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Long> getTopic() {
		return topics;
	}

	public int getHits() {
		return hits.get();
	}

	public int getMisses() {
		return misses.get();
	}

	public int getGame() {
		return game.get();
	}
}
