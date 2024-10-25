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
    private String payrollMonth;
    private Double leaveBalance;
    private Double unusedLeave;

    // Constructor with parameters
    public Employee(String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, Date dateOfEmployment) {
        this.employeeId = 0;  // Assuming employeeId is generated elsewhere
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.dateOfEmployment = dateOfEmployment;
    }

    // Another constructor
    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, String imagePath) {
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.imagePath = imagePath;
    }

    // Constructor for payroll calculations
    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, String imagePath, Double ratePerHour,
                    Double netSalary, Double philHealthDeduction, Double sssDeduction, Double pagIbigDeduction, Double incomeTax) {
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

    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, String imagePath, Double ratePerHour,
                    Double netSalary, Double philHealthDeduction, Double sssDeduction, Double pagIbigDeduction, Double incomeTax, Double unpaidLeaveDays, Double sickLeave, 
                    Double emergencyLeave, Double vacationLeave, Double sickLeaveUsed, Double emergencyLeaveUsed, Double vacationLeaveUsed, String payrollMonth, Double leaveBalance, Double unusedLeave) {
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
        this.unpaidLeaveDays = unpaidLeaveDays != null ? unpaidLeaveDays : 0.0;
        this.sickLeave = sickLeave != null ? sickLeave : 0.0;
        this.emergencyLeave = emergencyLeave != null ? emergencyLeave : 0.0;
        this.vacationLeave = vacationLeave != null ? vacationLeave : 0.0;
        this.sickLeaveUsed = sickLeaveUsed != null ? sickLeaveUsed : 0.0;
        this.emergencyLeaveUsed = emergencyLeaveUsed != null ? emergencyLeaveUsed : 0.0;
        this.vacationLeaveUsed = vacationLeaveUsed != null ? vacationLeaveUsed : 0.0;
        this.payrollMonth = payrollMonth != null ? payrollMonth : "January";  // Default payroll month
        this.leaveBalance = leaveBalance;
        this.unusedLeave = unusedLeave;
    }

    Employee() {
    }

    // Getters and setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getDateOfEmployment() {
        return dateOfEmployment;
    }

    public void setDateOfEmployment(Date dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
    }

    public Double getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(Double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public Double getPhilHealthDeduction() {
        return philHealthDeduction;
    }

    public void setPhilHealthDeduction(Double philHealthDeduction) {
        this.philHealthDeduction = philHealthDeduction;
    }

    public Double getSSSDeduction() {
        return sssDeduction;
    }

    public void setSSSDeduction(Double sssDeduction) {
        this.sssDeduction = sssDeduction;
    }

    public Double getPagIbigDeduction() {
        return pagIbigDeduction;
    }

    public void setPagIbigDeduction(Double pagIbigDeduction) {
        this.pagIbigDeduction = pagIbigDeduction;
    }

    public Double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(Double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public Double getUnpaidLeaveDays() {
        return unpaidLeaveDays;
    }

    public void setUnpaidLeaveDays(Double unpaidLeaveDays) {
        this.unpaidLeaveDays = unpaidLeaveDays;
    }

    public Double getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(Double sickLeave) {
        this.sickLeave = sickLeave;
    }

    public Double getSickLeaveUsed() {
        return sickLeaveUsed;
    }

    public void setSickLeaveUsed(Double sickLeaveUsed) {
        this.sickLeaveUsed = sickLeaveUsed;
    }

    public Double getEmergencyLeave() {
        return emergencyLeave;
    }

    public void setEmergencyLeave(Double emergencyLeave) {
        this.emergencyLeave = emergencyLeave;
    }

    public Double getEmergencyLeaveUsed() {
        return emergencyLeaveUsed;
    }

    public void setEmergencyLeaveUsed(Double emergencyLeaveUsed) {
        this.emergencyLeaveUsed = emergencyLeaveUsed;
    }

    public Double getVacationLeave() {
        return vacationLeave;
    }

    public void setVacationLeave(Double vacationLeave) {
        this.vacationLeave = vacationLeave;
    }

    public Double getVacationLeaveUsed() {
        return vacationLeaveUsed;
    }

    public void setVacationLeaveUsed(Double vacationLeaveUsed) {
        this.vacationLeaveUsed = vacationLeaveUsed;
    }

    public String getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
    }



    // Method to calculate 13th month pay
    public double calculateThirteenthMonthPay(double totalSalary) {
        return totalSalary / 12;  // Simplified formula for 13th-month pay
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
