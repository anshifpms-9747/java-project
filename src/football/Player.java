package football;

import java.io.Serializable;

// ============================================================
// OOP CONCEPT: INHERITANCE
// Player 'is-a' Person. It inherits the base attributes (id, name, age)
// and methods from Person using the 'extends' keyword and invokes the
// parent constructor via 'super(id, name, age)'.
// ============================================================
public class Player extends Person implements Serializable {

    private static final long serialVersionUID = 2L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION
    // Specialized private attributes specific to a football player.
    // --------------------------------------------------------
    private int    jerseyNumber;
    private String position;     // e.g., "Forward", "Midfielder", "Defender", "Goalkeeper"
    private int    goalsScored;
    private String teamId;

    public Player(String id, String name, int age,
                  int jerseyNumber, String position, String teamId) {
        super(id, name, age);   // Reuse parent constructor
        this.jerseyNumber = jerseyNumber;
        this.position     = position;
        this.goalsScored  = 0;
        this.teamId       = teamId;
    }

    // --- Getters (Encapsulation) ---
    public int    getJerseyNumber() { return jerseyNumber; }
    public String getPosition()     { return position;     }
    public int    getGoalsScored()  { return goalsScored;  }
    public String getTeamId()       { return teamId;       }

    // --- Setters (Encapsulation) ---
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }
    public void setPosition(String position)       { this.position     = position;     }
    public void setGoalsScored(int goalsScored)   { this.goalsScored  = goalsScored;  }
    public void setTeamId(String teamId)           { this.teamId       = teamId;       }

    // Business method to increment player's goal tally
    public void addGoals(int goals) {
        if (goals > 0) {
            this.goalsScored += goals;
        }
    }

    // --------------------------------------------------------
    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    // Concrete implementation of Person's abstract displayInfo().
    // Runtime polymorphism ensures this method is called when invoked
    // on a Person reference pointing to a Player object.
    // --------------------------------------------------------
    @Override
    public void displayInfo() {
        System.out.printf("  [Player]  #%-2d %-20s | Age: %-2d | Pos: %-12s | Goals: %d%n",
                jerseyNumber, getName(), getAge(), position, goalsScored);
    }

    @Override
    public String toString() {
        return String.format("#%-2d %-18s (%-11s) Goals: %d",
                jerseyNumber, getName(), position, goalsScored);
    }
}
