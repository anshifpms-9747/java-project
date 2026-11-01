package football;

import java.io.Serializable;

// ============================================================
// OOP CONCEPT: INHERITANCE
// Referee 'is-a' Person. It extends Person to share foundational
// identity details while representing independent match officials.
// ============================================================
public class Referee extends Person implements Serializable {

    private static final long serialVersionUID = 4L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION
    // Private attributes defining referee credentials.
    // --------------------------------------------------------
    private String certification;   // e.g., "FIFA", "UEFA", "AFC"
    private int    experienceYears;

    public Referee(String id, String name, int age,
                   String certification, int experienceYears) {
        super(id, name, age);
        this.certification   = certification;
        this.experienceYears = experienceYears;
    }

    // --- Getters (Encapsulation) ---
    public String getCertification()   { return certification;   }
    public int    getExperienceYears() { return experienceYears; }

    // --- Setters (Encapsulation) ---
    public void setCertification(String certification)   { this.certification   = certification;   }
    public void setExperienceYears(int experienceYears)  { this.experienceYears = experienceYears; }

    // --------------------------------------------------------
    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    // Custom implementation of the displayInfo() contract.
    // --------------------------------------------------------
    @Override
    public void displayInfo() {
        System.out.printf("  [Referee] %-20s | Age: %-2d | Cert: %-6s | Experience: %d yrs%n",
                getName(), getAge(), certification, experienceYears);
    }

    @Override
    public String toString() {
        return String.format("Referee: %-20s (%s, %d yrs exp)",
                getName(), certification, experienceYears);
    }
}
