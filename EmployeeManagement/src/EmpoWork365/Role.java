/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EmpoWork365;

/**
 *
 * @author jenal
 */
public class Role {
    private int roleId;
    private String roleName;

    // Constructor
    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // Getters
    public int getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    // Override toString() to return the department name
    @Override
    public String toString() {
        return roleName; // or you can return "Department ID: " + id + ", Name: " + name;
    }
}
