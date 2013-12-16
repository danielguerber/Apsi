package ch.fhnw.guerbereggenschwiler.apsi.lab2.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.CheckReturnValue;

import com.sun.istack.internal.NotNull;

/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * This class provides functionality to get
 * a Database connection.
 */
public final class ConnectionHandler {
	private ConnectionHandler(){}
	
	/**
	 * Returns a connection to the database
	 * @return connection
	 * @throws SQLException thrown on database error
	 */
	@CheckReturnValue
	@NotNull
	public static Connection getConnection() throws SQLException {
	
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			throw new AssertionError("MySql driver not installed!");
		}
		
		return DriverManager.getConnection("jdbc:mysql://localhost/apsi_lab?user=root");
	}
}
