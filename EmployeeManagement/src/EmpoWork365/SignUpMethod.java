package EmpoWork365;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpMethod {
    private sqlConnector connector;

    // Constructor
    public SignUpMethod() {
        connector = new sqlConnector();
    }

    public boolean createAccount(String firstName, String lastName, String email, String password, 
                                 String gender, int jobTitleId, int departmentId, int roleId, 
                                 String dateOfEmployment, String imagePath) {
        // Modified query to insert directly into tbl_employees
        String insertEmployeeQuery = "INSERT INTO tbl_employees (fld_first_name, fld_last_name, fld_email, fld_password, "
                + "fld_gender, fld_job_title_id, fld_department_id, fld_role_id, fld_date_of_employment, fld_image_path) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connector.createConnection();
             PreparedStatement employeeStmt = conn.prepareStatement(insertEmployeeQuery)) {

            // Set parameters for the employee statement
            employeeStmt.setString(1, firstName);
            employeeStmt.setString(2, lastName);
            employeeStmt.setString(3, email);
            employeeStmt.setString(4, password);
            employeeStmt.setString(5, gender);
            employeeStmt.setInt(6, jobTitleId); 
            employeeStmt.setInt(7, departmentId);
            employeeStmt.setInt(8, roleId);
            employeeStmt.setString(9, dateOfEmployment);
            employeeStmt.setString(10, imagePath);

            // Execute the insert query
            int employeeRows = employeeStmt.executeUpdate();

            if (employeeRows > 0) {
                return true; // Account creation successful
            } else {
                System.err.println("Failed to create employee account. No rows affected in tbl_employees.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("SQL error while creating account: " + e.getMessage());
            return false;
        }
    }
}
