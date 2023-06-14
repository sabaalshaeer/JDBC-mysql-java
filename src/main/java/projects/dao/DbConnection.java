package projects.dao;

import java.sql.*;

import projects.exception.DbException;

public class DbConnection {
	private static final String SCHEMA = "projects";
	private static final String USER = "projects";
	private static final String PASSWORD = "projects";
	private static final String HOST = "localhost";
	private static final int PORT = 3306;


	//method to create a new connection every time we call it
	//connection in JDBC is an interface called connection
	public static Connection getConnection() {
		String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s", HOST, PORT, SCHEMA,USER,PASSWORD);
		System.out.println("Connection with url= " +url);
		//create connection
		try {
			Connection conn =DriverManager.getConnection(url);
			System.out.println("Connection to schema '" +SCHEMA+ "' is successful.");
			return conn;
		} catch (SQLException e) {
			System.out.println("Unable to get connection ar "+ url);
			throw new DbException("Unable to get connection at \" +url");
		}
	}

}
