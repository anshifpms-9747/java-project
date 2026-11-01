package football;

import java.util.ArrayList;
import java.util.List;

// ============================================================
// OOP CONCEPT: POLYMORPHISM (Interface Implementation)
// KnockoutGenerator implements the FixtureGenerator interface contract.
// Generates a single-elimination tournament bracket where winners
// dynamically advance from initial rounds through to the Final.
// ============================================================
public class KnockoutGenerator implements FixtureGenerator {

    private static final long serialVersionUID = 8L;

    @Override
    public String getFormatName() {
        return "Single-Elimination Knockout";
    }

    // --------------------------------------------------------
    // OOP CONCEPT: POLYMORPHISM (Method Overriding)
    // --------------------------------------------------------
    @Override
    public List<Match> generateFixtures(List<Team> teams) {
        List<Match> allMatches = new ArrayList<>();
        int n = teams.size();

        if (n < 2) {
            System.out.println("  [Knockout] Need at least 2 teams to generate a knockout bracket.");
            return allMatches;
        }

        // Calculate nearest power of 2 >= n
        int slots = 1;
        while (slots < n) {
            slots <<= 1;
        }

        // Pad teams list if slots > n
        List<Team> padded = new ArrayList<>(teams);
        while (padded.size() < slots) {
            padded.add(null);
        }

        int totalRounds = (int) (Math.log(slots) / Math.log(2));
        List<List<Match>> rounds = new ArrayList<>();
        int matchCounter = 1;

        // Step 1: Create all Match placeholders for every round
        int matchesInRound = slots / 2;
        for (int r = 1; r <= totalRounds; r++) {
            List<Match> roundMatches = new ArrayList<>();
            String roundLabel = getRoundLabel(r, totalRounds);

            for (int m = 0; m < matchesInRound; m++) {
                String matchId = String.format("KO-%03d", matchCounter++);
                Match match = new Match(
                        matchId,
                        "TBD", "TBD",
                        "TBD", "TBD",
                        r, roundLabel
                );
                roundMatches.add(match);
            }
            rounds.add(roundMatches);
            matchesInRound /= 2;
        }

        // Step 2: Link matches between consecutive rounds
        for (int r = 0; r < totalRounds - 1; r++) {
            List<Match> currentList = rounds.get(r);
            List<Match> nextList    = rounds.get(r + 1);

            for (int i = 0; i < currentList.size(); i++) {
                Match currentMatch = currentList.get(i);
                Match nextMatch    = nextList.get(i / 2);

                currentMatch.setNextMatchId(nextMatch.getMatchId());
                currentMatch.setHomeForNext(i % 2 == 0);

                // Set placeholder names for next round
                if (i % 2 == 0) {
                    nextMatch.setHomeTeamName("Winner of " + currentMatch.getMatchId());
                } else {
                    nextMatch.setAwayTeamName("Winner of " + currentMatch.getMatchId());
                }
            }
        }

        // Step 3: Populate Round 1 with actual teams and handle byes
        List<Match> round1Matches = rounds.get(0);
        for (int i = 0; i < round1Matches.size(); i++) {
            Match m = round1Matches.get(i);
            Team home = padded.get(2 * i);
            Team away = padded.get(2 * i + 1);

            String homeId   = (home != null) ? home.getId()   : "BYE";
            String homeName = (home != null) ? home.getName() : "BYE";
            String awayId   = (away != null) ? away.getId()   : "BYE";
            String awayName = (away != null) ? away.getName() : "BYE";

            m.setHomeTeamId(homeId);
            m.setHomeTeamName(homeName);
            m.setAwayTeamId(awayId);
            m.setAwayTeamName(awayName);

            // Handle automatic bye advance
            if (away == null && home != null) {
                m.recordResult(1, 0); // Automatic walkover
                if (m.getNextMatchId() != null && rounds.size() > 1) {
                    Match nextM = rounds.get(1).get(i / 2);
                    if (m.isHomeForNext()) {
                        nextM.setHomeTeamId(home.getId());
                        nextM.setHomeTeamName(home.getName());
                    } else {
                        nextM.setAwayTeamId(home.getId());
                        nextM.setAwayTeamName(home.getName());
                    }
                }
            }
        }

        // Flatten all matches into return list
        for (List<Match> rList : rounds) {
            allMatches.addAll(rList);
        }

        System.out.printf("%n  [Knockout] Bracket generated: %d total matches across %d rounds.%n",
                allMatches.size(), totalRounds);
        return allMatches;
    }

    private String getRoundLabel(int currentRound, int totalRounds) {
        int dist = totalRounds - currentRound;
        switch (dist) {
            case 0:  return "Final";
            case 1:  return "Semi-Final";
            case 2:  return "Quarter-Final";
            case 3:  return "Round of 16";
            default: return "Round " + currentRound;
        }
    }
}
