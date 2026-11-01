package football;

// ============================================================
// OOP CONCEPT: EXCEPTION HANDLING — Custom Checked Exception
// Thrown when a team with a duplicate ID or duplicate name is added.
// Extends Exception to enforce compile-time error handling (checked exception).
// ============================================================
public class DuplicateTeamException extends Exception {

    private static final long serialVersionUID = 100L;

    // Encapsulated field: captures the duplicate identifier for error reporting
    private final String duplicateId;

    public DuplicateTeamException(String teamName, String id) {
        super("Team '" + teamName + "' (ID: " + id + ") already exists in this tournament.");
        this.duplicateId = id;
    }

    public String getDuplicateId() {
        return duplicateId;
    }
}
