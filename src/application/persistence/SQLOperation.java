package application.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import application.persistence.util.SQLDuplicateEntityException;
import application.persistence.util.SQLGeneratedKeyException;

public class SQLOperation {

	/**
	 * Returns an object's id from a table or -1 if that object is not
	 * contained.
	 */
	public static long getId(String name, String tableName) throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id FROM " + tableName + " WHERE name=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, name);
		ResultSet rs = stmt.executeQuery();
		if (rs.next())
			return rs.getLong("id");
		else
			return -1;
	}

	/**
	 * Returns an object's id from a table or -1 if that object is not
	 * contained.
	 */
	private static long getId(IModelObj obj, String tableName) throws SQLException {
		return getId(obj.getName(), tableName);
	}

	/**
	 * Returns true if tableName contains an object with the same id as obj.
	 */
	private static boolean exists(IModelObj obj, String tableName) throws SQLException {
		return getId(obj, tableName) != -1;
	}

	/**
	 * Insert a new object.
	 * 
	 * @param obj
	 *            Object that contains the name (key)
	 * @param tableName
	 *            Table where obj is going to be inserted
	 * @param parameters
	 *            Parameters to insert with the object
	 * @param values
	 *            Values of those parameters
	 * @return The ID of the inserted Object or -1 in case of error
	 * @throws SQLException
	 * @throws SQLDuplicateEntityException
	 * @throws SQLGeneratedKeyException
	 */
	public static long insert(IModelObj obj, String tableName, String[] parameters, String[] values)
			throws SQLException, SQLDuplicateEntityException, SQLGeneratedKeyException {

		if (exists(obj, tableName))
			throw new SQLDuplicateEntityException();

		Connection conn = SQLConnection.getInstance().getConnection();
		String paramNames, paramValues;
		if (parameters.length > 0) {
			paramNames = "(" + parameters[0];
			paramValues = "(?";

			for (int i = 1; i < parameters.length; i++) {
				paramNames += "," + parameters[i];
				paramValues += ",?";
			}

			paramNames += ")";
			paramValues += ")";
		} else {
			paramNames = "";
			paramValues = "";
		}
		String sql = "INSERT INTO " + tableName + paramNames + " VALUES " + paramValues;
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < values.length; i++)
			stmt.setString(i + 1, values[i]);

		int rows = stmt.executeUpdate();
		if (rows != 1)
			return -1;

		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if (!generatedKeys.next())
			throw new SQLGeneratedKeyException();

		return generatedKeys.getLong(1);
	}

	public static long insertNoCheck(String tableName, String[] parameters, String[] values)
			throws SQLException, SQLGeneratedKeyException {

		Connection conn = SQLConnection.getInstance().getConnection();
		String paramNames, paramValues;
		if (parameters.length > 0) {
			paramNames = "(" + parameters[0];
			paramValues = "(?";

			for (int i = 1; i < parameters.length; i++) {
				paramNames += "," + parameters[i];
				paramValues += ",?";
			}

			paramNames += ")";
			paramValues += ")";
		} else {
			paramNames = "";
			paramValues = "";
		}
		String sql = "INSERT INTO " + tableName + paramNames + " VALUES " + paramValues;
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < values.length; i++)
			stmt.setString(i + 1, values[i]);

		int rows = stmt.executeUpdate();
		if (rows != 1)
			return -1;

		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if (!generatedKeys.next())
			throw new SQLGeneratedKeyException();

		return generatedKeys.getLong(1);
	}

	/**
	 * Update an object.
	 * 
	 * @param id
	 *            id of the object to modify
	 * @param tableName
	 *            Name of the table where the object is
	 * @param newName
	 *            New or current name of the object
	 * @param parameters
	 *            Parameters to modify
	 * @param values
	 *            Values of parameters to modify
	 * @return True if object was modified
	 * @throws SQLException
	 * @throws SQLDuplicateEntityException
	 */
	public static boolean update(long id, String tableName, String newName, String[] parameters, String[] values)
			throws SQLException, SQLDuplicateEntityException {

		// Check if new name exists (duplicate)
		long otherObj = getId(newName, tableName);
		if (otherObj != id && otherObj != -1)
			throw new SQLDuplicateEntityException();

		Connection conn = SQLConnection.getInstance().getConnection();

		String cmd;
		if (parameters.length == 0 || values.length == 0)
			throw new IllegalArgumentException();

		cmd = parameters[0] + "='" + values[0] + "'";
		for (int i = 1; i < parameters.length; i++)
			cmd += ", " + parameters[i] + "='" + values[i] + "'";

		String sql = "UPDATE " + tableName + " SET " + cmd + " WHERE id=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		return (stmt.executeUpdate() == 1);
	}

	/**
	 * Update an object.
	 * 
	 * @param id
	 *            id of the object to modify
	 * @param tableName
	 *            Name of the table where the object is
	 * @param newName
	 *            New or current name of the object
	 * @param parameters
	 *            Parameters to modify
	 * @param values
	 *            Values of parameters to modify
	 * @return True if object was modified
	 * @throws SQLException
	 * @throws SQLDuplicateEntityException
	 */
	public static boolean updateNoCheck(long id, String tableName, String[] parameters, String[] values)
			throws SQLException, SQLDuplicateEntityException {

		Connection conn = SQLConnection.getInstance().getConnection();

		String cmd;
		if (parameters.length == 0 || values.length == 0)
			throw new IllegalArgumentException();

		cmd = parameters[0] + "='" + values[0] + "'";
		for (int i = 1; i < parameters.length; i++)
			cmd += ", " + parameters[i] + "='" + values[i] + "'";

		String sql = "UPDATE " + tableName + " SET " + cmd + " WHERE id=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		return (stmt.executeUpdate() == 1);
	}

	/**
	 * Retrieves some data from an object
	 * 
	 * @param id
	 *            Object's id
	 * @param tableName
	 *            Name of the table
	 * @param parameters
	 *            Parameters to retrieve
	 * @return A String[] with the same length as 'parameters' array.
	 * @throws SQLException
	 */
	public static String[] get(long id, String tableName, String[] parameters) throws SQLException {

		Connection conn = SQLConnection.getInstance().getConnection();

		if (parameters.length == 0)
			throw new IllegalArgumentException();
		String cmd = parameters[0];
		for (int i = 1; i < parameters.length; i++)
			cmd += ", " + parameters[i];

		String sql = "SELECT " + cmd + " FROM " + tableName + " WHERE id=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		ResultSet rs = stmt.executeQuery();

		// No results in select operation
		if (!rs.next())
			return null;

		String[] result = new String[parameters.length];
		for (int i = 0; i < result.length; i++)
			result[i] = rs.getString(parameters[i]);

		return result;
	}

	/**
	 * Removes an object from a table
	 * 
	 * @param id
	 *            Object's id
	 * @param tableName
	 *            Name of the table
	 * @return True if object was removed
	 * @throws SQLException
	 */
	public static boolean delete(long id, String tableName) throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "DELETE FROM " + tableName + " WHERE id=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setLong(1, id);
		return (stmt.executeUpdate() == 1);
	}

	public static void deleteAll(String tableName) throws SQLException {
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "DELETE FROM " + tableName;
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.executeUpdate();
	}

	/**
	 * Returns all the objects of a table that accomplish a condition.
	 */
	private static List<IModelObj> getAllFactor(String tableName, String condition) throws SQLException {
		List<IModelObj> list = new LinkedList<IModelObj>();
		Connection conn = SQLConnection.getInstance().getConnection();
		String sql = "SELECT id, name FROM " + tableName + condition;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next())
			list.add(new DefaultModelObj(rs.getString("name"), rs.getLong("id")));

		return list;
	}

	/**
	 * Returns a list with all objects from a table.
	 */
	public static List<IModelObj> getAll(String tableName) throws SQLException {
		return getAllFactor(tableName, "");
	}

	/**
	 * Returns a list with all objects from a table that meet a condition.
	 */
	public static List<IModelObj> getAllIfCondition(String tableName, String parameter, String value)
			throws SQLException {
		return getAllFactor(tableName, " WHERE " + parameter + "=" + value);
	}

	/**
	 * Returns a list with all objects from a table that meet a condition.
	 */
	public static List<IModelObj> getAllIfCondition(String tableName, String parameter, long value)
			throws SQLException {
		return getAllFactor(tableName, " WHERE " + parameter + "=" + value);
	}

}
