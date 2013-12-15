package ch.fhnw.guerbereggenschwiler.apsi.lab2.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class ConnectionHandler {
	private ConnectionHandler(){}
	
	public static Connection getConnection() throws SQLException {
	
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
		return DriverManager.getConnection("jdbc:mysql://localhost/apsi_lab?user=root");
	}
}
