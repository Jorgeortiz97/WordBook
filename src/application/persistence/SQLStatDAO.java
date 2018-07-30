package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import application.model.Stat;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLStatDAO {

	private String tableName = "TStat";
	private String refTableName = "TStatTopic";
	private String[] insParams = { "hits", "misses", "game" };

	// Complex method (doesn't use SQLOperation)
	public List<Stat> findAllByTopic(long topicId) throws SQLException {
		List<Long> statsId = getAllStats(topicId);
		List<Stat> stats = new LinkedList<Stat>();

		Stat s;
		for (long id : statsId) {
			s = getStatById(id);
			if (s != null)
				stats.add(s);
		}

		return stats;
	}

	private Stat getStatById(long id) throws SQLException {
		String[] parameters = { "hits", "misses", "game" };
		String[] result = SQLOperation.get(id, tableName, parameters);
		if (result == null)
			return null;
		return new Stat(Integer.parseInt(result[0]), Integer.parseInt(result[1]), Integer.parseInt(result[2]));
	}

	private List<Long> getAllStats(long id) throws SQLException {
		List<Long> stats = new LinkedList<Long>();

		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id FROM " + refTableName + " WHERE idTopic=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next())
			stats.add(rs.getLong("id"));

		return stats;
	}

	public void deleteAll() throws SQLException {
		SQLOperation.deleteAll(refTableName);
		SQLOperation.deleteAll(tableName);
	}

	public boolean insert(Stat stat) throws SQLException, SQLGeneratedKeyException {
		String[] values = { String.valueOf(stat.getHits()), String.valueOf(stat.getMisses()),
				String.valueOf(stat.getGame()), };

		long id = SQLOperation.insertNoCheck(tableName, insParams, values);
		if (id == -1)
			return false;
		stat.setId(id);

		for (long l : stat.getTopic())
			SQLOperation.insertNoCheck(refTableName, new String[] { "id", "idTopic" },
					new String[] { String.valueOf(id), String.valueOf(l) });

		return true;
	}

	public List<Stat> getHistorical() throws SQLException {
		List<Stat> list = new LinkedList<Stat>();
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT hits, misses, game FROM " + tableName;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next())
			list.add(new Stat(rs.getInt("hits"), rs.getInt("misses"), rs.getInt("game")));

		return list;
	}

}
