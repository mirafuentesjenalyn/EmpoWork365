package EmpoWork365;

import java.util.Objects;

public class JobTitle {
    private int id;
    private String title;

    // Constructor
    public JobTitle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false; 
        JobTitle jobTitle = (JobTitle) obj; 
        return id == jobTitle.id; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); 
    }
}
