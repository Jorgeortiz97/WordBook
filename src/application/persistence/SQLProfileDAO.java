package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLProfileDAO {
	private final static String styleParam = "style";
	private final static String tableName = "TProfile";

	public boolean updateStyle(int newStyle) throws SQLException {
		String sql = "UPDATE " + tableName + " SET " + styleParam + "=" + newStyle;
		Connection conn = SQLConnection.getInstance().getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql);
		return (stmt.executeUpdate() == 1);
	}

	public int getStyle() throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT " + styleParam + " FROM " + tableName;
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		if (rs.next())
			return rs.getInt(styleParam);
		else
			return -1;
	}

}
