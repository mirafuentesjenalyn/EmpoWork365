package EmpoWork365;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jenal
 */
public class EmployeeSearch {
    private String firstname;
    private String lastname;
    private String email;
    private String jobtitle;
    private String departmentName;

    // Constructor
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

    // Static method to search employees by full name
    public static List<EmployeeSearch> searchEmployeeMethod(List<EmployeeSearch> employees, String fullName) {
        List<EmployeeSearch> results = new ArrayList<>();
        for (EmployeeSearch employee : employees) {
            String employeeFullName = employee.getFirstname() + " " + employee.getLastname();
            if (employeeFullName.toLowerCase().contains(fullName.toLowerCase())) {
                results.add(employee);
            }
        }
        return results;
    }
}
