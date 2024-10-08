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
        return DriverManager.getConnection(connString, userName, passWord);
    }
}
