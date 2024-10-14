package EmpoWork365;

import java.util.Objects;

public class Department {
    private int id;
    private String name;

    // Constructor
    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 
        Department department = (Department) obj; 
        return id == department.id; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); 
    }
}
