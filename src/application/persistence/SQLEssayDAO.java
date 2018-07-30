package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import application.model.Essay;
import application.model.Topic;
import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLEssayDAO {

	private String tableName = "TEssay";
	private String[] selParams = { "content" };
	private String[] updParams = { "name", "content", "difficulty" };
	private String[] insParams = { "idTopic", "name", "content", "difficulty" };

	public String getContent(long id) throws SQLException {
		String[] result = SQLOperation.get(id, tableName, selParams);
		return result[0];
	}

	// Complex method (doesn't use SQLOperation)
	public List<Essay> findAllByTopic(Topic topic) throws SQLException {

		List<Essay> essays = new LinkedList<Essay>();

		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id, name, difficulty FROM TEssay WHERE idTopic=?";
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setLong(1, topic.getId());
		ResultSet rs = stmt.executeQuery();

		String title;
		long id;
		int difficulty;
		Essay essay;

		while (rs.next()) {
			title = rs.getString("name");
			id = rs.getLong("id");
			difficulty = rs.getInt("difficulty");
			essay = new Essay(title, difficulty);
			essay.setId(id);
			essays.add(essay);
		}

		return essays;
	}

	public boolean deleteEssay(long id) throws SQLException {
		return SQLOperation.delete(id, tableName);
	}

	public boolean updateEssay(long id, Essay newData, String content)
			throws SQLException, SQLDuplicateEntityException {

		String[] values = { newData.getName(), content, String.valueOf(newData.getDifficulty()) };
		return SQLOperation.update(id, tableName, newData.getName(), updParams, values);
	}

	public boolean insertEssay(Topic topic, Essay essay, String content)
			throws SQLException, SQLDuplicateEntityException, SQLGeneratedKeyException {

		String[] values = { String.valueOf(topic.getId()), essay.getName(), content,
				String.valueOf(essay.getDifficulty()) };
		long id = SQLOperation.insert(essay, tableName, insParams, values);
		if (id == -1)
			return false;
		essay.setId(id);
		return true;
	}

}
