package application.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLFactoryDAO {

	private static SQLFactoryDAO instance;
	private final static String PATH = "database.db";

	private SQLEssayDAO essay = new SQLEssayDAO();
	private SQLTopicDAO topic = new SQLTopicDAO();
	private SQLWordDAO word = new SQLWordDAO();
	private SQLGrammarDAO grammar = new SQLGrammarDAO();
	private SQLStatDAO stat = new SQLStatDAO();
	private SQLProfileDAO profile = new SQLProfileDAO();

	private SQLFactoryDAO() {
	}

	public static SQLFactoryDAO getInstance() {
		if (instance == null)
			instance = new SQLFactoryDAO();
		return instance;
	}

	public SQLEssayDAO getEssayDAO() {
		return essay;
	}

	public SQLWordDAO getWordDAO() {
		return word;
	}

	public SQLTopicDAO getTopicDAO() {
		return topic;
	}

	public SQLGrammarDAO getGrammarDAO() {
		return grammar;
	}

	public SQLStatDAO getStatDAO() {
		return stat;
	}

	public SQLProfileDAO getProfileDAO() {
		return profile;
	}

	private void execute(String sql) throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
	}

	public boolean init() {
		File f = new File(PATH);
		boolean isNew = !f.exists() || f.isDirectory();
		try {
			if (isNew) {
				execute("CREATE TABLE TTopic ( 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT)");

				execute("CREATE TABLE TEssay ( 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'idTopic' INTEGER NOT NULL, 'name' TEXT, 'content' TEXT, 'difficulty' INTEGER, FOREIGN KEY('idTopic') REFERENCES TTopic)");

				execute("CREATE TABLE TWord ( 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'idTopic' INTEGER NOT NULL, 'name' TEXT, 'translation' TEXT, 'other' TEXT, 'difficulty' INTEGER, FOREIGN KEY('idTopic') REFERENCES TTopic)");

				execute("CREATE TABLE TGrammar ( 'id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 'name' TEXT, 'content' TEXT, 'difficulty' INTEGER)");

				execute("CREATE TABLE TStat ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `hits` INTEGER, `misses` INTEGER, `game` INTEGER )");

				execute("CREATE TABLE TStatTopic ( `id` INTEGER NOT NULL, `idTopic` INTEGER NOT NULL, FOREIGN KEY(`id`) REFERENCES `TStat`(`id`), PRIMARY KEY(`id`,`idTopic`) )");

				execute("CREATE TABLE TProfile ( `id` INTEGER NOT NULL PRIMARY KEY, `style` INTEGER NOT NULL)");

				execute("INSERT INTO TProfile (`id`, `style`) VALUES (1, 0)");
			}
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

}
