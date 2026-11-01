package football;

import java.io.Serializable;

// ============================================================
// OOP CONCEPT: INHERITANCE
// Coach 'is-a' Person. It reuses common personal attributes from Person
// while adding tactical and coaching-specific attributes.
// ============================================================
public class Coach extends Person implements Serializable {

    private static final long serialVersionUID = 3L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION
    // Private attributes protected from direct external modification.
    // --------------------------------------------------------
    private String nationality;
    private String teamId;

    public Coach(String id, String name, int age,
                 String nationality, String teamId) {
        super(id, name, age);
        this.nationality = nationality;
        this.teamId      = teamId;
    }

    // --- Getters (Encapsulation) ---
    public String getNationality() { return nationality; }
    public String getTeamId()      { return teamId;      }

    // --- Setters (Encapsulation) ---
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setTeamId(String teamId)           { this.teamId      = teamId;      }

    // --------------------------------------------------------
    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    // Custom implementation of the displayInfo() contract.
    // --------------------------------------------------------
    @Override
    public void displayInfo() {
        System.out.printf("  [Coach]   %-20s | Age: %-2d | Nationality: %s%n",
                getName(), getAge(), nationality);
    }

    @Override
    public String toString() {
        return String.format("Coach: %-20s (Nationality: %s)", getName(), nationality);
    }
}
