package EmpoWork365;

import java.util.Objects;

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

    @Override
    public String toString() {
        return roleName; // Display the role name in JComboBox
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return roleId == role.roleId; // Compare by role ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId); // Generate hash code based on role ID
    }
}
