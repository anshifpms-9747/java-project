package football;

// ============================================================
// OOP CONCEPT: EXCEPTION HANDLING — Custom Checked Exception
// Thrown when an invalid match score is entered (e.g. negative score,
// score exceeding realistic limit, or tied score in knockout matches).
// Extends Exception to enforce compile-time handling.
// ============================================================
public class InvalidScoreException extends Exception {

    private static final long serialVersionUID = 101L;

    // Encapsulated field: captures the invalid score value
    private final int offendingScore;

    public InvalidScoreException(int score) {
        super("Invalid score entered: " + score + ". Score must be between 0 and 30.");
        this.offendingScore = score;
    }

    public InvalidScoreException(String customMessage) {
        super(customMessage);
        this.offendingScore = -1;
    }

    public int getOffendingScore() {
        return offendingScore;
    }
}
