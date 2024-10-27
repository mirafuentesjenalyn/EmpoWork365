package EmpoWork365;

import java.sql.Timestamp; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class AttendanceMethod {
    private final Connection connection;

    public AttendanceMethod(Connection connection) {
        this.connection = connection;
    }

    public void clockIn(int employeeId) throws SQLException {
        // Check if the current day is a weekday (Monday to Friday)
        if (isWeekday()) {
            // Check if already clocked in for today
            if (hasClockedIn(employeeId)) {
                throw new SQLException("Already clocked in for today.");
            }

            String sql = "INSERT INTO tbl_attendance (fld_employee_id, fld_attendance_date, fld_time_in) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);
                preparedStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now())); 
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); 

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Clock-in failed, no rows affected.");
                }
            }
        } else {
            throw new SQLException("Clock-in is only allowed on weekdays (Monday to Friday).");
        }
    }

    public void clockOut(int employeeId) throws SQLException {
        // Check if the current day is a weekday (Monday to Friday)
        if (isWeekday()) {
            // Check if already clocked out for today
            if (hasClockedOut(employeeId)) {
                throw new SQLException("Already clocked out for today.");
            }

            // Ensure that the employee has clocked in for today
            if (!hasClockedIn(employeeId)) {
                throw new SQLException("Cannot clock out without clocking in first.");
            }

            String sql = "UPDATE tbl_attendance SET fld_time_out = ? "
                    + "WHERE fld_employee_id = ? AND DATE(fld_time_in) = CURDATE() AND fld_time_out IS NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); 
                preparedStatement.setInt(2, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Clock-out failed, no rows affected or already clocked out today.");
                }
            }
        } else {
            throw new SQLException("Clock-out is only allowed on weekdays (Monday to Friday).");
        }
    }
    
    public void recordTimeIn(int employeeId) throws SQLException {
        clockIn(employeeId); 
    }

    public void recordTimeOut(int employeeId) throws SQLException {
        clockOut(employeeId); 
    }


    public boolean hasClockedIn(int employeeId) {
        String sql = "SELECT COUNT(*) FROM tbl_attendance WHERE fld_employee_id = ? AND DATE(fld_time_in) = CURDATE()";
        return executeCountQuery(sql, employeeId) > 0;
    }

    public boolean hasClockedOut(int employeeId) {
        String sql = "SELECT COUNT(*) FROM tbl_attendance WHERE fld_employee_id = ? AND DATE(fld_time_out) = CURDATE()";
        return executeCountQuery(sql, employeeId) > 0;
    }

    private int executeCountQuery(String sql, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalHoursWorkedInMonth(int employeeId, int month, int year) throws SQLException {
        String sql = "SELECT fld_time_in, fld_time_out FROM tbl_attendance " +
                     "WHERE fld_employee_id = ? AND MONTH(fld_attendance_date) = ? AND YEAR(fld_attendance_date) = ?";

        double totalHours = 0.0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, month);  // Ensure month is 1-12 for SQL
            preparedStatement.setInt(3, year);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Timestamp timeIn = rs.getTimestamp("fld_time_in");
                    Timestamp timeOut = rs.getTimestamp("fld_time_out");

                    if (timeIn != null && timeOut != null) {
                        long duration = timeOut.getTime() - timeIn.getTime();  // Calculate duration in milliseconds
                        totalHours += duration / (1000.0 * 60 * 60);  // Convert to hours
                    }
                }
            }
        }

        // Subtract 20 hours for lunch breaks for the month
        totalHours -= 20.0; // Adjust for lunch breaks

        return Math.max(totalHours, 0); // Ensure total hours cannot be negative
    }



    public int getUnpaidLeaveDays(int employeeId, int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tbl_leave_applications WHERE fld_employee_id = ? AND fld_leave_type_id = 4 AND fld_status = 'Approved' AND MONTH(fld_date_leave_request) = ? AND YEAR(fld_date_leave_request) = ?";  
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, month);
            preparedStatement.setInt(3, year);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); 
            }
        }
        return 0; 
    }

    public int getLeaveBalance(int employeeId) throws SQLException {
        String sql = "SELECT fld_remaining_days FROM tbl_leave_balances WHERE fld_employee_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("fld_remaining_days"); 
            }
        }
        return 0; 
    }
    
    public void processDeduction(int employeeId, double deduction) throws SQLException {
        String deductionQuery = "UPDATE tbl_payroll SET fld_deductions = fld_deductions + ? WHERE fld_employee_id = ?";
        try (PreparedStatement deductionStatement = connection.prepareStatement(deductionQuery)) {
            deductionStatement.setDouble(1, deduction);
            deductionStatement.setInt(2, employeeId);
            deductionStatement.executeUpdate();
        }
    }

    // Helper method to check if today is a weekday
    private boolean isWeekday() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }
    
    public int getTotalHoursWorkedInWorkdays(int employeeId, int month, int year) throws SQLException {
        double totalHours = 0.0;

        String query = "SELECT fld_time_in, fld_time_out FROM tbl_attendance " +
                       "WHERE fld_employee_id = ? AND MONTH(fld_attendance_date) = ? AND YEAR(fld_attendance_date) = ? " +
                       "AND DAYOFWEEK(fld_attendance_date) BETWEEN 2 AND 6"; // 2 = Monday, 6 = Friday

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp timeIn = rs.getTimestamp("fld_time_in");
                Timestamp timeOut = rs.getTimestamp("fld_time_out");

                if (timeIn != null && timeOut != null) {
                    long duration = timeOut.getTime() - timeIn.getTime(); // Calculate duration in milliseconds
                    totalHours += duration / (1000.0 * 60 * 60); // Convert to hours
                }
            }
        }

        // Subtract 20 hours for lunch breaks for the month
        totalHours -= 20.0; // Adjust for lunch breaks

        return (int) Math.max(totalHours, 0); // Ensure total hours cannot be negative and return as int
    }


}
