/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author jenal
 */
public class sqlConnector {
    private String connString = "jdbc:mysql://localhost:3306/db_employee_management";
    private String userName = "root";
    private String passWord = "";
    
  // Method to create a connection to the database
    public Connection createConnection() throws SQLException {
        System.out.println("Attempting to connect to the database...");

        try {
            Connection conn = DriverManager.getConnection(connString, userName, passWord);

            if (conn != null) {
                System.out.println("Database connected successfully.");
            } else {
                System.out.println("Failed to connect to database.");
            }

            return conn;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            throw e;  // Re-throw the exception to allow the caller to handle it
        }
    }
}
