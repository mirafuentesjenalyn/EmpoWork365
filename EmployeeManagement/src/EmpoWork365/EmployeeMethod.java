package EmpoWork365;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                        + "OR LOWER(e.fld_employee_id) LIKE ? "
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
            pstmt.setString(7, searchPattern);

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
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
                query += "WHERE a.fld_time_in IS NULL ";
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
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
                        rs.getBigDecimal("fld_rate_per_hour"),
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
                        rs.getBigDecimal("fld_rate_per_hour"),
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
                        rs.getBigDecimal("fld_rate_per_hour"),
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
    
    public List<String> getExpectedWorkdays(String startDate, String endDate) {
        List<String> workdays = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(java.sql.Date.valueOf(startDate));
        end.setTime(java.sql.Date.valueOf(endDate));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int month = -1;
        int weekdayCount = 0;

        while (!start.after(end)) {
            int dayOfWeek = start.get(Calendar.DAY_OF_WEEK);
            String currentDate = dateFormat.format(start.getTime());

            if (currentDate.equals(start.get(Calendar.YEAR) + "-11-01")) {
                start.add(Calendar.DATE, 1); // Skip November 1
                continue;
            }

            if (start.get(Calendar.MONTH) != month) {
                month = start.get(Calendar.MONTH);
                weekdayCount = 0;
            }

            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && weekdayCount < 20) {
                workdays.add(currentDate);
                weekdayCount++;
            }

            start.add(Calendar.DATE, 1);
        }

        return workdays;
    }



    public List<AbsenceRecord> getAbsenceRecords(int employeeId, String startDate, String endDate) throws SQLException {
        List<AbsenceRecord> absences = new ArrayList<>();

        // Get expected workdays for the specified range
        List<String> expectedWorkdays = getExpectedWorkdays(startDate, endDate);

        // Fetch attendance records for the employee in the same date range
        List<String> attendanceRecords = getAttendanceDates(employeeId, startDate, endDate);

        // Identify absences
        for (String workday : expectedWorkdays) {
            if (!attendanceRecords.contains(workday)) {
                absences.add(new AbsenceRecord(employeeId, workday)); // Log the absence
            }
        }

        return absences;
    }

    private List<String> getAttendanceDates(int employeeId, String startDate, String endDate) throws SQLException {
        List<String> attendanceDates = new ArrayList<>();
        String query = "SELECT fld_attendance_date FROM tbl_attendance " +
                       "WHERE fld_employee_id = ? AND fld_attendance_date BETWEEN ? AND ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Standardized date format

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId);
            statement.setString(2, startDate);
            statement.setString(3, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String formattedDate = dateFormat.format(resultSet.getDate("fld_attendance_date"));
                    attendanceDates.add(formattedDate); // Add the formatted date to the list
                }
            }
        }
        return attendanceDates;
    }

    public int getTotalAbsences(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee data is null");
        }
        int totalAbsences = 0;
        try {
            int employeeId = employee.getEmployeeId();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            String startDate = currentYear + "-01-01";
            String endDate = currentYear + "-12-31";

            List<String> expectedWorkdays = getExpectedWorkdays(startDate, endDate);
            List<String> attendanceRecords = getAttendanceDates(employeeId, startDate, endDate);

            for (String workday : expectedWorkdays) {
                if (!attendanceRecords.contains(workday)) {
                    totalAbsences++; // Count only days with no attendance record
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while retrieving total absences: " + e.getMessage(), e);
        }

        return totalAbsences;
    }


    private void generateAbsenceRecords(DefaultTableModel model, int employeeId) throws SQLException {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String startDate = currentYear + "-01-01";
        String endDate = currentYear + "-12-31";

        // Debugging output
        System.out.println("Generating absence records for Employee ID: " + employeeId);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        List<String> workdays = getExpectedWorkdays(startDate, endDate);
        List<String> attendanceDates = getAttendanceDates(employeeId, startDate, endDate);

        // Clear the model before adding new absence records
        model.setRowCount(0); // Clear existing records
        int count = 1; // Start count from 1

        // Create date format for displaying absence records
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy"); // Desired date format

        // Debugging output
        System.out.println("Workdays Count: " + workdays.size());
        System.out.println("Attendance Dates Count: " + attendanceDates.size());

        for (String workday : workdays) {
            if (!attendanceDates.contains(workday)) {
                // Format the workday date
                String formattedWorkday = dateFormat.format(java.sql.Date.valueOf(workday));
                model.addRow(new Object[]{count++, employeeId, formattedWorkday, null, null, "Absent"});
                // Debugging output for each absent workday added to the model
                System.out.println("Added Absent Workday to Model: " + formattedWorkday);
            }
        }
    }

    public DefaultTableModel getAttendanceByStatus(int employeeId, String status) throws SQLException {
        String[] columnNames = {"No.", "Employee ID", "Attendance Date", "Time In", "Time Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Build the SQL query
        String query = "SELECT fld_employee_id, fld_attendance_date, fld_time_in, fld_time_out " +
                       "FROM tbl_attendance " +
                       "WHERE fld_employee_id = ?";

        // Add filters for status
        switch (status) {
            case "Absent" -> query += " AND fld_time_in IS NULL AND fld_attendance_id IS NULL "; // No clock-in means absent
            case "Incomplete" -> query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NULL " +
                          "AND DAYOFWEEK(fld_attendance_date) BETWEEN 2 AND 6"; // Monday to Friday
            case "Present" -> query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NOT NULL " +
                          "AND DAYOFWEEK(fld_attendance_date) BETWEEN 2 AND 6"; // Monday to Friday
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy"); // Desired date format
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                int count = 1;

                while (resultSet.next()) {
                    Object[] row = {
                        count++,
                        resultSet.getInt("fld_employee_id"),
                        formatDate(resultSet.getDate("fld_attendance_date"), dateFormat), // Ensure date formatting
                        formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat),
                        formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat),
                        status // Status from method argument
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
            }
        }

        // Generate absence records if needed
        if (status.equals("Absent")) {
            generateAbsenceRecords(model, employeeId);
        }

        return model;
    }

    public DefaultTableModel getFilteredAttendanceByDateAndStatus(int employeeId, String date, String status) throws SQLException {
        String[] columnNames = {"No.", "Employee ID", "Attendance Date", "Time In", "Time Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        String query = "SELECT fld_employee_id, fld_attendance_date, fld_time_in, fld_time_out " +
                       "FROM tbl_attendance " +
                       "WHERE fld_employee_id = ? AND fld_attendance_date = ?";

        // Add filters for status
        switch (status) {
            case "Absent" -> query += " AND fld_time_in IS NULL AND fld_attendance_id IS NULL "; // No clock-in means absent
            case "Incomplete" -> query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NULL " +
                          "AND DAYOFWEEK(fld_attendance_date) BETWEEN 2 AND 6"; // Monday to Friday
            case "Present" -> query += " AND fld_time_in IS NOT NULL AND fld_time_out IS NOT NULL " +
                          "AND DAYOFWEEK(fld_attendance_date) BETWEEN 2 AND 6"; // Monday to Friday
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, employeeId);
            statement.setString(2, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy"); // Desired date format
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                int count = 1;

                while (resultSet.next()) {
                    Object[] row = {
                        count++,
                        resultSet.getInt("fld_employee_id"),
                        formatDate(resultSet.getDate("fld_attendance_date"), dateFormat), // Ensure date formatting
                        formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat),
                        formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat),
                        status // Status from method argument
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
            }
        }

        return model;
    }

    public DefaultTableModel getAllAttendanceRecords(int employeeId) throws SQLException {
        String[] columnNames = {"No.", "Employee ID", "Attendance Date", "Time In", "Time Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String startDate = currentYear + "-01-01"; // January 1 of the current year
        String endDate = currentYear + "-12-31";   // December 31 of the current year

        // Get expected workdays for the current year
        List<String> expectedWorkdays = getExpectedWorkdays(startDate, endDate);

        // Fetch attendance records for the employee
        List<String> attendanceRecords = getAttendanceDates(employeeId, startDate, endDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy"); // Desired date format
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        int count = 1;

        // Loop through expected workdays to check attendance
        for (String workday : expectedWorkdays) {
            // Create a new row for each workday
            Object[] row = new Object[6];
            row[0] = count++;
            row[1] = employeeId;
            row[2] = dateFormat.format(java.sql.Date.valueOf(workday)); // Format workday date

            // Check if the workday exists in the attendance records
            if (attendanceRecords.contains(workday)) {
                // If attendance exists, get the time in and time out
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT fld_time_in, fld_time_out FROM tbl_attendance WHERE fld_employee_id = ? AND fld_attendance_date = ?")) {
                    statement.setInt(1, employeeId);
                    statement.setString(2, workday);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            row[3] = formatTimestamp(resultSet.getTimestamp("fld_time_in"), timeFormat); // Time In
                            row[4] = formatTimestamp(resultSet.getTimestamp("fld_time_out"), timeFormat); // Time Out
                        }
                    }
                }
                row[5] = determineStatus(row[3], row[4]); // Determine status based on time in/out
            } else {
                // If no attendance record exists for the workday, mark as Absent
                row[3] = null; // Time In
                row[4] = null; // Time Out
                row[5] = "Absent"; // Mark as Absent
            }

            // Add the row to the model
            model.addRow(row);
        }

        return model;
    }

    private String determineStatus(Object timeIn, Object timeOut) {
        if (timeIn == null) {
            return "Absent";
        } else if (timeOut == null) {
            return "Incomplete";
        } else {
            return "Present";
        }
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
    
    public BigDecimal getUnpaidLeaveCost(Employee employee, int year) throws SQLException {
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

        BigDecimal unpaidLeaveCost = BigDecimal.ZERO; // Change to BigDecimal

        if (rs.next()) {
            int unpaidLeaveCount = rs.getInt("unpaid_leave_count");

            // Calculate unpaid leave cost (assuming 8 hours workday)
            BigDecimal ratePerHour = employee.getRatePerHour();
            final int HOURS_PER_DAY = 8;
            unpaidLeaveCost = ratePerHour.multiply(BigDecimal.valueOf(HOURS_PER_DAY)).multiply(BigDecimal.valueOf(unpaidLeaveCount));
        }

        return unpaidLeaveCost;
    }
    


}