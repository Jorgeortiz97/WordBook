package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import application.model.Topic;
import application.model.Word;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLWordDAO {

	private String tableName = "TWord";
	private String[] updParams = { "name", "translation", "other", "difficulty" };
	private String[] insParams = { "idTopic", "name", "translation", "other", "difficulty" };

	// Complex method (doesn't use SQLOperation)
	public List<Word> findAllByTopic(Topic topic) throws SQLException {
		List<Word> words = new LinkedList<Word>();

		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT name, translation, other, difficulty, id FROM TWord WHERE idTopic==?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, topic.getId());
		ResultSet rs = stmt.executeQuery();
		String name, translation, other;
		int difficulty = 0;
		long id;
		Word word;
		while (rs.next()) {
			name = rs.getString("name");
			translation = rs.getString("translation");
			other = rs.getString("other");
			difficulty = rs.getInt("difficulty");
			id = rs.getLong("id");
			word = new Word(name, translation, other, difficulty);
			word.setId(id);
			words.add(word);
		}

		return words;
	}

	public boolean deleteWord(long id) throws SQLException {
		return SQLOperation.delete(id, tableName);
	}

	public boolean updateWord(long id, Word newData) throws SQLException, SQLDuplicateEntityException {
		long otherId = exists(newData.getName(), newData.getId());

		if (otherId != id && otherId != -1)
			throw new SQLDuplicateEntityException();

		String[] values = { newData.getName(), newData.getTranslation(), newData.getOther(),
				String.valueOf(newData.getDifficulty()) };
		return SQLOperation.updateNoCheck(id, tableName, updParams, values);
	}

	private long exists(String name, long topic) throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id FROM " + tableName + " WHERE name=? AND idTopic=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, name);
		stmt.setLong(2, topic);
		ResultSet rs = stmt.executeQuery();
		if (rs.next())
			return (rs.getLong("id"));
		return -1;
	}

	public boolean insertWord(Topic topic, Word word)
			throws SQLException, SQLGeneratedKeyException, SQLDuplicateEntityException {
		if (exists(word.getName(), topic.getId()) != -1)
			throw new SQLDuplicateEntityException();

		String[] values = { String.valueOf(topic.getId()), word.getName(), word.getTranslation(), word.getOther(),
				String.valueOf(word.getDifficulty()) };
		long id = SQLOperation.insertNoCheck(tableName, insParams, values);
		if (id == -1)
			return false;
		word.setId(id);
		return true;
	}

}
