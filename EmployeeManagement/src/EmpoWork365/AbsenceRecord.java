/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

/**
 *
 * @author jenal
 */
public class AbsenceRecord {
    private final int employeeId;
    private final String absenceDate;

    public AbsenceRecord(int employeeId, String absenceDate) {
        this.employeeId = employeeId;
        this.absenceDate = absenceDate;
    }

    // Getters and toString() method for convenience
    public int getEmployeeId() { return employeeId; }
    public String getAbsenceDate() { return absenceDate; }

    @Override
    public String toString() {
        return "AbsenceRecord{" +
                "employeeId=" + employeeId +
                ", absenceDate='" + absenceDate + '\'' +
                '}';
    }
}