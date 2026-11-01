package football;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

// ============================================================
// OOP CONCEPT: ENCAPSULATION
// Tournament is the central aggregate coordinator. It encapsulates
// all teams, matches, referees, and scheduling algorithms, exposing
// well-defined business methods.
//
// OOP CONCEPT: COLLECTIONS
// - Map<String, Team> (HashMap) for O(1) key-based team lookup.
// - List<Match> (ArrayList) for ordered fixture representation.
// - List<Referee> (ArrayList) for managing officiating personnel.
//
// OOP CONCEPT: POLYMORPHISM
// - FixtureGenerator interface reference allows dynamic swapping
//   between RoundRobinGenerator and KnockoutGenerator at runtime.
//
// OOP CONCEPT: EXCEPTION HANDLING
// - Throws and propagates DuplicateTeamException, TeamNotFoundException,
//   and InvalidScoreException to protect domain invariants.
// ============================================================
public class Tournament implements Serializable {

    private static final long serialVersionUID = 9L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION — private fields
    // --------------------------------------------------------
    private String name;
    private String season;

    // OOP CONCEPT: COLLECTIONS — HashMap: teamId -> Team
    private Map<String, Team> teamMap;

    // OOP CONCEPT: COLLECTIONS — ArrayList for ordered match fixtures
    private List<Match> matches;

    // OOP CONCEPT: COLLECTIONS — ArrayList for match officials
    private List<Referee> referees;

    // OOP CONCEPT: POLYMORPHISM — Interface type reference
    private FixtureGenerator generator;
    private String format; // "RR" or "KO"

    private int playerIdCounter = 1;

    public Tournament(String name, String season) {
        this.name      = name;
        this.season    = season;
        this.teamMap   = new HashMap<>();
        this.matches   = new ArrayList<>();
        this.referees  = new ArrayList<>();
        this.generator = null;
        this.format    = "None";
    }

    // ========================================================
    // TEAM OPERATIONS (Exception Handling & Collections)
    // ========================================================

    /**
     * Registers a new team.
     * OOP CONCEPT: EXCEPTION HANDLING — throws DuplicateTeamException if ID or Name exists.
     */
    public void addTeam(Team team) throws DuplicateTeamException {
        if (teamMap.containsKey(team.getId().toUpperCase())) {
            throw new DuplicateTeamException(team.getName(), team.getId());
        }

        // Case-insensitive duplicate name check
        for (Team existing : teamMap.values()) {
            if (existing.getName().equalsIgnoreCase(team.getName())) {
                throw new DuplicateTeamException(team.getName(), team.getId());
            }
        }

        teamMap.put(team.getId().toUpperCase(), team);
        System.out.printf("  [SUCCESS] Team '%s' (ID: %s) registered successfully.%n",
                team.getName(), team.getId());
    }

    /**
     * Removes a team by its ID.
     * OOP CONCEPT: EXCEPTION HANDLING — throws TeamNotFoundException.
     */
    public void removeTeam(String teamId) throws TeamNotFoundException {
        String key = teamId.toUpperCase();
        if (!teamMap.containsKey(key)) {
            throw new TeamNotFoundException(teamId);
        }

        Team removed = teamMap.remove(key);
        // Cascade delete: remove any matches involving this team
        matches.removeIf(m -> m.getHomeTeamId().equalsIgnoreCase(key)
                           || m.getAwayTeamId().equalsIgnoreCase(key));

        System.out.printf("  [SUCCESS] Team '%s' (ID: %s) removed from tournament.%n",
                removed.getName(), key);
    }

    /**
     * Look up a team by ID.
     * OOP CONCEPT: EXCEPTION HANDLING — throws TeamNotFoundException.
     */
    public Team findTeam(String teamId) throws TeamNotFoundException {
        if (teamId == null) {
            throw new TeamNotFoundException("null");
        }
        Team t = teamMap.get(teamId.toUpperCase());
        if (t == null) {
            throw new TeamNotFoundException(teamId);
        }
        return t;
    }

    /**
     * Returns an alphabetically sorted list of all registered teams.
     */
    public List<Team> getAllTeams() {
        return teamMap.values().stream()
                .sorted(Comparator.comparing(Team::getName))
                .collect(Collectors.toList());
    }

    public int getTeamCount() {
        return teamMap.size();
    }

    // ========================================================
    // REFEREE OPERATIONS
    // ========================================================

    public void addReferee(Referee referee) {
        referees.add(referee);
    }

    public List<Referee> getReferees() {
        return referees;
    }

    // ========================================================
    // PLAYER OPERATIONS
    // ========================================================

