package EmpoWork365;

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

    // Override toString() to return the department name
    @Override
    public String toString() {
        return name; // or you can return "Department ID: " + id + ", Name: " + name;
    }
}
