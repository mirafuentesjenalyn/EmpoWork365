package EmpoWork365;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class EmployeeMethod {
    private final Connection connection;

    // Constructor to receive the Connection object
    public EmployeeMethod(Connection connection) {
        this.connection = connection;
    }

    public DefaultTableModel getEmployeeData() throws SQLException {
        String[] columnNames = {
            "Employee ID", "Full Name", "Email", "Gender", 
            "Job Title", "Department", "Date of Employment"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT e.fld_employee_id, "
                     + "CONCAT(u.fld_first_name, ' ', u.fld_last_name) AS full_name, "
                     + "u.fld_email, u.fld_gender, "
                     + "jt.fld_job_title, " 
                     + "d.fld_department_name, "
                     + "e.fld_date_of_employment "
                     + "FROM tbl_employees e "
                     + "INNER JOIN tbl_users u ON e.fld_user_id = u.fld_user_id "
                     + "INNER JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                     + "INNER JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id;";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("fld_email"),
                    resultSet.getString("fld_gender"),
                    resultSet.getString("fld_job_title"), // Use job title directly from resultSet
                    resultSet.getString("fld_department_name"),
                    resultSet.getDate("fld_date_of_employment")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching employee data: " + e.getMessage(), e);
        }

        return model; 
    }
    
    public List<EmployeeSearch> searchEmployeeMethod(String searchTerm) {
        List<EmployeeSearch> employeeList = new ArrayList<>();

        String sql = "SELECT e.fld_employee_id, "
                     + "u.fld_first_name, "
                     + "u.fld_last_name, "
                     + "u.fld_email, "
                     + "jt.fld_job_title, " 
                     + "d.fld_department_name "
                     + "FROM tbl_users u "
                     + "JOIN tbl_employees e ON u.fld_user_id = e.fld_user_id "
                     + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                     + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                     + "WHERE LOWER(CONCAT(u.fld_first_name, ' ', u.fld_last_name)) LIKE ? "
                     + "OR LOWER(u.fld_first_name) LIKE ? "
                     + "OR LOWER(u.fld_last_name) LIKE ? "
                     + "OR LOWER(u.fld_email) LIKE ?";

        String searchPattern = "%" + searchTerm.toLowerCase() + "%";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmployeeSearch employee = new EmployeeSearch(
                        rs.getString("fld_first_name"),  // First Name
                        rs.getString("fld_last_name"),   // Last Name
                        rs.getString("fld_email"),        // Email
                        rs.getString("fld_job_title"),    // Job Title
                        rs.getString("fld_department_name") // Department Name
                    );
                    employeeList.add(employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return employeeList;
    }


    public boolean deleteEmployeeById(int employeeId) {
        String deletePayrollSQL = "DELETE FROM tbl_payroll WHERE fld_employee_id = ?";
        String deleteAttendanceSQL = "DELETE FROM tbl_attendance WHERE fld_employee_id = ?";
        String deleteEmployeeSQL = "DELETE FROM tbl_employees WHERE fld_employee_id = ?";
        String deleteUserSQL = "DELETE FROM tbl_users WHERE fld_user_id = (SELECT fld_user_id FROM tbl_employees WHERE fld_employee_id = ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement payrollStmt = connection.prepareStatement(deletePayrollSQL)) {
                payrollStmt.setInt(1, employeeId);
                payrollStmt.executeUpdate();
            }

            try (PreparedStatement attendanceStmt = connection.prepareStatement(deleteAttendanceSQL)) {
                attendanceStmt.setInt(1, employeeId);
                attendanceStmt.executeUpdate();
            }

            try (PreparedStatement employeeStmt = connection.prepareStatement(deleteEmployeeSQL)) {
                employeeStmt.setInt(1, employeeId);
                int employeeRowsAffected = employeeStmt.executeUpdate();

                try (PreparedStatement userStmt = connection.prepareStatement(deleteUserSQL)) {
                    userStmt.setInt(1, employeeId);
                    userStmt.executeUpdate();
                }

                if (employeeRowsAffected > 0) {
                    connection.commit();
                    return true;
                }
            }

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
            try {
                connection.rollback(); // Rollback in case of an error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // Reset auto-commit to true after the transaction
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false; 
    }
}
