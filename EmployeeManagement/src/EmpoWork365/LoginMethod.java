package EmpoWork365;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class LoginMethod {
    public UserAuthenticate authenticate(String email, String password) {
        UserAuthenticate loggedInUser = null;
        sqlConnector callConnector = new sqlConnector();

        String query = "SELECT fld_user_id, fld_password, fld_first_name, fld_last_name, " +
                       "fld_job_title_id, fld_image_path, fld_gender, fld_department_id, fld_role_id " +
                       "FROM tbl_users WHERE fld_email = ?";

        try (Connection conn = callConnector.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("fld_user_id"); 
                String storedPassword = rs.getString("fld_password");  
                String firstName = rs.getString("fld_first_name");
                String lastName = rs.getString("fld_last_name");
                String imagePath = rs.getString("fld_image_path");
                String gender = rs.getString("fld_gender");
                int roleId = rs.getInt("fld_role_id");
                int departmentId = rs.getInt("fld_department_id");
                int jobTitleId = rs.getInt("fld_job_title_id"); 

                if (password.equals(storedPassword)) { 
                    loggedInUser = new UserAuthenticate(userId, firstName, lastName, email, 
                                                         storedPassword, gender, 
                                                         getJobTitleName(jobTitleId), 
                                                         getDepartmentName(departmentId), 
                                                         getRoleName(roleId), imagePath);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return loggedInUser;
    }


    private String getDepartmentName(int departmentId) {
        String departmentName = null;
        String query = "SELECT fld_department_name FROM tbl_department WHERE fld_department_id = ?";
        
        try (Connection conn = new sqlConnector().createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                departmentName = rs.getString("fld_department_name");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fetching department name: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return departmentName;
    }

    private String getRoleName(int roleId) {
        String roleName = null;
        String query = "SELECT fld_role_name FROM tbl_roles WHERE fld_role_id = ?";
        
        try (Connection conn = new sqlConnector().createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                roleName = rs.getString("fld_role_name");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fetching role name: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return roleName;
    }

    private String getJobTitleName(int jobTitleId) {
        String jobTitleName = null;
        String query = "SELECT fld_job_title FROM tbl_job_titles WHERE fld_job_title_id = ?";
        
        try (Connection conn = new sqlConnector().createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, jobTitleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                jobTitleName = rs.getString("fld_job_title");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fetching job title name: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return jobTitleName;
    }
}
