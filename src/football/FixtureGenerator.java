package football;

import java.io.Serializable;
import java.util.List;

// ============================================================
// OOP CONCEPT: POLYMORPHISM (Interface)
// FixtureGenerator defines an abstract contract for tournament scheduling algorithms.
// Any scheduling strategy (Round-Robin, Knockout, etc.) must implement this interface.
// This enables dynamic runtime polymorphism and follows the "Open/Closed" principle.
// ============================================================
public interface FixtureGenerator extends Serializable {

    /**
     * Generates a scheduled list of Match objects given a list of participating teams.
     *
     * @param teams the participating teams
     * @return list of generated Match objects
     */
    List<Match> generateFixtures(List<Team> teams);

    /**
     * Returns a human-friendly name for this tournament format.
     */
    String getFormatName();
}
