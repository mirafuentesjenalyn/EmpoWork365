package EmpoWork365;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class EmployeeMethod {
    private final Connection connection;
    private Employee loggedInEmployee; 


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
                 + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                 + "e.fld_email, e.fld_gender, "
                 + "jt.fld_job_title, " 
                 + "d.fld_department_name, "
                 + "e.fld_date_of_employment "
                 + "FROM tbl_employees e "
                 + "INNER JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                 + "INNER JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                 + "INNER JOIN tbl_roles r ON e.fld_role_id = r.fld_role_id " // Join with roles table
                 + "WHERE r.fld_role_name <> 'Admin' " // Exclude Admin role
                 + "ORDER BY e.fld_employee_id ASC";

    try (PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {

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
    } catch (SQLException e) {
        throw new SQLException("Error fetching employee data: " + e.getMessage(), e);
    }

    return model; 
}

  public List<Employee> searchEmployeeMethod(String searchTerm) {
    List<Employee> employeeList = new ArrayList<>();

    String sql = "SELECT e.fld_employee_id, "
                 + "e.fld_first_name, "
                 + "e.fld_last_name, "
                 + "e.fld_email, "
                 + "e.fld_gender, "
                 + "e.fld_date_of_employment, "
                 + "jt.fld_job_title, " 
                 + "d.fld_department_name "
                 + "FROM tbl_employees e "
                 + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                 + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                 + "JOIN tbl_roles r ON e.fld_role_id = r.fld_role_id " // Join with roles table
                 + "WHERE r.fld_role_name <> 'Admin' " // Exclude Admin role
                 + "AND (LOWER(CONCAT(e.fld_first_name, ' ', e.fld_last_name)) LIKE ? "
                 + "OR LOWER(e.fld_first_name) LIKE ? "
                 + "OR LOWER(e.fld_last_name) LIKE ? "
                 + "OR LOWER(e.fld_email) LIKE ? "
                 + "OR LOWER(e.fld_gender) LIKE ? "
                 + "OR LOWER(e.fld_date_of_employment) LIKE ?)";

    String searchPattern = "%" + searchTerm.toLowerCase() + "%";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, searchPattern);
        pstmt.setString(4, searchPattern);
        pstmt.setString(5, searchPattern);
        pstmt.setString(6, searchPattern);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Employee employee = new Employee(
                                        rs.getString("fld_first_name"), 
                    rs.getString("fld_last_name"),   
                    rs.getString("fld_email"), 
                    rs.getString("fld_gender"),        
                    rs.getString("fld_job_title"),   
                    rs.getString("fld_department_name"),
                    rs.getDate("fld_date_of_employment")
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
        String deleteEmployeeSQL = "DELETE FROM tbl_employees WHERE fld_employee_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteEmployeeSQL)) {
            pstmt.setInt(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false; 
        }
    }
    
    public DefaultTableModel getAttendanceDataById(int employeeId) throws SQLException {
        String[] columnNames = {
            "Employee ID", "Full Name", "Time In", "Time Out", "Date"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "a.fld_time_in, a.fld_time_out, a.fld_attendance_date "
                     + "FROM tbl_attendance a "
                     + "INNER JOIN tbl_employees e ON a.fld_employee_id = e.fld_employee_id "
                     + "WHERE e.fld_employee_id = ? "
                     + "ORDER BY a.fld_attendance_date DESC";


        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

                while (resultSet.next()) {
                    Object[] row = {
                        resultSet.getInt("fld_employee_id"),
                        resultSet.getString("full_name"),
                        formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat),
                        formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat),
                        formatDate(resultSet.getDate("fld_attendance_date"), dateFormat)
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
        }

        return model;
    }

    public DefaultTableModel getAttendanceData() throws SQLException {
       String[] columnNames = {
           "Employee ID", "Full Name", "Job Title", "Department", 
           "Time In", "Time Out", "Date"
       };

       DefaultTableModel model = new DefaultTableModel(columnNames, 0);

       String query = "SELECT e.fld_employee_id, "
                    + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                    + "jt.fld_job_title, "
                    + "d.fld_department_name, "
                    + "a.fld_time_in, a.fld_time_out, a.fld_attendance_date "
                    + "FROM tbl_attendance a "
                    + "INNER JOIN tbl_employees e ON a.fld_employee_id = e.fld_employee_id "
                    + "INNER JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                    + "INNER JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                    + "ORDER BY a.fld_attendance_date DESC";

       try (PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            
           while (resultSet.next()) {
               Object[] row = {
                   resultSet.getInt("fld_employee_id"),
                   resultSet.getString("full_name"),
                   resultSet.getString("fld_job_title"),
                   resultSet.getString("fld_department_name"),
                   formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat),
                   formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat),
                   formatDate(resultSet.getDate("fld_attendance_date"), dateFormat)
               };
               model.addRow(row);
           }
       } catch (SQLException e) {
           throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
       }

       return model; 
   }
    
    public DefaultTableModel getAttendanceDataByDateAndStatus(java.sql.Date selectedDate, String statusFilter) throws SQLException {
        String[] columnNames = {
            "Employee ID", "Full Name", "Job Title", "Department", 
            "Time In", "Time Out", "Date", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Base query
        String query = "SELECT e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "jt.fld_job_title, "
                     + "d.fld_department_name, "
                     + "a.fld_time_in, a.fld_time_out, a.fld_attendance_date, "
                     + "CASE "
                     + "WHEN a.fld_employee_id IS NULL THEN 'Absent' "
                     + "WHEN a.fld_time_out IS NULL THEN 'Incomplete' "
                     + "ELSE 'Present' END AS status "
                     + "FROM tbl_employees e "
                     + "LEFT JOIN tbl_attendance a ON e.fld_employee_id = a.fld_employee_id "
                     + "AND a.fld_attendance_date = ? "
                     + "INNER JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                     + "INNER JOIN tbl_department d ON e.fld_department_id = d.fld_department_id ";

        // Add filter based on status selection from JComboBox
        switch (statusFilter) {
            case "Present":
                query += "WHERE a.fld_time_in IS NOT NULL AND a.fld_time_out IS NOT NULL ";
                break;
            case "Incomplete":
                query += "WHERE a.fld_time_in IS NOT NULL AND a.fld_time_out IS NULL ";
                break;
            case "Absent":
                query += "WHERE a.fld_time_in IS NULL AND a.fld_time_out IS NULL ";
                break;
            case "All":
            default:
                // No additional WHERE clause needed for "All"
                break;
        }

        query += "ORDER BY e.fld_employee_id ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Bind the date parameter
            statement.setDate(1, selectedDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

                while (resultSet.next()) {
                    String timeIn = formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat);
                    String timeOut = formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat);
                    String attendanceDate = formatDate(resultSet.getDate("fld_attendance_date"), dateFormat);

                    Object[] row = {
                        resultSet.getInt("fld_employee_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("fld_job_title"),
                        resultSet.getString("fld_department_name"),
                        (timeIn != null) ? timeIn : "",
                        (timeOut != null) ? timeOut : "",
                        (attendanceDate != null) ? attendanceDate : "",
                        resultSet.getString("status")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
        }

        return model;
    }


    private String formatTimestamp(Timestamp timestamp, SimpleDateFormat timeFormat) {
        return (timestamp != null) ? timeFormat.format(timestamp) : "N/A";
    }

    private String formatDate(Date date, SimpleDateFormat dateFormat) {
        return (date != null) ? dateFormat.format(date) : "N/A"; 
    }

    public Employee getEmployeeIdById(int employeeId) throws SQLException {
        String query = "SELECT e.fld_employee_id, "
                     + "e.fld_first_name, "
                     + "e.fld_last_name, "
                     + "e.fld_email, "
                     + "e.fld_gender, "
                     + "e.fld_image_path, "
                     + "jt.fld_job_title, "
                     + "jt.fld_rate_per_hour, "
                     + "d.fld_department_name "
                     + "FROM tbl_employees e "
                     + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                     + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                     + "JOIN tbl_roles r ON e.fld_role_id = r.fld_role_id " 
                     + "WHERE r.fld_role_name <> 'Admin' " 
                     + "AND e.fld_employee_id = ?"; 

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, employeeId); 

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("fld_employee_id"),
                        rs.getString("fld_first_name"),
                        rs.getString("fld_last_name"),
                        rs.getString("fld_email"),
                        rs.getString("fld_gender"),
                        rs.getString("fld_job_title"),
                        rs.getString("fld_department_name"),
                        rs.getString("fld_image_path"),
                        rs.getDouble("fld_rate_per_hour"),
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                } else {
                    return null; 
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching employee data: " + e.getMessage(), e);
        }
    }
    
    public Employee getEmployeeByName(String name) throws SQLException {
        String query = "SELECT e.fld_employee_id, "
                     + "e.fld_first_name, "
                     + "e.fld_last_name, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "e.fld_email, "
                     + "e.fld_gender, "
                     + "e.fld_image_path, "
                     + "jt.fld_job_title, "
                     + "jt.fld_rate_per_hour, "
                     + "d.fld_department_name "
                     + "FROM tbl_employees e "
                     + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                     + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                     + "JOIN tbl_roles r ON e.fld_role_id = r.fld_role_id "
                     + "WHERE r.fld_role_name <> 'Admin' "
                     + "AND (e.fld_first_name LIKE ? OR e.fld_last_name LIKE ? "
                     + "OR CONCAT(e.fld_first_name, ' ', e.fld_last_name) LIKE ?)"; 


        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchTerm = "%" + name + "%";  
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("fld_employee_id"),
                        rs.getString("fld_first_name"),
                        rs.getString("fld_last_name"),
                        rs.getString("fld_email"),
                        rs.getString("fld_gender"),
                        rs.getString("fld_job_title"),
                        rs.getString("fld_department_name"),
                        rs.getString("fld_image_path"),
                        rs.getDouble("fld_rate_per_hour"),
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching employee data: " + e.getMessage(), e);
        }
    }

    public Employee getLoggedInUser(int userId) throws SQLException {
        String query = "SELECT e.fld_employee_id, "
                     + "e.fld_first_name, "
                     + "e.fld_last_name, "
                     + "e.fld_email, "
                     + "e.fld_gender, "
                     + "e.fld_image_path, "
                     + "jt.fld_job_title, "
                     + "jt.fld_rate_per_hour, "
                     + "d.fld_department_name "
                     + "FROM tbl_employees e "
                     + "JOIN tbl_department d ON e.fld_department_id = d.fld_department_id "
                     + "JOIN tbl_job_titles jt ON e.fld_job_title_id = jt.fld_job_title_id "
                     + "WHERE e.fld_employee_id = ?"; 

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("fld_employee_id"),
                        rs.getString("fld_first_name"),
                        rs.getString("fld_last_name"),
                        rs.getString("fld_email"),
                        rs.getString("fld_gender"),
                        rs.getString("fld_job_title"),
                        rs.getString("fld_department_name"),
                        rs.getString("fld_image_path"),
                        rs.getDouble("fld_rate_per_hour"),
                        null,
                        null,
                        null,
                        null,
                        null
                    );
                } else {
                    return null; 
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching logged-in user data: " + e.getMessage(), e);
        }
    }

    public void updateRatePerHourInDatabase(String jobTitle, double newRatePerHour) throws SQLException {
        String updateQuery = "UPDATE tbl_job_titles SET fld_rate_per_hour = ? WHERE fld_job_title = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setDouble(1, newRatePerHour);
            pstmt.setString(2, jobTitle);  // Update this to use job title

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update the rate. No rows affected.");
            }
        }
    }
    
    public DefaultTableModel viewLeaveApplications(int employeeId) throws SQLException {
            String[] columnNames = {
            "Application ID", "Employee ID", "Full Name", "Leave Type", 
            "Leave Request", "Status", "Date Applied"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT la.fld_application_id, "
                     + "e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "lt.fld_leave_type_name, "
                     + "la.fld_date_leave_request, "
                     + "la.fld_status, "
                     + "la.fld_request_date "
                     + "FROM tbl_leave_applications la "
                     + "INNER JOIN tbl_employees e ON la.fld_employee_id = e.fld_employee_id "
                     + "INNER JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id "
                     + "WHERE e.fld_employee_id = ? "
                     + "ORDER BY la.fld_application_id ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId); // Set employee ID parameter
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_application_id"),
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("fld_leave_type_name"),
                    resultSet.getDate("fld_date_leave_request"),
                    resultSet.getString("fld_status"),
                    resultSet.getDate("fld_request_date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving leave applications: " + e.getMessage(), e);
        }

        return model;
    }
    
    public DefaultTableModel getFilteredAttendanceByDateAndStatus(int employeeId, String date, String status) throws SQLException {
        String[] columnNames = {"Employee ID", "Attendance Date", "Time In", "Time Out", "Status"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT fld_employee_id, fld_attendance_date, fld_time_in, fld_time_out " +
                       "FROM tbl_attendance " +
                       "WHERE fld_employee_id = ? AND fld_attendance_date = ?";

        if (status.equals("Absent")) {
            query += " AND fld_time_in IS NULL"; // Assuming absence means no time-in record
        } else if (status.equals("Incomplete")) {
            query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NULL"; // Incomplete = Time-in but no Time-out
        } else if (status.equals("Present")) {
            query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NOT NULL"; // Present = Both Time-in and Time-out exist
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId);
            statement.setString(2, date);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getDate("fld_attendance_date"),
                    resultSet.getTime("fld_time_in"),
                    resultSet.getTime("fld_time_out"),
                    status
                };
                model.addRow(row);
            }
        }

        return model;
    }

    
    public DefaultTableModel searchLeaveApplications(int employeeId, String searchTerm) throws SQLException {
        String[] columnNames = {
            "Application ID", "Employee ID", "Full Name", "Leave Type", 
            "Leave Request", "Reason", "Status", "Date Applied"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT la.fld_application_id, "
                     + "e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "lt.fld_leave_type_name, "
                     + "la.fld_date_leave_request, "
                     + "la.fld_reason, "  // Include the reason in the query
                     + "la.fld_status, "
                     + "la.fld_request_date "
                     + "FROM tbl_leave_applications la "
                     + "INNER JOIN tbl_employees e ON la.fld_employee_id = e.fld_employee_id "
                     + "INNER JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id "
                     + "WHERE e.fld_employee_id = ? "
                     + "AND la.fld_reason LIKE ? "  // Add search term for the reason
                     + "ORDER BY la.fld_application_id ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId); // Set employee ID parameter
            statement.setString(2, "%" + searchTerm + "%"); // Set search term with wildcards for partial matching
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_application_id"),
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("fld_leave_type_name"),
                    resultSet.getDate("fld_date_leave_request"),
                    resultSet.getString("fld_reason"), // Add reason to the results
                    resultSet.getString("fld_status"),
                    resultSet.getDate("fld_request_date")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            throw new SQLException("Error searching leave applications: " + e.getMessage(), e);
        }

        return model;
    }
    
    public void updateLeaveStatus(int leaveId, String status) throws SQLException {
        String updateQuery = "UPDATE tbl_leave_applications SET fld_status = ? WHERE fld_application_id = ?";

        try {
            connection.setAutoCommit(false);

            // Update the leave status
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, status);
                updateStatement.setInt(2, leaveId);

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to update leave application with ID: " + leaveId);
                }
            }

            // Process based on the status
            switch (status.toLowerCase()) {
                case "approved" -> handleApprovedLeave(leaveId);
                case "rejected" -> archiveRejectedLeave(leaveId);
                case "unpaid" -> handleUnpaidLeave(leaveId);
                default -> throw new IllegalArgumentException("Invalid leave status: " + status);
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void handleApprovedLeave(int leaveId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM tbl_leave_balances WHERE fld_employee_id = ? AND fld_leave_type_id = ?";
        String insertQuery = "INSERT INTO tbl_leave_balances (fld_application_id, fld_employee_id, fld_leave_type_id, fld_remaining_days) " +
                             "SELECT la.fld_application_id, la.fld_employee_id, lt.fld_leave_type_id, lt.fld_max_days " +
                             "FROM tbl_leave_applications la " +
                             "JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id " +
                             "WHERE la.fld_application_id = ?";

        int leaveTypeId;
        int employeeId;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT fld_leave_type_id, fld_employee_id FROM tbl_leave_applications WHERE fld_application_id = ?")) {
            preparedStatement.setInt(1, leaveId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                leaveTypeId = resultSet.getInt("fld_leave_type_id");
                employeeId = resultSet.getInt("fld_employee_id");
            } else {
                throw new SQLException("Leave application not found for ID: " + leaveId);
            }
        }

        String remainingDaysQuery = "SELECT COALESCE(fld_remaining_days, lt.fld_max_days) AS remaining_days " +
                                    "FROM tbl_leave_balances lb " +
                                    "JOIN tbl_leave_types lt ON lb.fld_leave_type_id = lt.fld_leave_type_id " +
                                    "WHERE lb.fld_employee_id = ? AND lb.fld_leave_type_id = ?";
        try (PreparedStatement remainingDaysStatement = connection.prepareStatement(remainingDaysQuery)) {
            remainingDaysStatement.setInt(1, employeeId);
            remainingDaysStatement.setInt(2, leaveTypeId);
            ResultSet remainingDaysResult = remainingDaysStatement.executeQuery();
            if (remainingDaysResult.next()) {
                int remainingDays = remainingDaysResult.getInt("remaining_days");
                if (remainingDays <= 0) {
                    throw new SQLException("The employee does not have leave balance.");
                }
            }
        }

        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, employeeId);
            checkStatement.setInt(2, leaveTypeId);
            ResultSet checkResult = checkStatement.executeQuery();
            checkResult.next();
            if (checkResult.getInt(1) > 0) {
                updateLeaveBalance(employeeId, leaveTypeId); // Update existing record
                return;
            }
        }

        // Proceed with the insert if leave balance is available
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, leaveId);
            insertStatement.executeUpdate();
        }

        updateRemainingDays(leaveId);
    }

    private void updateLeaveBalance(int employeeId, int leaveTypeId) throws SQLException {
        String currentBalanceQuery = "SELECT fld_remaining_days FROM tbl_leave_balances WHERE fld_employee_id = ? AND fld_leave_type_id = ?";
        try (PreparedStatement currentBalanceStatement = connection.prepareStatement(currentBalanceQuery)) {
            currentBalanceStatement.setInt(1, employeeId);
            currentBalanceStatement.setInt(2, leaveTypeId);
            ResultSet currentBalanceResult = currentBalanceStatement.executeQuery();
            if (currentBalanceResult.next()) {
                int remainingDays = currentBalanceResult.getInt("fld_remaining_days");
                int updatedRemainingDays = remainingDays - 1; // Subtract 1 from remaining days

                String updateBalanceQuery = "UPDATE tbl_leave_balances SET fld_remaining_days = ? WHERE fld_employee_id = ? AND fld_leave_type_id = ?";
                try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery)) {
                    updateBalanceStatement.setInt(1, updatedRemainingDays);
                    updateBalanceStatement.setInt(2, employeeId);
                    updateBalanceStatement.setInt(3, leaveTypeId);
                    updateBalanceStatement.executeUpdate();
                }
            }
        }
    }

    private void updateRemainingDays(int leaveId) throws SQLException {
        String maxDaysQuery = "SELECT lt.fld_max_days FROM tbl_leave_applications la " +
                              "JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id " +
                              "WHERE la.fld_application_id = ?";

        try (PreparedStatement maxDaysStatement = connection.prepareStatement(maxDaysQuery)) {
            maxDaysStatement.setInt(1, leaveId);
            ResultSet maxDaysResult = maxDaysStatement.executeQuery();

            if (maxDaysResult.next()) {
                int maxDays = maxDaysResult.getInt("fld_max_days");

                String currentBalanceQuery = "SELECT fld_remaining_days FROM tbl_leave_balances " +
                                              "WHERE fld_employee_id = (SELECT fld_employee_id FROM tbl_leave_applications WHERE fld_application_id = ?) " +
                                              "AND fld_leave_type_id = (SELECT fld_leave_type_id FROM tbl_leave_applications WHERE fld_application_id = ?)";

                int remainingDays;
                try (PreparedStatement currentBalanceStatement = connection.prepareStatement(currentBalanceQuery)) {
                    currentBalanceStatement.setInt(1, leaveId);
                    currentBalanceStatement.setInt(2, leaveId);
                    ResultSet currentBalanceResult = currentBalanceStatement.executeQuery();

                    if (currentBalanceResult.next()) {
                        remainingDays = currentBalanceResult.getInt("fld_remaining_days");
                    } else {
                        remainingDays = maxDays;
                    }
                }

                // Update the remaining days
                int updatedRemainingDays = remainingDays - 1; // Subtract 1 from remaining days
                String updateBalanceQuery = "UPDATE tbl_leave_balances SET fld_remaining_days = ? " +
                                             "WHERE fld_employee_id = (SELECT fld_employee_id FROM tbl_leave_applications WHERE fld_application_id = ?) " +
                                             "AND fld_leave_type_id = (SELECT fld_leave_type_id FROM tbl_leave_applications WHERE fld_application_id = ?)";
                try (PreparedStatement updateBalanceStatement = connection.prepareStatement(updateBalanceQuery)) {
                    updateBalanceStatement.setInt(1, updatedRemainingDays);
                    updateBalanceStatement.setInt(2, leaveId);
                    updateBalanceStatement.setInt(3, leaveId);
                    updateBalanceStatement.executeUpdate();
                }
            }
        }
    }

     private void archiveRejectedLeave(int leaveId) throws SQLException {
        String archiveQuery = "INSERT INTO tbl_rejected_leave_applications (fld_application_id, fld_employee_id, fld_leave_type_id, fld_date_leave_request, fld_reason, fld_request_date, fld_status) " +
                              "SELECT fld_application_id, fld_employee_id, fld_leave_type_id, fld_date_leave_request, fld_reason, fld_request_date, fld_status " +
                              "FROM tbl_leave_applications WHERE fld_application_id = ?";
        try (PreparedStatement archiveStatement = connection.prepareStatement(archiveQuery)) {
            archiveStatement.setInt(1, leaveId);
            archiveStatement.executeUpdate();
        }
    }

    private void handleUnpaidLeave(int leaveId) throws SQLException {
        String checkExistsQuery = "SELECT COUNT(*) FROM tbl_unpaid_leave_records WHERE fld_application_id = ?";
        try (PreparedStatement checkExistsStatement = connection.prepareStatement(checkExistsQuery)) {
            checkExistsStatement.setInt(1, leaveId);
            ResultSet resultSet = checkExistsStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                throw new SQLException("Unpaid leave record already exists for application ID: " + leaveId);
            }
        }

        // Proceed with insertion if no duplicate exists
        String unpaidInsertQuery = "INSERT INTO tbl_unpaid_leave_records (fld_application_id, fld_employee_id, fld_leave_type_id, fld_date_leave_request, fld_reason) " +
                                   "SELECT fld_application_id, fld_employee_id, fld_leave_type_id, fld_date_leave_request, fld_reason " +
                                   "FROM tbl_leave_applications WHERE fld_application_id = ?";
        try (PreparedStatement insertStatement = connection.prepareStatement(unpaidInsertQuery)) {
            insertStatement.setInt(1, leaveId);
            insertStatement.executeUpdate();
        }
    }

    
    public Map<String, Integer> getRemainingLeaveDays(int employeeId) throws SQLException {
        Map<String, Integer> remainingDaysMap = new HashMap<>();
        String query = "SELECT lt.fld_leave_type_name, " +
                       "COALESCE(lb.fld_remaining_days, lt.fld_max_days) AS remaining_days " +
                       "FROM tbl_leave_types lt " +
                       "LEFT JOIN tbl_leave_balances lb ON lb.fld_leave_type_id = lt.fld_leave_type_id " +
                       "AND lb.fld_employee_id = ? " +
                       "WHERE lt.fld_leave_type_name IN ('Sick Leave', 'Emergency Leave', 'Vacation Leave')";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String leaveTypeName = rs.getString("fld_leave_type_name");
                int remainingDays = rs.getInt("remaining_days");
                remainingDaysMap.put(leaveTypeName, remainingDays);
            }
        }

        return remainingDaysMap; 
    }
    
    public double getUnpaidLeaveCost(Employee employee, int year) throws SQLException {
        // SQL query to fetch unpaid leave count for a year
        String query = "SELECT e.fld_employee_id, e.fld_first_name, e.fld_last_name, lt.fld_leave_type_name, " +
                       "l.fld_remaining_days, COALESCE(SUM(CASE " +
                       "WHEN l.fld_remaining_days <= 0 AND (a.fld_time_out IS NULL OR a.fld_employee_id IS NULL) THEN 1 " +
                       "ELSE 0 END), 0) AS unpaid_leave_count " +
                       "FROM tbl_leave_balances l " +
                       "JOIN tbl_employees e ON l.fld_employee_id = e.fld_employee_id " +
                       "JOIN tbl_leave_types lt ON l.fld_leave_type_id = lt.fld_leave_type_id " +
                       "LEFT JOIN tbl_attendance a ON e.fld_employee_id = a.fld_employee_id " +
                       "AND a.fld_attendance_date BETWEEN '" + year + "-01-01' AND '" + year + "-12-31' " +
                       "LEFT JOIN tbl_leave_applications la ON e.fld_employee_id = la.fld_employee_id " +
                       "AND la.fld_is_unpaid = 1 " +
                       "AND YEAR(la.fld_request_date) = " + year + " " +
                       "WHERE (l.fld_remaining_days <= 0 AND (a.fld_time_out IS NULL OR a.fld_employee_id IS NULL)) " +
                       "OR (a.fld_time_out IS NULL AND a.fld_employee_id IS NULL) " +
                       "AND (la.fld_employee_id IS NULL OR YEAR(la.fld_request_date) = " + year + ") " +
                       "GROUP BY e.fld_employee_id, e.fld_first_name, e.fld_last_name, lt.fld_leave_type_name, l.fld_remaining_days " +
                       "ORDER BY e.fld_employee_id;";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        double unpaidLeaveCost = 0;

        if (rs.next()) {
            int unpaidLeaveCount = rs.getInt("unpaid_leave_count");

            // Calculate unpaid leave cost (assuming 8 hours workday)
            double ratePerHour = employee.getRatePerHour();
            final int HOURS_PER_DAY = 8;
            unpaidLeaveCost = ratePerHour * HOURS_PER_DAY * unpaidLeaveCount;
        }

        return unpaidLeaveCost;
    }
    
    public int getTotalAbsences(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee data is null");
        }

        int totalAbsences = 0;

        try {
            int employeeId = employee.getEmployeeId();

            String query = "SELECT COUNT(a.fld_attendance_id) AS total_absences " +
                           "FROM tbl_employees e " +
                           "LEFT JOIN tbl_attendance a ON e.fld_employee_id = a.fld_employee_id " +
                           "AND YEAR(a.fld_attendance_date) = YEAR(CURDATE()) " + // Current year's attendance
                           "WHERE e.fld_employee_id = ? " +  // Employee ID
                           "AND (a.fld_attendance_id IS NULL " +  // No attendance record
                           "OR a.fld_time_in IS NULL " +  // No time-in recorded
                           "OR a.fld_time_out IS NULL)";  // No time-out recorded

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, employeeId); // Set employee ID to the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                totalAbsences = resultSet.getInt("total_absences");  // Get the total absences
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while retrieving total absences: " + e.getMessage(), e);
        }

        return totalAbsences; 
    }



}


