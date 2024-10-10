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
                                 String gender, String jobTitle, int departmentId, int roleId, 
                                 String dateOfEmployment, String imagePath) {
        // SQL query for inserting a new user into tbl_users
        String insertUserQuery = "INSERT INTO tbl_users (fld_first_name, fld_last_name, fld_email, fld_password, "
                + "fld_gender, fld_job_title, fld_department_id, fld_role_id, fld_date_of_employment, fld_image_path) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // SQL query for inserting a new employee into tbl_employees
        String insertEmployeeQuery = "INSERT INTO tbl_employees (fld_user_id, fld_job_title, "
                + "fld_department_id, fld_role_id, fld_date_of_employment, fld_employee_salary, fld_image_path) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Establish a database connection
        try (Connection conn = connector.createConnection();
             PreparedStatement userStmt = conn.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement employeeStmt = conn.prepareStatement(insertEmployeeQuery)) {

            // Insert into `tbl_users`
            userStmt.setString(1, firstName);
            userStmt.setString(2, lastName);
            userStmt.setString(3, email);
            userStmt.setString(4, password);
            userStmt.setString(5, gender);
            userStmt.setString(6, jobTitle);
            userStmt.setInt(7, departmentId);
            userStmt.setInt(8, roleId);
            userStmt.setString(9, dateOfEmployment);
            userStmt.setString(10, imagePath); // Corrected index

            // Execute the user statement
            int userRows = userStmt.executeUpdate();

            // Only proceed if user was inserted successfully
            if (userRows > 0) {
                // Get the generated user ID
                int generatedUserId = -1;
                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedUserId = generatedKeys.getInt(1);
                }

                // Insert into `tbl_employees`
                employeeStmt.setInt(1, generatedUserId); // Set `fld_user_id` in `tbl_employees`
                employeeStmt.setString(2, jobTitle);
                employeeStmt.setInt(3, departmentId);
                employeeStmt.setInt(4, roleId);
                employeeStmt.setString(5, dateOfEmployment);
                
                // Retrieve salary based on the job title from tbl_job_titles
                double salary = getJobTitleSalary(conn, jobTitle, departmentId);
                employeeStmt.setDouble(6, salary); // Use the retrieved salary
                employeeStmt.setString(7, imagePath);

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

    private double getJobTitleSalary(Connection conn, String jobTitle, int departmentId) {
        String salaryQuery = "SELECT fld_salary FROM tbl_job_titles WHERE fld_job_title = ? AND fld_department_id = ?";
        try (PreparedStatement salaryStmt = conn.prepareStatement(salaryQuery)) {
            salaryStmt.setString(1, jobTitle);
            salaryStmt.setInt(2, departmentId);
            ResultSet resultSet = salaryStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("fld_salary");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching salary for job title: " + e.getMessage());
        }
        return 0.0; // Default salary if not found
    }
}
