package application.persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

	private final static String PATH = "jdbc:sqlite:database.db";
	private Connection connection = null;
	private static SQLConnection instance = null;

	public static SQLConnection getInstance() throws SQLException {
		if (instance == null)
			instance = new SQLConnection();
		return instance;
	}

	private SQLConnection() throws SQLException {
		connection = DriverManager.getConnection(PATH);
	}

	public void close() throws SQLException{
		close();
	}

	public Connection getConnection() {
		return connection;
	}
}


