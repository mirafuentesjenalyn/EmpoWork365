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
    private String imagePath;
    private Date dateOfEmployment;
    private Double ratePerHour;
    private Double netSalary; 
    private Double philHealthDeduction; 
    private Double sssDeduction; 
    private Double pagIbigDeduction; 
    private Double incomeTax;

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
    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, 
                       String departmentName, String imagePath, Double ratePerHour, 
                       Double netSalary, Double philHealthDeduction, Double sssDeduction, Double pagIbigDeduction, 
                       Double incomeTax) {
           this.employeeId = employeeId;
           this.firstname = firstname;
           this.lastname = lastname;
           this.email = email;
           this.gender = gender;
           this.jobtitle = jobtitle;
           this.departmentName = departmentName;
           this.imagePath = imagePath;
           this.ratePerHour = ratePerHour;
           this.netSalary = netSalary;
           this.philHealthDeduction = philHealthDeduction;
           this.sssDeduction = sssDeduction;
           this.pagIbigDeduction = pagIbigDeduction;
           this.incomeTax = incomeTax;
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
    
    public String getImagePath() {
        return imagePath;
    }

    public String getDepartmentName() {
        return departmentName;
    }
    
    public Date getDateOfEmployment() {
        return dateOfEmployment;
    }
    
    public Double getRatePerHour() {
        return ratePerHour;
    }
    
    public double getNetSalary() {
        return netSalary;
    }

    public double getPhilHealthDeduction() {
        return philHealthDeduction;
    }

    public double getSSSDeduction() {
        return sssDeduction;
    }

    public double getPagIbigDeduction() {
        return pagIbigDeduction;
    }

    public double getIncomeTax() {
        return incomeTax;
    }
    

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