    /**
     * Registers a player to a specific team.
     * OOP CONCEPT: EXCEPTION HANDLING — throws TeamNotFoundException.
     */
    public void addPlayerToTeam(String teamId, String name, int age,
                                int jersey, String position) throws TeamNotFoundException {
        Team team = findTeam(teamId);

        if (team.findPlayerByJersey(jersey) != null) {
            System.out.printf("  [ERROR] Jersey #%d is already assigned to another player in %s!%n",
                    jersey, team.getName());
            return;
        }

        String pid = String.format("P%04d", playerIdCounter++);
        Player player = new Player(pid, name, age, jersey, position, team.getId());
        team.addPlayer(player);

        System.out.printf("  [SUCCESS] Player %s (#%d, %s) added to team %s.%n",
                name, jersey, position, team.getName());
    }

    // ========================================================
    // FIXTURE MANAGEMENT (Polymorphism)
    // ========================================================

    /**
     * Generates tournament fixtures using polymorphic FixtureGenerator.
     *
     * OOP CONCEPT: POLYMORPHISM
     * generator reference points to either RoundRobinGenerator or KnockoutGenerator.
     */
    public void generateFixtures(String chosenFormat) {
        List<Team> teams = getAllTeams();
        if (teams.size() < 2) {
            System.out.println("  [ERROR] At least 2 teams are required to generate tournament fixtures!");
            return;
        }

        // Reset all team statistics before new fixtures
        teams.forEach(Team::resetStats);
        matches.clear();

        // OOP CONCEPT: POLYMORPHISM — instantiate appropriate subclass strategy
        if ("KO".equalsIgnoreCase(chosenFormat) || "KNOCKOUT".equalsIgnoreCase(chosenFormat)) {
            this.generator = new KnockoutGenerator();
            this.format    = "KO";
        } else {
            this.generator = new RoundRobinGenerator();
            this.format    = "RR";
        }

        // OOP CONCEPT: POLYMORPHISM — dynamic method dispatch on interface
        this.matches = generator.generateFixtures(teams);

        // Assign available referees to matches cyclically
        if (!referees.isEmpty() && !matches.isEmpty()) {
            for (int i = 0; i < matches.size(); i++) {
                Referee ref = referees.get(i % referees.size());
                matches.get(i).setReferee(ref);
            }
        }
    }

    public Match findMatch(String matchId) {
        for (Match m : matches) {
            if (m.getMatchId().equalsIgnoreCase(matchId)) {
                return m;
            }
        }
        return null;
    }

    // ========================================================
    // MATCH RESULT ENTRY & STANDINGS UPDATE
    // ========================================================

    /**
     * Enters score for a match, updates points table, and advances knockout winners.
     *
     * OOP CONCEPT: EXCEPTION HANDLING — throws InvalidScoreException and TeamNotFoundException.
     */
    public void enterResult(String matchId, int homeScore, int awayScore)
            throws InvalidScoreException, TeamNotFoundException {

        // Validate score bounds
        if (homeScore < 0 || homeScore > 30) {
            throw new InvalidScoreException(homeScore);
        }
        if (awayScore < 0 || awayScore > 30) {
            throw new InvalidScoreException(awayScore);
        }

        Match match = findMatch(matchId);
        if (match == null) {
            System.out.printf("  [ERROR] Fixture with ID '%s' not found.%n", matchId);
            return;
        }

        if ("TBD".equalsIgnoreCase(match.getHomeTeamId()) || "TBD".equalsIgnoreCase(match.getAwayTeamId())) {
            System.out.println("  [ERROR] Cannot play this fixture yet — previous round winners are still TBD!");
            return;
        }

        // Knockout validation: elimination matches cannot end in a draw
        if ("KO".equalsIgnoreCase(format) && homeScore == awayScore) {
            throw new InvalidScoreException("Knockout matches cannot end in a draw! Enter score including extra-time / penalties.");
        }

        // If match was already completed, reverse previous results from standings
        if (match.isCompleted()) {
            System.out.println("  [NOTICE] Match was previously completed. Recalculating standings...");
            reverseMatchStats(match);
        }

        // Record the verified result
        match.recordResult(homeScore, awayScore);

        // Update participating teams' points and goal differences
        if (!"BYE".equalsIgnoreCase(match.getHomeTeamId())) {
            Team home = findTeam(match.getHomeTeamId());
            home.recordResult(homeScore, awayScore);
        }

        if (!"BYE".equalsIgnoreCase(match.getAwayTeamId())) {
            Team away = findTeam(match.getAwayTeamId());
            away.recordResult(awayScore, homeScore);
        }

        System.out.printf("  [SUCCESS] Result recorded: %s %d - %d %s [%s]%n",
                match.getHomeTeamName(), homeScore, awayScore,
                match.getAwayTeamName(), match.getOutcome());

        // Knockout bracket advancement
        if ("KO".equalsIgnoreCase(format)) {
            advanceKnockoutWinner(match, homeScore, awayScore);
        }
    }

