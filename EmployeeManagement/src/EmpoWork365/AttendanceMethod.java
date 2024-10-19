package EmpoWork365;

import java.sql.Timestamp; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AttendanceMethod {
    private final Connection connection;

    
    public AttendanceMethod(Connection connection) {
        this.connection = connection;
    }

    public void clockIn(int employeeId) throws SQLException {

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
    }
   
    public void clockOut(int employeeId) throws SQLException {

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
            preparedStatement.setInt(2, month);
            preparedStatement.setInt(3, year);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Timestamp timeIn = rs.getTimestamp("fld_time_in");
                Timestamp timeOut = rs.getTimestamp("fld_time_out");

                if (timeIn != null) {
                    Timestamp endTime = (timeOut != null) ? timeOut : new Timestamp(System.currentTimeMillis());

                    long durationInMillis = endTime.getTime() - timeIn.getTime();
                    totalHours += durationInMillis / (1000.0 * 60 * 60); 
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching attendance data: " + e.getMessage(), e);
        }
        return totalHours;
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


}
