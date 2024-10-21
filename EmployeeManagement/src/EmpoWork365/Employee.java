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
    private Double unpaidLeaveDays;
    private Double sickLeave;
    private Double sickLeaveUsed; 
    private Double emergencyLeave;
    private Double emergencyLeaveUsed; 
    private Double vacationLeave;
    private Double vacationLeaveUsed;

    // Constructor with parameters
    public Employee(String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, Date dateOfEmployment) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.dateOfEmployment = dateOfEmployment;
    }
    
    public Employee(int employeeId, String firstname, String lastname, String email,
                    String gender, String jobtitle, String departmentName, String imagePath) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.imagePath = imagePath;
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
    
    public Employee(int employeeId, String firstname, String lastname, String email, String gender,
                String jobtitle, String departmentName, String imagePath, Double ratePerHour, 
                Double netSalary, Double philHealthDeduction, Double sssDeduction, 
                Double pagIbigDeduction, Double incomeTax, Double unpaidLeaveDays,
                Double sickLeave, Double emergencyLeave, Double vacationLeave,
                Double sickLeaveUsed, Double emergencyLeaveUsed, Double vacationLeaveUsed) {
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
        this.unpaidLeaveDays = unpaidLeaveDays;
        this.sickLeave = sickLeave;
        this.emergencyLeave = emergencyLeave;
        this.vacationLeave = vacationLeave;
        this.sickLeaveUsed = sickLeaveUsed;
        this.emergencyLeaveUsed = emergencyLeaveUsed;
        this.vacationLeaveUsed = vacationLeaveUsed;
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
    
    public Double getNetSalary() {
        return netSalary;
    }

    public Double getPhilHealthDeduction() {
        return philHealthDeduction;
    }

    public Double getSSSDeduction() {
        return sssDeduction;
    }

    public Double getPagIbigDeduction() {
        return pagIbigDeduction;
    }

    public Double getIncomeTax() {
        return incomeTax;
    }
    
    public Double getUnpaidLeaveDays() {
        return unpaidLeaveDays;
    }
    
    public Double getSickLeave() {
        return sickLeave;
    }

    public Double getEmergencyLeave() {
        return emergencyLeave;
    }

    public Double getVacationLeave() {
        return vacationLeave;
    }
    
    public void setSickLeave(Double sickLeave) {
        this.sickLeave = sickLeave;
    }

    public void setEmergencyLeave(Double emergencyLeave) {
        this.emergencyLeave = emergencyLeave;
    }
        
    public void setVacationLeave(Double vacationLeave) {
        this.vacationLeave = vacationLeave;
    }
    
   public void setSickLeaveUsed(Double usedDays) {
        this.sickLeaveUsed = usedDays;
    }

    public void setEmergencyLeaveUsed(Double usedDays) {
        this.emergencyLeaveUsed = usedDays;
    }

    public void setVacationLeaveUsed(Double usedDays) {
        this.vacationLeaveUsed = usedDays;
    }

    public Double getSickLeaveUsed() {
        return sickLeaveUsed;
    }

    public Double getEmergencyLeaveUsed() {
        return emergencyLeaveUsed;
    }

    public Double getVacationLeaveUsed() {
        return vacationLeaveUsed;
    }
    
    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
