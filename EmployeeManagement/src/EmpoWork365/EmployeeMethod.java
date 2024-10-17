package EmpoWork365;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class EmployeeMethod {
    private final Connection connection;


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
                    rs.getInt("fld_employee_id"),
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
                    + "ORDER BY e.fld_employee_id ASC";

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
                    return null; // No user found
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching logged-in user data: " + e.getMessage(), e);
        }
    }
    
    public DefaultTableModel viewLeaveApplications(int employeeId) throws SQLException {
            String[] columnNames = {
            "Application ID", "Employee ID", "Full Name", "Leave Type", 
            "Start Date", "End Date", "Status", "Date Applied"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT la.fld_application_id, "
                     + "e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "lt.fld_leave_type_name, "
                     + "la.fld_start_date, "
                     + "la.fld_end_date, "
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
                    resultSet.getDate("fld_start_date"),
                    resultSet.getDate("fld_end_date"),
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


  
    public DefaultTableModel getRequestsData(int employeeId) throws SQLException {
        String[] columnNames = {
            "Application ID", "Employee ID", "Name", "Start Date",
            "End Date", "Leave Type", "Reason", "Status", "Date Applied"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT la.fld_application_id, "
                     + "e.fld_employee_id, "
                     + "CONCAT(e.fld_first_name, ' ', e.fld_last_name) AS full_name, "
                     + "la.fld_start_date, "
                     + "la.fld_end_date, "
                     + "lt.fld_leave_type_name, "
                     + "la.fld_reason, "
                     + "la.fld_status, "
                     + "la.fld_request_date "
                     + "FROM tbl_leave_applications la "
                     + "INNER JOIN tbl_employees e ON la.fld_employee_id = e.fld_employee_id "
                     + "INNER JOIN tbl_leave_types lt ON la.fld_leave_type_id = lt.fld_leave_type_id "
                     + "WHERE la.fld_employee_id = ?"; 

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId); 
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("fld_application_id"),
                    resultSet.getInt("fld_employee_id"),
                    resultSet.getString("full_name"),
                    resultSet.getDate("fld_start_date"),
                    resultSet.getDate("fld_end_date"),
                    resultSet.getString("fld_leave_type_name"),
                    resultSet.getString("fld_reason"),
                    resultSet.getString("fld_status"),
                    resultSet.getTimestamp("fld_request_date") 
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching leave applications: " + e.getMessage(), e);
        }

        return model; 
    }

}


