package EmpoWork365;

public class EmployeeSearch {
    private String firstname;
    private String lastname;
    private String email;
    private String jobtitle;
    private String departmentName;

    // No-argument constructor
    public EmployeeSearch() {}

    // Constructor with parameters
    public EmployeeSearch(String firstname, String lastname, String email, String jobtitle, String departmentName) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
    }

    // Getters
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
