package bj.highfive.usermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bj.highfive.usermanagement.bean.User;

public class UserDao {

	private String jdbcURL = "jdbc:mysql://localhost:3306/userdb?useSSL=false";
	private String jdbcUserName = "root";
	private String jdbcPassword = "";
	private String jdbcDriver = "com.mysql.jdbc.Driver";

	private static final String INSERT_USER_SQL = "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";
	private static final String SELECT_USER_BY_ID = "SELECT name, email, country FROM users WHERE id = ?;";
	private static final String SELECT_ALL_USERS = "SELECT * FROM users;";
	private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?;";
	private static final String UPDATE_USER_SQL = "UPDATE users SET name = ?, email = ?, country = ? WHERE id = ?;";

	public UserDao() {

	}

	protected Connection getConnection() {
		Connection connection = null;

		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}

	// Insert user
	public void insertUser(User user) {
		System.out.println(INSERT_USER_SQL);
		try {
			Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL);
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getCountry());
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	// Select user by Id
	public User selectUser(int id) {
		User user = null;
		// Step 1: Establishing a Connection
		try {
			Connection connection = getConnection();
			// Step 2: Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);
			System.out.println(preparedStatement);
			// Step 3: Excecute the query or update query
			ResultSet rs = preparedStatement.executeQuery();
			// Step 4: Process the ResultSet object
			while (rs.next()) {
				String name = (String) rs.getString("name");
				String email = (String) rs.getString("email");
				String country = (String) rs.getString("country");
				user = new User(id, name, email, country);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return user;
	}

	// Select all users
	public List<User> selectAllUsers() {
		List<User> users = new ArrayList<>();

		try {
			// Step 1: Establishing a connection
			Connection connection = getConnection();
			// Step 2: Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();
			// Step 4: Process the ResultSet object
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				users.add(new User(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return users;
	}

	// Update a user
	public boolean updateUser(User user) {
		boolean rowUpdated = false;
		try {
			Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL);
			System.out.println("Updated User: " + preparedStatement);
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getCountry());
			preparedStatement.setInt(4, user.getId());

			rowUpdated = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			printSQLException(e);
		}
		return rowUpdated;
	}

	// Delete
	public boolean deleteUser(int id) {
		boolean rowDeleted = false;
		try {
			Connection connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL);
			preparedStatement.setInt(1, id);

			rowDeleted = preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			printSQLException(e);
		}
		return rowDeleted;
	}

	public void printSQLException(SQLException exception) {
		for (Throwable e : exception) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLStqte: " + ((SQLException) e).getSQLState());
				System.err.println("Error code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = exception.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}
