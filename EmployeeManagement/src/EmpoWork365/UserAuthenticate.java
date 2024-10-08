package EmpoWork365;

import java.util.regex.Pattern;

public class UserAuthenticate {
    private String firstname; 
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String jobtitle;
    private String departmentName; // Changed to hold department name
    private String roleName;       // Changed to hold role name
    private String imagepath; 

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Constructor
    public UserAuthenticate(String firstname, String lastname, String email, String password, 
                            String gender, String jobtitle, String departmentName, 
                            String roleName, String imagepath) {
        if (firstname == null || lastname == null || email == null || password == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = hashPassword(password); // Hash the password
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName; 
        this.roleName = roleName;               
        this.imagepath = imagepath;
    }

    // Password hashing method (using BCrypt as an example)
    private String hashPassword(String password) {
        // Implement password hashing logic here
        // For example: return BCrypt.hashpw(password, BCrypt.gensalt());
        return password; // Placeholder
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
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

    public String getPassword() {
        return password; // Consider returning hashed password or nothing
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

    public String getRoleName() {
        return roleName;       
    }

    public String getImagepath() {
        return imagepath;
    }

    @Override
    public String toString() {
        return "UserAuthenticate{" +
               "firstname='" + firstname + '\'' +
               ", lastname='" + lastname + '\'' +
               ", email='" + email + '\'' +
               ", gender='" + gender + '\'' +
               ", jobtitle='" + jobtitle + '\'' +
               ", departmentName='" + departmentName + '\'' +
               ", roleName='" + roleName + '\'' +
               ", imagepath='" + imagepath + '\'' +
               '}';
    }
}
