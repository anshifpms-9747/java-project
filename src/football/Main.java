package football;

import java.util.List;
import java.util.Scanner;

// ============================================================
// OOP CONCEPT: CONSOLE USER INTERFACE & DEMONSTRATION HARNESS
// Main drives the menu-driven Football Tournament Management System.
// Handles user interaction, input validation, and exception handling.
// ============================================================
public class Main {

    private static Tournament tournament;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("--cli") || args[0].equalsIgnoreCase("--console"))) {
            runConsoleApp();
        } else {
            // Start Embedded Web Application on http://localhost:8080
            tournament = new Tournament("UEFA Champions League", "2026-27");
            SampleDataLoader.populate(tournament);

            try {
                WebServer webServer = new WebServer(tournament);
                webServer.start();

                // Open default browser to the web app
                try {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "http://localhost:8080"});
                } catch (Exception ex) {
                    System.out.println("  Open http://localhost:8080 in your browser.");
                }

                // Also launch Swing Desktop GUI window
                try {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        new TournamentGUI().setVisible(true);
                    });
                } catch (Throwable ignored) {}

                System.out.println("  [SERVER READY] Web Application active at: http://localhost:8080");

            } catch (Exception e) {
                System.err.println("  [ERROR] Could not start web server: " + e.getMessage());
                // Fallback to pure Swing GUI
                new TournamentGUI().setVisible(true);
            }
        }
    }

    public static void runConsoleApp() {
        // Initialize Tournament domain model
        tournament = new Tournament("UEFA Champions League", "2026-27");

        printHeader();

        // Prompt to load sample demo data right at launch for fast viva demonstration
        System.out.print("  Would you like to load pre-configured sample demo data? (y/n) [y]: ");
        String quickDemo = scanner.nextLine().trim();
        if (quickDemo.isEmpty() || quickDemo.equalsIgnoreCase("y") || quickDemo.equalsIgnoreCase("yes")) {
            SampleDataLoader.populate(tournament);
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntInput("Select an option (0-9): ", 0, 9);
            System.out.println();

            switch (choice) {
                case 1:
                    handleTeamManagement();
                    break;
                case 2:
                    handleAddPlayer();
                    break;
                case 3:
                    handleGenerateFixtures();
                    break;
                case 4:
                    handleEnterMatchResult();
                    break;
                case 5:
                    handleViewPointsTable();
                    break;
                case 6:
                    handleViewTopScorers();
                    break;
                case 7:
                    handleViewTeamDetails();
                    break;
                case 8:
                    handleSaveAndLoadData();
                    break;
                case 9:
                    handleLoadSampleData();
                    break;
                case 0:
                    running = false;
                    System.out.println("  =======================================================");
                    System.out.println("  Thank you for using Football Tournament Management System!");
                    System.out.println("  Good luck with your OOP Lab Mini Project Evaluation!");
                    System.out.println("  =======================================================");
                    break;
                default:
                    System.out.println("  [ERROR] Invalid choice. Please enter a valid number (0-9).");
            }
            if (running) {
                pause();
            }
        }
    }

    // ========================================================
    // MENU DISPLAY METHODS
    // ========================================================

    private static void printHeader() {
        System.out.println("===============================================================================");
        System.out.println("                 FOOTBALL TOURNAMENT MANAGEMENT SYSTEM                         ");
        System.out.println("                     OOP Lab Mini Project (Java 17)                            ");
        System.out.println("===============================================================================");
        System.out.printf("  Tournament : %s | Season: %s%n", tournament.getName(), tournament.getSeason());
        System.out.println("===============================================================================");
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println("                                MAIN MENU                                      ");
        System.out.println("===============================================================================");
        System.out.println("  [1] Add / View / Delete Teams");
        System.out.println("  [2] Add Players to a Team (Name, Age, Jersey #, Position)");
        System.out.println("  [3] Generate & View Fixtures (Round-Robin or Knockout)");
        System.out.println("  [4] Enter Match Results (Score Validation & Scorers)");
        System.out.println("  [5] View Auto-Updating Points Table (Win=3, Draw=1, Loss=0)");
        System.out.println("  [6] View Top Scorers List (Golden Boot Standings)");
        System.out.println("  [7] View Team Details & Squad Roster (Coaching & Squad)");
        System.out.println("  [8] Save and Load Data (Java Object Serialization)");
        System.out.println("  [9] Quick-Load Sample / Demo Data");
        System.out.println("  [0] Exit Application");
        System.out.println("===============================================================================");
    }

    // ========================================================
    // FEATURE 1: ADD / VIEW / DELETE TEAMS
    // ========================================================

    private static void handleTeamManagement() {
        System.out.println("--- [1] TEAM MANAGEMENT ---");
        System.out.println("  1. View All Registered Teams");
        System.out.println("  2. Add New Team");
        System.out.println("  3. Delete a Team");
        System.out.println("  4. Return to Main Menu");

        int subChoice = readIntInput("Select an option (1-4): ", 1, 4);
        System.out.println();

        switch (subChoice) {
            case 1:
                displayAllTeamsTable();
                break;
            case 2:
                addNewTeamWorkflow();
                break;
            case 3:
                deleteTeamWorkflow();
                break;
            case 4:
                return;
        }
    }

    private static void displayAllTeamsTable() {
        List<Team> teams = tournament.getAllTeams();
        if (teams.isEmpty()) {
            System.out.println("  [NOTICE] No teams registered yet. Use Option 1->2 or Option 9 to add teams.");
            return;
        }

        System.out.println("  +--------+------------------------+----------------------+--------------------+--------+");
        System.out.println("  | ID     | Team Name              | City                 | Head Coach         | Squad  |");
        System.out.println("  +--------+------------------------+----------------------+--------------------+--------+");
        for (Team t : teams) {
            String coachName = (t.getCoach() != null) ? t.getCoach().getName() : "[None]";
            System.out.printf("  | %-6s | %-22s | %-20s | %-18s | %-6d |%n",
                    t.getId(), t.getName(), t.getCity(), coachName, t.getPlayers().size());
        }
        System.out.println("  +--------+------------------------+----------------------+--------------------+--------+");
        System.out.printf("  Total Teams: %d%n", teams.size());
    }

    private static void addNewTeamWorkflow() {
        System.out.println("  >> Register New Team");
        String id   = readStringInput("  Enter Team Short ID (e.g. RMA, MCI): ").toUpperCase();
        String name = readStringInput("  Enter Team Name (e.g. Real Madrid): ");
        String city = readStringInput("  Enter Home City (e.g. Madrid): ");

        Team newTeam = new Team(id, name, city);

        System.out.print("  Would you like to assign a Head Coach now? (y/n) [n]: ");
        String assignCoach = scanner.nextLine().trim();
        if (assignCoach.equalsIgnoreCase("y") || assignCoach.equalsIgnoreCase("yes")) {
            String coachName = readStringInput("    Coach Name: ");
            int coachAge     = readIntInput("    Coach Age: ", 25, 90);
            String coachNat  = readStringInput("    Coach Nationality: ");
            Coach coach = new Coach("C_" + id, coachName, coachAge, coachNat, id);
            newTeam.setCoach(coach);
        }

        // OOP CONCEPT: EXCEPTION HANDLING
        // Catches DuplicateTeamException thrown by tournament.addTeam()
        try {
            tournament.addTeam(newTeam);
        } catch (DuplicateTeamException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT] %s%n", e.getMessage());
            System.err.printf("  Duplicate Key: %s%n", e.getDuplicateId());
        }
    }

    private static void deleteTeamWorkflow() {
        displayAllTeamsTable();
        if (tournament.getTeamCount() == 0) return;

        String id = readStringInput("  Enter Team ID to delete: ");

        // OOP CONCEPT: EXCEPTION HANDLING
        // Catches TeamNotFoundException thrown by tournament.removeTeam()
        try {
            tournament.removeTeam(id);
        } catch (TeamNotFoundException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT] %s%n", e.getMessage());
            System.err.printf("  Queried Team ID '%s' does not exist!%n", e.getQueriedId());
        }
    }

    // ========================================================
    // FEATURE 2: ADD PLAYERS TO A TEAM
    // ========================================================

    private static void handleAddPlayer() {
        System.out.println("--- [2] ADD PLAYER TO TEAM ---");
        displayAllTeamsTable();
        if (tournament.getTeamCount() == 0) return;

        String teamId = readStringInput("  Enter Team ID for the player: ").toUpperCase();

        try {
            // Verify team exists before continuing
            Team team = tournament.findTeam(teamId);
            System.out.printf("  Adding player to: %s (%s)%n", team.getName(), team.getId());

            String name = readStringInput("  Player Name: ");
            int age     = readIntInput("  Age (15 - 45): ", 15, 45);
            int jersey  = readIntInput("  Jersey Number (1 - 99): ", 1, 99);

            System.out.println("  Positions: [1] Goalkeeper | [2] Defender | [3] Midfielder | [4] Forward");
            int posChoice = readIntInput("  Select Position (1-4): ", 1, 4);
            String position;
            switch (posChoice) {
                case 1: position = "Goalkeeper"; break;
                case 2: position = "Defender";   break;
                case 3: position = "Midfielder"; break;
                default: position = "Forward";   break;
            }

            // OOP CONCEPT: EXCEPTION HANDLING
            tournament.addPlayerToTeam(teamId, name, age, jersey, position);

        } catch (TeamNotFoundException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    // ========================================================
    // FEATURE 3: GENERATE FIXTURES (Round-Robin or Knockout)
    // ========================================================

    private static void handleGenerateFixtures() {
        System.out.println("--- [3] FIXTURE MANAGEMENT ---");
        System.out.println("  1. Generate Round-Robin Fixtures (Every team plays every team)");
        System.out.println("  2. Generate Single-Elimination Knockout Fixtures");
        System.out.println("  3. View Existing Fixtures & Schedules");
        System.out.println("  4. Return to Main Menu");

        int choice = readIntInput("Select an option (1-4): ", 1, 4);
        System.out.println();

        if (choice == 1) {
            tournament.generateFixtures("RR");
            displayFixturesList();
        } else if (choice == 2) {
            tournament.generateFixtures("KO");
            displayFixturesList();
        } else if (choice == 3) {
            displayFixturesList();
        }
    }

    private static void displayFixturesList() {
        List<Match> matches = tournament.getMatches();
        if (matches.isEmpty()) {
            System.out.println("  [NOTICE] No fixtures generated yet. Choose Option 3->1 or 3->2 first.");
            return;
        }

        System.out.printf("%n  ==== FIXTURES & SCHEDULES (Format: %s) ====%n", tournament.getFormat());
        System.out.println("  -----------------------------------------------------------------------------------------");
        for (Match m : matches) {
            m.display();
        }
        System.out.println("  -----------------------------------------------------------------------------------------");
        System.out.printf("  Total Matches: %d%n", matches.size());
    }

    // ========================================================
    // FEATURE 4: ENTER MATCH RESULTS
    // ========================================================

    private static void handleEnterMatchResult() {
        System.out.println("--- [4] ENTER MATCH RESULTS ---");
        displayFixturesList();
        if (tournament.getMatches().isEmpty()) return;

        String matchId = readStringInput("  Enter Match ID to record result (e.g. RR-001 or KO-001): ").toUpperCase();

        Match match = tournament.findMatch(matchId);
        if (match == null) {
            System.out.printf("  [ERROR] Match ID '%s' was not found!%n", matchId);
            return;
        }

        System.out.printf("%n  Recording Result for: %s vs %s%n",
                match.getHomeTeamName(), match.getAwayTeamName());

        int homeScore = readIntInput("  Enter goals scored by " + match.getHomeTeamName() + ": ", 0, 30);
        int awayScore = readIntInput("  Enter goals scored by " + match.getAwayTeamName() + ": ", 0, 30);

        // OOP CONCEPT: EXCEPTION HANDLING
        // Catches InvalidScoreException and TeamNotFoundException
        try {
            tournament.enterResult(matchId, homeScore, awayScore);

            // Optional: Attribute goals to individual players for Top Scorers leaderboard
            if (homeScore > 0 && !"BYE".equalsIgnoreCase(match.getHomeTeamId())) {
                attributeGoalsWorkflow(match.getHomeTeamId(), match.getHomeTeamName(), homeScore);
            }
            if (awayScore > 0 && !"BYE".equalsIgnoreCase(match.getAwayTeamId())) {
                attributeGoalsWorkflow(match.getAwayTeamId(), match.getAwayTeamName(), awayScore);
            }

        } catch (InvalidScoreException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT - InvalidScoreException] %s%n", e.getMessage());
            if (e.getOffendingScore() >= 0) {
                System.err.printf("  Offending Score Value: %d%n", e.getOffendingScore());
            }
        } catch (TeamNotFoundException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT - TeamNotFoundException] %s%n", e.getMessage());
        }
    }

    private static void attributeGoalsWorkflow(String teamId, String teamName, int totalGoals) {
        System.out.printf("%n  Assign individual goal scorers for %s (%d goal(s) scored)? (y/n) [n]: ",
                teamName, totalGoals);
        String ans = scanner.nextLine().trim();
        if (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("yes")) return;

        try {
            Team team = tournament.findTeam(teamId);
            if (team.getPlayers().isEmpty()) {
                System.out.println("  (No players registered in team to attribute goals)");
                return;
            }

            System.out.println("  Team Players:");
            for (Player p : team.getPlayers()) {
                System.out.printf("    #%-2d %-20s (Current Goals: %d)%n",
                        p.getJerseyNumber(), p.getName(), p.getGoalsScored());
            }

            int goalsLeft = totalGoals;
            while (goalsLeft > 0) {
                int jersey = readIntInput("  Enter Jersey # of scorer (or 0 to stop): ", 0, 99);
                if (jersey == 0) break;

                int count = readIntInput("  Goals scored by this player (1 - " + goalsLeft + "): ", 1, goalsLeft);
                tournament.addGoalsToPlayer(teamId, jersey, count);
                goalsLeft -= count;
            }
        } catch (TeamNotFoundException ignored) {}
    }

    // ========================================================
    // FEATURE 5: AUTO-UPDATING POINTS TABLE
    // ========================================================

    private static void handleViewPointsTable() {
        System.out.println("--- [5] POINTS TABLE / STANDINGS ---");
        List<Team> table = tournament.getPointsTable();

        if (table.isEmpty()) {
            System.out.println("  [NOTICE] No teams available in the points table.");
            return;
        }

        System.out.println("  Scoring Rules: Win = 3 Pts | Draw = 1 Pt | Loss = 0 Pts");
        System.out.println("  Sorting Criteria: 1st Points -> 2nd Goal Difference (GD) -> 3rd Goals For (GF)");
        System.out.println("  +-----+--------+------------------------+----+----+----+----+----+----+-----+-----+");
        System.out.println("  | Pos | ID     | Team Name              | MP | W  | D  | L  | GF | GA | GD  | Pts |");
        System.out.println("  +-----+--------+------------------------+----+----+----+----+----+----+-----+-----+");

        int pos = 1;
        for (Team t : table) {
            System.out.printf("  | %-3d | %-6s | %-22s | %-2d | %-2d | %-2d | %-2d | %-2d | %-2d | %+3d | %-3d |%n",
                    pos++, t.getId(), t.getName(),
                    t.getMatchesPlayed(), t.getWins(), t.getDraws(), t.getLosses(),
                    t.getGoalsFor(), t.getGoalsAgainst(), t.getGoalDifference(), t.getPoints());
        }
        System.out.println("  +-----+--------+------------------------+----+----+----+----+----+----+-----+-----+");
        System.out.println("  Key: MP=Matches Played, W=Wins, D=Draws, L=Losses, GF=Goals For, GA=Goals Against, GD=Goal Diff");
    }

    // ========================================================
    // FEATURE 6: TOP SCORERS LIST
    // ========================================================

    private static void handleViewTopScorers() {
        System.out.println("--- [6] TOP SCORERS (GOLDEN BOOT RACE) ---");
        List<Player> topScorers = tournament.getTopScorers();

        if (topScorers.isEmpty()) {
            System.out.println("  [NOTICE] No goals scored yet in this tournament.");
            return;
        }

        System.out.println("  +------+------------------------+--------+--------------------+--------+-------+");
        System.out.println("  | Rank | Player Name            | Jersey | Position           | Team   | Goals |");
        System.out.println("  +------+------------------------+--------+--------------------+--------+-------+");

        int rank = 1;
        for (Player p : topScorers) {
            System.out.printf("  | %-4d | %-22s | #%-5d | %-18s | %-6s | %-5d |%n",
                    rank++, p.getName(), p.getJerseyNumber(), p.getPosition(), p.getTeamId(), p.getGoalsScored());
        }
        System.out.println("  +------+------------------------+--------+--------------------+--------+-------+");
        System.out.printf("  Golden Boot Leader: %s (%d Goals)%n",
                topScorers.get(0).getName(), topScorers.get(0).getGoalsScored());
    }

    // ========================================================
    // FEATURE 7: VIEW TEAM DETAILS & SQUAD ROSTER
    // ========================================================

    private static void handleViewTeamDetails() {
        System.out.println("--- [7] VIEW TEAM DETAILS & SQUAD ---");
        displayAllTeamsTable();
        if (tournament.getTeamCount() == 0) return;

        String teamId = readStringInput("  Enter Team ID to view complete details: ").toUpperCase();

        try {
            Team team = tournament.findTeam(teamId);
            System.out.println();
            team.displayFullDetails();
        } catch (TeamNotFoundException e) {
            System.err.printf("%n  [EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    // ========================================================
    // FEATURE 8: SAVE & LOAD DATA (Serialization)
    // ========================================================

    private static void handleSaveAndLoadData() {
        System.out.println("--- [8] SAVE & LOAD DATA (JAVA SERIALIZATION) ---");
        System.out.println("  1. Save Tournament Data to Disk");
        System.out.println("  2. Load Tournament Data from Disk");
        System.out.println("  3. Return to Main Menu");

        int choice = readIntInput("Select an option (1-3): ", 1, 3);
        System.out.println();

        if (choice == 1) {
            String filename = readStringInput("  Enter filename to save [default: tournament_data.dat]: ");
            if (filename.isEmpty()) filename = "tournament_data.dat";
            FileManager.save(tournament, filename);
        } else if (choice == 2) {
            String filename = readStringInput("  Enter filename to load [default: tournament_data.dat]: ");
            if (filename.isEmpty()) filename = "tournament_data.dat";
            Tournament loaded = FileManager.load(filename);
            if (loaded != null) {
                tournament = loaded;
                System.out.printf("  [SUCCESS] Tournament '%s' active! Registered Teams: %d, Matches: %d%n",
                        tournament.getName(), tournament.getTeamCount(), tournament.getMatches().size());
            }
        }
    }

    // ========================================================
    // FEATURE 9: QUICK-LOAD SAMPLE / DEMO DATA
    // ========================================================

    private static void handleLoadSampleData() {
        System.out.println("--- [9] LOAD SAMPLE / DEMO DATA ---");
        System.out.print("  This will populate 4 teams, coaches, squads, and match officials. Proceed? (y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
            SampleDataLoader.populate(tournament);
        } else {
            System.out.println("  Operation canceled.");
        }
    }

    // ========================================================
    // INPUT VALIDATION UTILITY HELPERS
    // ========================================================

    private static int readIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  [INPUT ERROR] Please enter an integer between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [INPUT ERROR] Invalid numeric format. Please enter a valid whole number.");
            }
        }
    }

    private static String readStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  [INPUT ERROR] Input cannot be blank. Please try again.");
        }
    }

    private static void pause() {
        System.out.print("\n  Press [ENTER] to return to the menu...");
        scanner.nextLine();
    }
}

