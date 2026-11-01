package football;

// ============================================================
// OOP CONCEPT: EXCEPTION HANDLING — Custom Checked Exception
// Thrown when an operation queries a team ID that does not exist.
// Extends Exception to enforce compile-time handling.
// ============================================================
public class TeamNotFoundException extends Exception {

    private static final long serialVersionUID = 102L;

    // Encapsulated field: stores the queried ID that caused the failure
    private final String queriedId;

    public TeamNotFoundException(String teamId) {
        super("No team found with ID: '" + teamId + "'.");
        this.queriedId = teamId;
    }

    public String getQueriedId() {
        return queriedId;
    }
}
