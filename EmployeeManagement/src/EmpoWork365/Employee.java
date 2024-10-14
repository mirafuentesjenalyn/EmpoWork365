package EmpoWork365;

import java.util.Date;

public class Employee {
    private int employeeId;
    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private String jobtitle;
    private String departmentName;
    private Date dateOfEmployment;

    // Constructor with parameters
    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, Date dateOfEmployment) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.dateOfEmployment = dateOfEmployment;
    }

    // Getters
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
    
    public String getGender() {
        return gender;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    
    public Date getDateOfEmployment() {
        return dateOfEmployment;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
