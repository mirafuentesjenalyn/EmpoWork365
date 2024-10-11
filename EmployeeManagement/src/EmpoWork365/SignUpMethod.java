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
    String insertUserQuery = "INSERT INTO tbl_users (fld_first_name, fld_last_name, fld_email, fld_password, "
            + "fld_gender, fld_job_title_id, fld_department_id, fld_role_id, fld_date_of_employment, fld_image_path) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String insertEmployeeQuery = "INSERT INTO tbl_employees (fld_user_id, fld_job_title_id, "
            + "fld_department_id, fld_role_id, fld_date_of_employment, fld_image_path) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = connector.createConnection();
         PreparedStatement userStmt = conn.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
         PreparedStatement employeeStmt = conn.prepareStatement(insertEmployeeQuery)) {

        // Insert into `tbl_users`
        userStmt.setString(1, firstName);
        userStmt.setString(2, lastName);
        userStmt.setString(3, email);
        userStmt.setString(4, password);
        userStmt.setString(5, gender);
        userStmt.setInt(6, jobTitleId); 
        userStmt.setInt(7, departmentId);
        userStmt.setInt(8, roleId);
        userStmt.setString(9, dateOfEmployment);
        userStmt.setString(10, imagePath);

        int userRows = userStmt.executeUpdate();

        if (userRows > 0) {
            int generatedUserId = -1;
            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedUserId = generatedKeys.getInt(1);
            }

            employeeStmt.setInt(1, generatedUserId); // Set `fld_user_id` in `tbl_employees`
            employeeStmt.setInt(2, jobTitleId); // Use jobTitleId here as well
            employeeStmt.setInt(3, departmentId);
            employeeStmt.setInt(4, roleId);
            employeeStmt.setString(5, dateOfEmployment);
            employeeStmt.setString(6, imagePath);

            // Execute the employee statement
            int employeeRows = employeeStmt.executeUpdate();

            // Return true if both inserts are successful
            return employeeRows > 0;
        } else {
            System.err.println("Failed to create user. No rows affected in tbl_users.");
            return false; // User creation failed
        }

    } catch (SQLException e) {
        // Enhanced error handling
        System.err.println("SQL error while creating account: " + e.getMessage());
        return false;
    }
}


    // Updated method to retrieve the salary using job title ID
    private double getJobTitleSalary(Connection conn, int jobTitleId) {
        String salaryQuery = "SELECT fld_salary FROM tbl_job_titles WHERE fld_job_title_id = ?";
        try (PreparedStatement salaryStmt = conn.prepareStatement(salaryQuery)) {
            salaryStmt.setInt(1, jobTitleId); // Use setInt for jobTitleId
            ResultSet resultSet = salaryStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("fld_salary");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching salary for job title: " + e.getMessage());
        }
        return 0.0; 
    }
}