    private void advanceKnockoutWinner(Match match, int homeScore, int awayScore) {
        String winnerId   = (homeScore > awayScore) ? match.getHomeTeamId() : match.getAwayTeamId();
        String winnerName = (homeScore > awayScore) ? match.getHomeTeamName() : match.getAwayTeamName();

        if (match.getNextMatchId() != null) {
            Match next = findMatch(match.getNextMatchId());
            if (next != null) {
                if (match.isHomeForNext()) {
                    next.setHomeTeamId(winnerId);
                    next.setHomeTeamName(winnerName);
                } else {
                    next.setAwayTeamId(winnerId);
                    next.setAwayTeamName(winnerName);
                }
                System.out.printf("  [KNOCKOUT] >>> %s advances to %s (%s)!%n",
                        winnerName, next.getMatchId(), next.getRoundLabel());
            }
        } else if ("Final".equalsIgnoreCase(match.getRoundLabel())) {
            System.out.println("\n  =======================================================");
            System.out.println("  *** TOURNAMENT CHAMPION DECLARED: " + winnerName.toUpperCase() + " ***");
            System.out.println("  =======================================================");
        }
    }

    private void reverseMatchStats(Match m) {
        try {
            Team home = findTeam(m.getHomeTeamId());
            Team away = findTeam(m.getAwayTeamId());
            rebuildTeamStatsExcluding(home, m.getMatchId());
            rebuildTeamStatsExcluding(away, m.getMatchId());
        } catch (TeamNotFoundException ignored) {}
    }

    private void rebuildTeamStatsExcluding(Team team, String excludeMatchId) {
        team.resetStats();
        for (Match m : matches) {
            if (m.getMatchId().equalsIgnoreCase(excludeMatchId) || !m.isCompleted()) {
                continue;
            }
            if (m.getHomeTeamId().equalsIgnoreCase(team.getId())) {
                team.recordResult(m.getHomeScore(), m.getAwayScore());
            } else if (m.getAwayTeamId().equalsIgnoreCase(team.getId())) {
                team.recordResult(m.getAwayScore(), m.getHomeScore());
            }
        }
    }

    // ========================================================
    // STANDINGS & TOP SCORERS (Collections & Streams)
    // ========================================================

    /**
     * Returns standings sorted by:
     * 1. Points DESC (Win=3, Draw=1, Loss=0)
     * 2. Goal Difference DESC
     * 3. Goals For DESC
     *
     * OOP CONCEPT: COLLECTIONS — sorted stream pipeline
     */
    public List<Team> getPointsTable() {
        return teamMap.values().stream()
                .sorted(Comparator
                        .comparingInt(Team::getPoints).reversed()
                        .thenComparingInt(Team::getGoalDifference).reversed()
                        .thenComparingInt(Team::getGoalsFor).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns all players with goals sorted descending.
     * OOP CONCEPT: COLLECTIONS — flat-mapping streams
     */
    public List<Player> getTopScorers() {
        return teamMap.values().stream()
                .flatMap(t -> t.getPlayers().stream())
                .filter(p -> p.getGoalsScored() > 0)
                .sorted(Comparator.comparingInt(Player::getGoalsScored).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Adds goals to a player in a team.
     * OOP CONCEPT: EXCEPTION HANDLING — throws TeamNotFoundException.
     */
    public void addGoalsToPlayer(String teamId, int jersey, int goals) throws TeamNotFoundException {
        Team team = findTeam(teamId);
        Player player = team.findPlayerByJersey(jersey);
        if (player == null) {
            System.out.printf("  [ERROR] No player found with jersey #%d in team %s.%n", jersey, team.getName());
            return;
        }
        player.addGoals(goals);
        System.out.printf("  [SUCCESS] Added %d goal(s) to %s (#%d, %s). Total Goals: %d%n",
                goals, player.getName(), jersey, team.getName(), player.getGoalsScored());
    }

    public int getTotalGoalsScored() {
        return matches.stream()
                .filter(Match::isCompleted)
                .mapToInt(m -> m.getHomeScore() + m.getAwayScore())
                .sum();
    }

    public int getCompletedMatchCount() {
        return (int) matches.stream().filter(Match::isCompleted).count();
    }

    // ========================================================
    // GETTERS
    // ========================================================
    public String           getName()      { return name;      }
    public String           getSeason()    { return season;    }
    public List<Match>      getMatches()   { return matches;   }
    public String           getFormat()    { return format;    }
    public FixtureGenerator getGenerator() { return generator; }
}
