package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import application.model.Grammar;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLGrammarDAO {

	private String tableName = "TGrammar";
	private String[] selParams = { "content" };
	private String[] updParams = { "name", "content", "difficulty" };
	private String[] insParams = updParams;

	public String getContent(long id) throws SQLException {
		String[] result = SQLOperation.get(id, tableName, selParams);
		return result[0];
	}

	// Complex method (doesn't use SQLOperation)
	public List<Grammar> findAll() throws SQLException {

		List<Grammar> entries = new LinkedList<Grammar>();

		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id, name, difficulty FROM " + tableName;
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = stmt.executeQuery();

		String title;
		long id;
		int difficulty;
		Grammar entry;

		while (rs.next()) {
			title = rs.getString("name");
			id = rs.getLong("id");
			difficulty = rs.getInt("difficulty");
			entry = new Grammar(title, difficulty);
			entry.setId(id);
			entries.add(entry);
		}

		return entries;
	}

	public boolean delete(long id) throws SQLException {
		return SQLOperation.delete(id, tableName);
	}

	public boolean update(long id, Grammar newData, String content) throws SQLException, SQLDuplicateEntityException {

		String[] values = { newData.getName(), content, String.valueOf(newData.getDifficulty()) };
		return SQLOperation.update(id, tableName, newData.getName(), updParams, values);
	}

	public boolean insert(Grammar entry, String content)
			throws SQLException, SQLDuplicateEntityException, SQLGeneratedKeyException {

		String[] values = { entry.getName(), content, String.valueOf(entry.getDifficulty()) };
		long id = SQLOperation.insert(entry, tableName, insParams, values);
		if (id == -1)
			return false;
		entry.setId(id);
		return true;
	}

}
