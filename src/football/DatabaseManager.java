package football;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// ============================================================
// OOP CONCEPT: DATABASE ACCESS LAYER (JDBC & ACID Transactions)
// DatabaseManager handles persisting and reconstructing the complete
// Tournament domain model using Relational SQL Databases.
// Supports SQLite (default embedded file) and MySQL via standard JDBC.
// Demonstrates PreparedStatement, Transaction Rollback, and Foreign Keys.
// ============================================================
public class DatabaseManager {

    private static String dbType = "sqlite";
    private static String dbUrl  = "jdbc:sqlite:tournament.db";
    private static String dbUser = "";
    private static String dbPass = "";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        File configFile = new File("db.properties");
        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(in);

                dbType = props.getProperty("db.type", "sqlite").trim().toLowerCase();
                if ("mysql".equals(dbType)) {
                    dbUrl  = props.getProperty("db.url", "jdbc:mysql://localhost:3306/football_tournament");
                    dbUser = props.getProperty("db.user", "root");
                    dbPass = props.getProperty("db.password", "");
                } else {
                    String dbFile = props.getProperty("db.file", "tournament.db").trim();
                    dbUrl = "jdbc:sqlite:" + dbFile;
                }
            } catch (Exception e) {
                System.err.println("  [DB NOTICE] Using default SQLite database configuration: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            if ("mysql".equals(dbType)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(dbUrl, dbUser, dbPass);
            } else {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection(dbUrl);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found for " + dbType + ". Ensure driver JAR is in lib/: " + e.getMessage());
        }
    }

    /**
     * Creates all necessary relational tables if they do not exist.
     */
    public static void initializeTables() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Tournament metadata
            stmt.execute("CREATE TABLE IF NOT EXISTS tournament_meta (" +
                    "tournament_name VARCHAR(100) NOT NULL, " +
                    "season VARCHAR(50) NOT NULL, " +
                    "fixture_format VARCHAR(20) DEFAULT 'RR', " +
                    "PRIMARY KEY (tournament_name, season))");

            // 2. Teams table
            stmt.execute("CREATE TABLE IF NOT EXISTS teams (" +
                    "id VARCHAR(10) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "city VARCHAR(100) NOT NULL, " +
                    "coach_id VARCHAR(20), " +
                    "coach_name VARCHAR(100), " +
                    "coach_age INT, " +
                    "coach_nationality VARCHAR(50), " +
                    "matches_played INT DEFAULT 0, " +
                    "wins INT DEFAULT 0, " +
                    "draws INT DEFAULT 0, " +
                    "losses INT DEFAULT 0, " +
                    "goals_for INT DEFAULT 0, " +
                    "goals_against INT DEFAULT 0, " +
                    "points INT DEFAULT 0)");

            // 3. Players table
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "jersey_number INT NOT NULL, " +
                    "position VARCHAR(30) NOT NULL, " +
                    "goals_scored INT DEFAULT 0, " +
                    "team_id VARCHAR(10) NOT NULL, " +
                    "FOREIGN KEY (team_id) REFERENCES teams(id))");

            // 4. Referees table
            stmt.execute("CREATE TABLE IF NOT EXISTS referees (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "certification VARCHAR(50) NOT NULL, " +
                    "experience_years INT NOT NULL)");

            // 5. Matches table
            stmt.execute("CREATE TABLE IF NOT EXISTS matches (" +
                    "match_id VARCHAR(20) PRIMARY KEY, " +
                    "home_team_id VARCHAR(10) NOT NULL, " +
                    "home_team_name VARCHAR(100) NOT NULL, " +
                    "away_team_id VARCHAR(10) NOT NULL, " +
                    "away_team_name VARCHAR(100) NOT NULL, " +
                    "home_score INT DEFAULT 0, " +
                    "away_score INT DEFAULT 0, " +
                    "status VARCHAR(20) DEFAULT 'SCHEDULED', " +
                    "round_number INT NOT NULL, " +
                    "round_label VARCHAR(50), " +
                    "scheduled_time VARCHAR(50), " +
                    "referee_name VARCHAR(100), " +
                    "next_match_id VARCHAR(20), " +
                    "is_home_for_next INT DEFAULT 1)");
        }
    }

    /**
     * Persists the Tournament object graph to SQL tables inside an ACID transaction.
     */
    public static boolean saveTournament(Tournament tournament) {
        Connection conn = null;
        try {
            initializeTables();
            conn = getConnection();
            // Start Transaction (ACID)
            conn.setAutoCommit(false);

            try (Statement clearStmt = conn.createStatement()) {
                clearStmt.execute("DELETE FROM matches");
                clearStmt.execute("DELETE FROM players");
                clearStmt.execute("DELETE FROM teams");
                clearStmt.execute("DELETE FROM referees");
                clearStmt.execute("DELETE FROM tournament_meta");
            }

            // 1. Insert Tournament Metadata
            String metaSql = "INSERT INTO tournament_meta (tournament_name, season, fixture_format) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(metaSql)) {
                ps.setString(1, tournament.getName());
                ps.setString(2, tournament.getSeason());
                ps.setString(3, tournament.getFormat());
                ps.executeUpdate();
            }

            // 2. Insert Referees
            String refSql = "INSERT INTO referees (id, name, age, certification, experience_years) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(refSql)) {
                for (Referee r : tournament.getReferees()) {
                    ps.setString(1, r.getId());
                    ps.setString(2, r.getName());
                    ps.setInt(3, r.getAge());
                    ps.setString(4, r.getCertification());
                    ps.setInt(5, r.getExperienceYears());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Insert Teams
            String teamSql = "INSERT INTO teams (id, name, city, coach_id, coach_name, coach_age, coach_nationality, " +
                    "matches_played, wins, draws, losses, goals_for, goals_against, points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(teamSql)) {
                for (Team t : tournament.getAllTeams()) {
                    ps.setString(1, t.getId());
                    ps.setString(2, t.getName());
                    ps.setString(3, t.getCity());

                    if (t.getCoach() != null) {
                        ps.setString(4, t.getCoach().getId());
                        ps.setString(5, t.getCoach().getName());
                        ps.setInt(6, t.getCoach().getAge());
                        ps.setString(7, t.getCoach().getNationality());
                    } else {
                        ps.setNull(4, Types.VARCHAR);
                        ps.setNull(5, Types.VARCHAR);
                        ps.setNull(6, Types.INTEGER);
                        ps.setNull(7, Types.VARCHAR);
                    }

                    ps.setInt(8, t.getMatchesPlayed());
                    ps.setInt(9, t.getWins());
                    ps.setInt(10, t.getDraws());
                    ps.setInt(11, t.getLosses());
                    ps.setInt(12, t.getGoalsFor());
                    ps.setInt(13, t.getGoalsAgainst());
                    ps.setInt(14, t.getPoints());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4. Insert Players
            String playerSql = "INSERT INTO players (id, name, age, jersey_number, position, goals_scored, team_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(playerSql)) {
                for (Team t : tournament.getAllTeams()) {
                    for (Player p : t.getPlayers()) {
                        ps.setString(1, p.getId());
                        ps.setString(2, p.getName());
                        ps.setInt(3, p.getAge());
                        ps.setInt(4, p.getJerseyNumber());
                        ps.setString(5, p.getPosition());
                        ps.setInt(6, p.getGoalsScored());
                        ps.setString(7, t.getId());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }

            // 5. Insert Matches
            String matchSql = "INSERT INTO matches (match_id, home_team_id, home_team_name, away_team_id, away_team_name, " +
                    "home_score, away_score, status, round_number, round_label, scheduled_time, referee_name, next_match_id, is_home_for_next) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(matchSql)) {
                for (Match m : tournament.getMatches()) {
                    ps.setString(1, m.getMatchId());
                    ps.setString(2, m.getHomeTeamId());
                    ps.setString(3, m.getHomeTeamName());
                    ps.setString(4, m.getAwayTeamId());
                    ps.setString(5, m.getAwayTeamName());
                    ps.setInt(6, m.getHomeScore());
                    ps.setInt(7, m.getAwayScore());
                    ps.setString(8, m.getStatus().name());
                    ps.setInt(9, m.getRound());
                    ps.setString(10, m.getRoundLabel());
                    ps.setString(11, m.getScheduledTime());
                    ps.setString(12, m.getReferee() != null ? m.getReferee().getName() : null);
                    ps.setString(13, m.getNextMatchId());
                    ps.setInt(14, m.isHomeForNext() ? 1 : 0);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Commit Transaction
            conn.commit();
            System.out.println("  [SQL SUCCESS] Tournament saved into database (" + dbType + "): " + dbUrl);
            return true;

        } catch (SQLException e) {
            System.err.println("  [SQL ERROR] Save transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("  [SQL NOTICE] Transaction rolled back safely.");
                } catch (SQLException ex) {
                    System.err.println("  [SQL ERROR] Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Loads and reconstructs the Tournament object graph from SQL database tables.
     */
    public static Tournament loadTournament() {
        try {
            initializeTables();
            try (Connection conn = getConnection()) {

                // 1. Read Tournament Metadata
                String name = "UEFA Champions League";
                String season = "2026-27";
                String format = "RR";

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM tournament_meta LIMIT 1")) {
                    if (rs.next()) {
                        name = rs.getString("tournament_name");
                        season = rs.getString("season");
                        format = rs.getString("fixture_format");
                    } else {
                        System.out.println("  [SQL NOTICE] No tournament data found in database.");
                        return null;
                    }
                }

                Tournament tournament = new Tournament(name, season);

                // 2. Read Referees
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM referees")) {
                    while (rs.next()) {
                        Referee r = new Referee(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getInt("age"),
                                rs.getString("certification"),
                                rs.getInt("experience_years")
                        );
                        tournament.addReferee(r);
                    }
                }

                // 3. Read Teams
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM teams")) {
                    while (rs.next()) {
                        String tid   = rs.getString("id");
                        String tname = rs.getString("name");
                        String city  = rs.getString("city");

                        Team team = new Team(tid, tname, city);

                        String coachName = rs.getString("coach_name");
                        if (coachName != null && !coachName.isEmpty()) {
                            Coach c = new Coach(
                                    rs.getString("coach_id"),
                                    coachName,
                                    rs.getInt("coach_age"),
                                    rs.getString("coach_nationality"),
                                    tid
                            );
                            team.setCoach(c);
                        }

                        // Restore statistics
                        int mp = rs.getInt("matches_played");
                        int w  = rs.getInt("wins");
                        int d  = rs.getInt("draws");
                        int l  = rs.getInt("losses");
                        int gf = rs.getInt("goals_for");
                        int ga = rs.getInt("goals_against");

                        // Simulate stats calculation
                        for (int i = 0; i < w; i++) team.recordResult(1, 0);
                        for (int i = 0; i < d; i++) team.recordResult(0, 0);
                        for (int i = 0; i < l; i++) team.recordResult(0, 1);
                        // Reset and sync exact points
                        team.resetStats();
                        for (int i = 0; i < w; i++) team.recordResult(gf / Math.max(1, mp), ga / Math.max(1, mp));
                        for (int i = 0; i < d; i++) team.recordResult(1, 1);
                        for (int i = 0; i < l; i++) team.recordResult(0, 1);

                        tournament.addTeam(team);
                    }
                }

                // 4. Read Players
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM players")) {
                    while (rs.next()) {
                        String pid     = rs.getString("id");
                        String pname   = rs.getString("name");
                        int age        = rs.getInt("age");
                        int jersey     = rs.getInt("jersey_number");
                        String pos     = rs.getString("position");
                        int goals      = rs.getInt("goals_scored");
                        String teamId  = rs.getString("team_id");

                        Player player = new Player(pid, pname, age, jersey, pos, teamId);
                        player.setGoalsScored(goals);

                        try {
                            Team t = tournament.findTeam(teamId);
                            t.addPlayer(player);
                        } catch (TeamNotFoundException ignored) {}
                    }
                }

                // 5. Read Matches
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM matches ORDER BY round_number ASC, match_id ASC")) {
                    while (rs.next()) {
                        String mid    = rs.getString("match_id");
                        String hid    = rs.getString("home_team_id");
                        String hname  = rs.getString("home_team_name");
                        String aid    = rs.getString("away_team_id");
                        String aname  = rs.getString("away_team_name");
                        int hscore    = rs.getInt("home_score");
                        int ascore    = rs.getInt("away_score");
                        String status = rs.getString("status");
                        int rnum      = rs.getInt("round_number");
                        String rlabel = rs.getString("round_label");
                        String refName= rs.getString("referee_name");
                        String nextMid= rs.getString("next_match_id");
                        boolean isHomeNext = rs.getInt("is_home_for_next") == 1;

                        Match match = new Match(mid, hid, hname, aid, aname, rnum, rlabel);
                        if ("COMPLETED".equalsIgnoreCase(status)) {
                            match.recordResult(hscore, ascore);
                        }
                        if (refName != null && !refName.isEmpty()) {
                            match.setReferee(new Referee("REF_" + mid, refName, 40, "FIFA", 10));
                        }
                        match.setNextMatchId(nextMid);
                        match.setHomeForNext(isHomeNext);

                        tournament.getMatches().add(match);
                    }
                }

                System.out.printf("  [SQL SUCCESS] Loaded Tournament '%s' from database (%s teams, %d matches)%n",
                        tournament.getName(), tournament.getTeamCount(), tournament.getMatches().size());
                return tournament;

            }
        } catch (Exception e) {
            System.err.println("  [SQL ERROR] Loading from database failed: " + e.getMessage());
            return null;
        }
    }

    public static String getDbType() {
        return dbType;
    }

    public static String getDbUrl() {
        return dbUrl;
    }
}
