/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

/**
 *
 * @author jenal
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceMethod {
    private Connection connection;

    public AttendanceMethod(Connection connection) {
        this.connection = connection;
    }

    // Record time in for the user
    public void recordTimeIn(int userId) throws SQLException {
        String query = "INSERT INTO tbl_attendance (fld_employee_id, fld_time_in) VALUES (?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Record time out for the user
    public void recordTimeOut(int userId) throws SQLException {
        String query = "UPDATE tbl_attendance SET fld_time_out = NOW() WHERE fld_employee_id = ? AND fld_time_out IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Check if the user has already clocked in for today
    public boolean hasClockedIn(int userId) throws SQLException {
        String sql = "SELECT fld_time_in FROM tbl_attendance WHERE fld_employee_id = ? AND DATE(fld_time_in) = CURDATE()";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if there's a record for today
            }
        }
    }

    // Check if the user has already clocked out for today
    public boolean hasClockedOut(int userId) throws SQLException {
        String sql = "SELECT fld_time_out FROM tbl_attendance WHERE fld_employee_id = ? AND DATE(fld_time_in) = CURDATE()";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if there's a record for today
            }
        }
    }

    // Reset attendance for yesterday
    public void resetAttendanceForYesterday() throws SQLException {
        String sql = "UPDATE tbl_attendance SET fld_time_in = NULL, fld_time_out = NULL WHERE DATE(fld_time_in) = CURDATE() - INTERVAL 1 DAY";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }
}
