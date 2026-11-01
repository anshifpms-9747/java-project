package football;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ============================================================
// OOP CONCEPT: POLYMORPHISM (Interface Implementation)
// RoundRobinGenerator implements the FixtureGenerator interface contract.
// Uses the standard round-robin polygon/circle rotation algorithm
// so that every team plays against every other team exactly once,
// organized into structured rounds.
// ============================================================
public class RoundRobinGenerator implements FixtureGenerator {

    private static final long serialVersionUID = 7L;

    @Override
    public String getFormatName() {
        return "Round-Robin Tournament";
    }

    // --------------------------------------------------------
    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    // --------------------------------------------------------
    @Override
    public List<Match> generateFixtures(List<Team> teams) {
        List<Match> matches = new ArrayList<>();
        int n = teams.size();

        if (n < 2) {
            System.out.println("  [RoundRobin] Need at least 2 teams to schedule fixtures.");
            return matches;
        }

        // Create a working list of teams; if odd, add a dummy null (BYE)
        List<Team> teamList = new ArrayList<>(teams);
        boolean hasBye = (n % 2 != 0);
        if (hasBye) {
            teamList.add(null);
        }

        int totalTeams = teamList.size();
        int totalRounds = totalTeams - 1;
        int matchesPerRound = totalTeams / 2;
        int matchCount = 1;

        for (int r = 0; r < totalRounds; r++) {
            int roundNumber = r + 1;
            String roundLabel = "Round " + roundNumber;

            for (int m = 0; m < matchesPerRound; m++) {
                Team home = teamList.get(m);
                Team away = teamList.get(totalTeams - 1 - m);

                // Skip bye pairings
                if (home == null || away == null) {
                    continue;
                }

                // Alternate home and away to balance home advantage
                if (m == 0 && (r % 2 == 1)) {
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                String matchId = String.format("RR-%03d", matchCount++);
                Match fixture = new Match(
                        matchId,
                        home.getId(), home.getName(),
                        away.getId(), away.getName(),
                        roundNumber, roundLabel
                );
                matches.add(fixture);
            }

            // Circle rotation: keep index 0 fixed, rotate the rest clockwise
            Team last = teamList.remove(totalTeams - 1);
            teamList.add(1, last);
        }

        System.out.printf("%n  [Round-Robin] Generated %d fixtures across %d rounds for %d teams.%n",
                matches.size(), totalRounds, n);
        return matches;
    }
}
