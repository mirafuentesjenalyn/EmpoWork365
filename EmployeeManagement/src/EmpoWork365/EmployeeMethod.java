/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jenal
 */
public class EmployeeMethod {
    
    public DefaultTableModel getEmployeeData(Connection connection) throws SQLException {

        String[] columnNames = {
            "Employee ID", "Full Name", "Email", "Gender", 
            "Job Title", "Department", "Date of Employment"
        };
        
        // Create a table model with the defined columns
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        sqlConnector connector = new sqlConnector();
        if (connection == null) {
            connection = connector.createConnection();
        }
        
            String query = "SELECT e.fld_employee_id, "
                         + "CONCAT(u.fld_first_name, ' ', u.fld_last_name) AS full_name, "
                         + "u.fld_email, u.fld_gender, "
                         + "e.fld_job_title, d.fld_department_name, "
                         + "e.fld_date_of_employment "
                         + "FROM tbl_employees e "
                         + "INNER JOIN tbl_users u ON e.fld_user_id = u.fld_user_id "
                         + "INNER JOIN tbl_department d ON e.fld_department_id = d.fld_department_id;";
            try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            
            // Iterate through the result set and add rows to the model
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("fld_email"),
                    resultSet.getString("fld_gender"),
                    resultSet.getString("fld_job_title"),
                    resultSet.getString("fld_department_name"),
                    resultSet.getDate("fld_date_of_employment")
                };
                model.addRow(row);
                
            }
        } catch (Exception e) {
        }
        
        return model; 
    }

    public List<EmployeeSearch> searchEmployeeMethod(String searchTerm) {
        List<EmployeeSearch> employees = new ArrayList<>();
        sqlConnector connector = new sqlConnector(); // Create a new sqlConnector instance
        Connection connection = null;

        try {
            connection = connector.createConnection(); // Create the connection
            String sql = "SELECT firstname, lastname, email, jobtitle, departmentName " +
                         "FROM employees " +
                         "WHERE CONCAT(firstname, ' ', lastname) LIKE ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, "%" + searchTerm + "%"); // Set the search term with wildcards
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    String email = resultSet.getString("email");
                    String jobtitle = resultSet.getString("jobtitle");
                    String departmentName = resultSet.getString("departmentName");

                    EmployeeSearch employee = new EmployeeSearch(firstname, lastname, email, jobtitle, departmentName);
                    employees.add(employee);
                }
            } catch (SQLException e) {
                System.err.println("SQL Exception: " + e.getMessage()); // Handle SQL exceptions
            }
        } catch (SQLException e) {
            System.err.println("Connection Exception: " + e.getMessage()); // Handle connection errors
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Always close the connection
                } catch (SQLException e) {
                    System.err.println("Closing Connection Exception: " + e.getMessage()); // Handle closing errors
                }
            }
        }

        return employees;
    }


    
    
}

