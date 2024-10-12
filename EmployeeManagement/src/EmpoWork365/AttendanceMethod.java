package EmpoWork365;

import java.sql.Timestamp; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceMethod {
    private final Connection connection;
    private static final LocalTime CLOCK_IN_TIME = LocalTime.of(8, 0); // 8:00 AM
    private static final LocalTime CLOCK_OUT_TIME = LocalTime.of(17, 0); // 5:00 PM
    private static final LocalTime ABSENT_CUTOFF_TIME = LocalTime.of(9, 0); // 9:00 AM

    
    public AttendanceMethod(Connection connection) {
        this.connection = connection;
    }

    public void clockIn(int employeeId) throws SQLException {
        String status;

        LocalTime currentTime = LocalTime.now();
        
        if (currentTime.isAfter(CLOCK_IN_TIME) && currentTime.isBefore(ABSENT_CUTOFF_TIME)) {
            status = "Late"; // If current time is after 8:00 AM but before 9:00 AM
        } else if (currentTime.isAfter(ABSENT_CUTOFF_TIME)) {
            status = "Absent"; // If current time is after 9:00 AM
        } else {
            status = "Present"; // If current time is before or at 8:00 AM
        }

        String sql = "INSERT INTO tbl_attendance (fld_employee_id, fld_attendance_date, fld_time_in, fld_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now())); 
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); 
            preparedStatement.setString(4, status); 

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Clock-in failed, no rows affected.");
            }
        }
    }
   
    public void clockOut(int employeeId) throws SQLException {
        String status;

        LocalTime currentTime = LocalTime.now();

        if (currentTime.isAfter(CLOCK_OUT_TIME)) {
            status = "Overtime"; // If current time is after 5:00 PM
        } else {
            status = "Present"; // If current time is before or at 5:00 PM
        }

        String sql = "UPDATE tbl_attendance SET fld_time_out = ?, fld_status = ? WHERE fld_employee_id = ? AND DATE(fld_time_in) = CURDATE() AND fld_time_out IS NULL";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis())); 
            preparedStatement.setString(2, status); 
            preparedStatement.setInt(3, employeeId);

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
    
}
