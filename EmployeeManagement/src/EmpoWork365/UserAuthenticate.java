package EmpoWork365;

import java.util.regex.Pattern;

public class UserAuthenticate {
    private int id;
    private String firstname; 
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String jobtitle;
    private String departmentName; 
    private String roleName;       
    private String imagepath;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Constructor
    public UserAuthenticate(int id, String firstname, String lastname, String email, String password, 
                            String gender, String jobtitle, String departmentName, 
                            String roleName, String imagepath) {
        if (firstname == null || lastname == null || email == null || password == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.id = id; 
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = hashPassword(password); 
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName; 
        this.roleName = roleName;               
        this.imagepath = imagepath;
    }
    
    // New constructor for essential fields
    public UserAuthenticate(int id, String firstname, String lastname, String email, String password) {
        this(id, firstname, lastname, email, password, null, null, null, null, null);
    }

   
    // Password hashing method (using BCrypt as an example)
    private String hashPassword(String password) {
        return password; // Placeholder
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    // Getters
    public int getId() {
        return id; 
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

    public String getPassword() {
        return password;
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
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

}
