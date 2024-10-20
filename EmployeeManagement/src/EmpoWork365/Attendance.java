/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

import java.security.Timestamp;

/**
 *
 * @author jenal
 */

public class Attendance {
    private int attendanceId; 
    private int employeeId;   
    private String firstname;  
    private String lastname;   
    private String email;    
    private String jobTitle;
    private String departmentName;
    private Timestamp timeIn; 
    private Timestamp timeOut;

    public Attendance() {}

    public Attendance(int attendanceId, int employeeId, String firstname, String lastname, String email, String jobTitle, String departmentName, Timestamp timeIn, Timestamp timeOut) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.jobTitle = jobTitle;
        this.departmentName = departmentName;
        this.timeIn = timeIn; 
        this.timeOut = timeOut; 
    }
    
    public Attendance(int employeeId, String firstname, String lastname) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Getters
    public int getAttendanceId() {
        return attendanceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    
    public Timestamp getTimeIn() { 
        return timeIn;
    }

    public Timestamp getTimeOut() {
        return timeOut;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
