package EmpoWork365;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Employee {
    private int employeeId;
    private String employeeID;
    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private String jobtitle;
    private String departmentName;
    private String imagePath;
    private Date dateOfEmployment;
    BigDecimal ratePerHour;
    private BigDecimal netSalary;
    private BigDecimal philHealthDeduction;
    private BigDecimal sssDeduction;
    private BigDecimal pagIbigDeduction;
    private BigDecimal incomeTax;
    private BigDecimal unpaidLeaveDays;
    private BigDecimal sickLeave;
    private BigDecimal sickLeaveUsed;
    private BigDecimal emergencyLeave;
    private BigDecimal emergencyLeaveUsed;
    private BigDecimal vacationLeave;
    private BigDecimal vacationLeaveUsed;
    private String payrollMonth;
    private BigDecimal leaveBalance;
    private BigDecimal unusedLeave;

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
    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, String imagePath, BigDecimal ratePerHour,
                    BigDecimal netSalary, BigDecimal philHealthDeduction, BigDecimal sssDeduction, BigDecimal pagIbigDeduction, BigDecimal incomeTax) {
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

    public Employee(int employeeId, String firstname, String lastname, String email, String gender, String jobtitle, String departmentName, String imagePath, BigDecimal ratePerHour,
                    BigDecimal netSalary, BigDecimal philHealthDeduction, BigDecimal sssDeduction, BigDecimal pagIbigDeduction, BigDecimal incomeTax, 
                    BigDecimal unpaidLeaveDays, BigDecimal sickLeave, BigDecimal emergencyLeave, BigDecimal vacationLeave, 
                    BigDecimal sickLeaveUsed, BigDecimal emergencyLeaveUsed, BigDecimal vacationLeaveUsed, String payrollMonth, 
                    BigDecimal leaveBalance, BigDecimal unusedLeave) {
        
        this.employeeId = employeeId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.gender = gender;
        this.jobtitle = jobtitle;
        this.departmentName = departmentName;
        this.imagePath = imagePath;
        this.ratePerHour = ratePerHour != null ? ratePerHour : BigDecimal.ZERO;  // Ensure it's never null
        this.netSalary = netSalary != null ? netSalary : BigDecimal.ZERO;  // Ensure it's never null
        this.philHealthDeduction = philHealthDeduction != null ? philHealthDeduction : BigDecimal.ZERO;  // Ensure it's never null
        this.sssDeduction = sssDeduction != null ? sssDeduction : BigDecimal.ZERO;  // Ensure it's never null
        this.pagIbigDeduction = pagIbigDeduction != null ? pagIbigDeduction : BigDecimal.ZERO;  // Ensure it's never null
        this.incomeTax = incomeTax != null ? incomeTax : BigDecimal.ZERO;  // Ensure it's never null
        this.unpaidLeaveDays = unpaidLeaveDays != null ? unpaidLeaveDays : BigDecimal.ZERO;  // Ensure it's never null
        this.sickLeave = sickLeave != null ? sickLeave : BigDecimal.ZERO;  // Ensure it's never null
        this.emergencyLeave = emergencyLeave != null ? emergencyLeave : BigDecimal.ZERO;  // Ensure it's never null
        this.vacationLeave = vacationLeave != null ? vacationLeave : BigDecimal.ZERO;  // Ensure it's never null
        this.sickLeaveUsed = sickLeaveUsed != null ? sickLeaveUsed : BigDecimal.ZERO;  // Ensure it's never null
        this.emergencyLeaveUsed = emergencyLeaveUsed != null ? emergencyLeaveUsed : BigDecimal.ZERO;  // Ensure it's never null
        this.vacationLeaveUsed = vacationLeaveUsed != null ? vacationLeaveUsed : BigDecimal.ZERO;  // Ensure it's never null
        this.payrollMonth = payrollMonth != null ? payrollMonth : "January";  // Default payroll month
        this.leaveBalance = leaveBalance != null ? leaveBalance : BigDecimal.ZERO;  // Ensure it's never null
        this.unusedLeave = unusedLeave != null ? unusedLeave : BigDecimal.ZERO;  // Ensure it's never null
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

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeId(String employeeID) {
        this.employeeID = employeeID;
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

    public BigDecimal getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(BigDecimal ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public BigDecimal getPhilHealthDeduction() {
        return philHealthDeduction;
    }

    public void setPhilHealthDeduction(BigDecimal philHealthDeduction) {
        this.philHealthDeduction = philHealthDeduction;
    }

    public BigDecimal getSSSDeduction() {
        return sssDeduction;
    }

    public void setSSSDeduction(BigDecimal sssDeduction) {
        this.sssDeduction = sssDeduction;
    }

    public BigDecimal getPagIbigDeduction() {
        return pagIbigDeduction;
    }

    public void setPagIbigDeduction(BigDecimal pagIbigDeduction) {
        this.pagIbigDeduction = pagIbigDeduction;
    }

    public BigDecimal getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(BigDecimal incomeTax) {
        this.incomeTax = incomeTax;
    }

    public BigDecimal getUnpaidLeaveDays() {
        return unpaidLeaveDays;
    }

    public void setUnpaidLeaveDays(BigDecimal unpaidLeaveDays) {
        this.unpaidLeaveDays = unpaidLeaveDays;
    }

    public BigDecimal getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(BigDecimal sickLeave) {
        this.sickLeave = sickLeave;
    }

    public BigDecimal getSickLeaveUsed() {
        return sickLeaveUsed;
    }

    public void setSickLeaveUsed(BigDecimal sickLeaveUsed) {
        this.sickLeaveUsed = sickLeaveUsed;
    }

    public BigDecimal getEmergencyLeave() {
        return emergencyLeave;
    }

    public void setEmergencyLeave(BigDecimal emergencyLeave) {
        this.emergencyLeave = emergencyLeave;
    }

    public BigDecimal getEmergencyLeaveUsed() {
        return emergencyLeaveUsed;
    }

    public void setEmergencyLeaveUsed(BigDecimal emergencyLeaveUsed) {
        this.emergencyLeaveUsed = emergencyLeaveUsed;
    }

    public BigDecimal getVacationLeave() {
        return vacationLeave;
    }

    public void setVacationLeave(BigDecimal vacationLeave) {
        this.vacationLeave = vacationLeave;
    }

    public BigDecimal getVacationLeaveUsed() {
        return vacationLeaveUsed;
    }

    public void setVacationLeaveUsed(BigDecimal vacationLeaveUsed) {
        this.vacationLeaveUsed = vacationLeaveUsed;
    }

    public String getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public BigDecimal getLeaveBalance() {
        return leaveBalance;
    }

   // Method to calculate 13th month pay
    public BigDecimal calculateThirteenthMonthPay(BigDecimal totalSalary) {
        // Dividing totalSalary by 12 for the 13th month pay, using HALF_UP rounding mode
        return totalSalary.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
    }


    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
