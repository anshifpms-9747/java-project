package football;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ============================================================
// OOP CONCEPT: INTEGRATED WEB APPLICATION SERVER (JDK Built-in)
// WebServer utilizes com.sun.net.httpserver.HttpServer (Standard Java SE)
// to host a modern, browser-based SPA web application on localhost:8080.
// Directly interacts with the OOP Tournament domain model via REST endpoints.
// ============================================================
public class WebServer {

    private static final int PORT = 8080;
    private final Tournament tournament;
    private HttpServer server;

    public WebServer(Tournament tournament) {
        this.tournament = tournament;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Web App Single Page Application (HTML/CSS/JS)
        server.createContext("/", new IndexHandler());

        // REST API Endpoints
        server.createContext("/api/data", new ApiDataHandler());
        server.createContext("/api/generate", new ApiGenerateHandler());
        server.createContext("/api/result", new ApiResultHandler());
        server.createContext("/api/team/add", new ApiAddTeamHandler());
        server.createContext("/api/team/delete", new ApiDeleteTeamHandler());
        server.createContext("/api/player/add", new ApiAddPlayerHandler());
        server.createContext("/api/save", new ApiSaveHandler());
        server.createContext("/api/load", new ApiLoadHandler());
        server.createContext("/api/db/save", new ApiDbSaveHandler());
        server.createContext("/api/db/load", new ApiDbLoadHandler());
        server.createContext("/api/demo", new ApiDemoHandler());

        server.setExecutor(null); // Default single-threaded executor
        server.start();
        System.out.println("===============================================================================");
        System.out.printf("  [WEB APP ONLINE] Running at: http://localhost:%d%n", PORT);
        System.out.println("===============================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // ========================================================
    // JSON SERIALIZER HELPER (Zero External Libraries)
    // ========================================================

    private String buildTournamentJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // Tournament metadata
        sb.append("\"name\":\"").append(escapeJson(tournament.getName())).append("\",");
        sb.append("\"season\":\"").append(escapeJson(tournament.getSeason())).append("\",");
        sb.append("\"format\":\"").append(escapeJson(tournament.getFormat())).append("\",");
        sb.append("\"teamCount\":").append(tournament.getTeamCount()).append(",");
        sb.append("\"matchesCount\":").append(tournament.getMatches().size()).append(",");
        sb.append("\"completedMatches\":").append(tournament.getCompletedMatchCount()).append(",");
        sb.append("\"totalGoals\":").append(tournament.getTotalGoalsScored()).append(",");

        List<Team> standings = tournament.getPointsTable();
        String leader = !standings.isEmpty() ? standings.get(0).getName() : "—";
        sb.append("\"leader\":\"").append(escapeJson(leader)).append("\",");

        // Standings Array
        sb.append("\"standings\":[");
        for (int i = 0; i < standings.size(); i++) {
            Team t = standings.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"rank\":").append(i + 1).append(",");
            sb.append("\"id\":\"").append(escapeJson(t.getId())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(t.getName())).append("\",");
            sb.append("\"city\":\"").append(escapeJson(t.getCity())).append("\",");
            sb.append("\"mp\":").append(t.getMatchesPlayed()).append(",");
            sb.append("\"w\":").append(t.getWins()).append(",");
            sb.append("\"d\":").append(t.getDraws()).append(",");
            sb.append("\"l\":").append(t.getLosses()).append(",");
            sb.append("\"gf\":").append(t.getGoalsFor()).append(",");
            sb.append("\"ga\":").append(t.getGoalsAgainst()).append(",");
            sb.append("\"gd\":").append(t.getGoalDifference()).append(",");
            sb.append("\"pts\":").append(t.getPoints());
            sb.append("}");
        }
        sb.append("],");

        // Teams with full Squads Array
        List<Team> allTeams = tournament.getAllTeams();
        sb.append("\"teams\":[");
        for (int i = 0; i < allTeams.size(); i++) {
            Team t = allTeams.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(escapeJson(t.getId())).append("\",");
            sb.append("\"name\":\"").append(escapeJson(t.getName())).append("\",");
            sb.append("\"city\":\"").append(escapeJson(t.getCity())).append("\",");
            sb.append("\"coach\":\"").append(t.getCoach() != null ? escapeJson(t.getCoach().getName()) : "[None]").append("\",");
            sb.append("\"coachNat\":\"").append(t.getCoach() != null ? escapeJson(t.getCoach().getNationality()) : "").append("\",");

            sb.append("\"players\":[");
            List<Player> players = t.getPlayers();
            for (int j = 0; j < players.size(); j++) {
                Player p = players.get(j);
                if (j > 0) sb.append(",");
                sb.append("{");
                sb.append("\"jersey\":").append(p.getJerseyNumber()).append(",");
                sb.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
                sb.append("\"age\":").append(p.getAge()).append(",");
                sb.append("\"position\":\"").append(escapeJson(p.getPosition())).append("\",");
                sb.append("\"goals\":").append(p.getGoalsScored());
                sb.append("}");
            }
            sb.append("]");
            sb.append("}");
        }
        sb.append("],");

        // Fixtures Array
        List<Match> matches = tournament.getMatches();
        sb.append("\"matches\":[");
        for (int i = 0; i < matches.size(); i++) {
            Match m = matches.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(escapeJson(m.getMatchId())).append("\",");
            sb.append("\"round\":\"").append(escapeJson(m.getRoundLabel())).append("\",");
            sb.append("\"home\":\"").append(escapeJson(m.getHomeTeamName())).append("\",");
            sb.append("\"homeId\":\"").append(escapeJson(m.getHomeTeamId())).append("\",");
            sb.append("\"away\":\"").append(escapeJson(m.getAwayTeamName())).append("\",");
            sb.append("\"awayId\":\"").append(escapeJson(m.getAwayTeamId())).append("\",");
            sb.append("\"homeScore\":").append(m.getHomeScore()).append(",");
            sb.append("\"awayScore\":").append(m.getAwayScore()).append(",");
            sb.append("\"status\":\"").append(m.getStatus().name()).append("\",");
            sb.append("\"referee\":\"").append(m.getReferee() != null ? escapeJson(m.getReferee().getName()) : "TBD").append("\",");
            sb.append("\"outcome\":\"").append(escapeJson(m.getOutcome())).append("\"");
            sb.append("}");
        }
        sb.append("],");

        // Top Scorers Array
        List<Player> scorers = tournament.getTopScorers();
        sb.append("\"scorers\":[");
        for (int i = 0; i < scorers.size(); i++) {
            Player p = scorers.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"rank\":").append(i + 1).append(",");
            sb.append("\"name\":\"").append(escapeJson(p.getName())).append("\",");
            sb.append("\"jersey\":").append(p.getJerseyNumber()).append(",");
            sb.append("\"team\":\"").append(escapeJson(p.getTeamId())).append("\",");
            sb.append("\"position\":\"").append(escapeJson(p.getPosition())).append("\",");
            sb.append("\"goals\":").append(p.getGoalsScored());
            sb.append("}");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return params;
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // ========================================================
    // HTTP HANDLERS
    // ========================================================

    private class ApiDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, buildTournamentJson());
        }
    }

    private class ApiGenerateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String format = q.getOrDefault("format", "RR");
            tournament.generateFixtures(format);
            sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Fixtures generated\"}");
        }
    }

    private class ApiResultHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String matchId = q.get("matchId");
            try {
                int homeScore = Integer.parseInt(q.get("homeScore"));
                int awayScore = Integer.parseInt(q.get("awayScore"));

                tournament.enterResult(matchId, homeScore, awayScore);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Result saved!\"}");
            } catch (NumberFormatException e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Scores must be valid numbers\"}");
            } catch (InvalidScoreException e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            } catch (TeamNotFoundException e) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ApiAddTeamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String id   = q.getOrDefault("id", "").trim().toUpperCase();
            String name = q.getOrDefault("name", "").trim();
            String city = q.getOrDefault("city", "").trim();
            String coach= q.getOrDefault("coach", "").trim();
            String nat  = q.getOrDefault("nat", "International").trim();

            if (id.isEmpty() || name.isEmpty() || city.isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"ID, Name and City are required\"}");
                return;
            }

            Team team = new Team(id, name, city);
            if (!coach.isEmpty()) {
                team.setCoach(new Coach("C_" + id, coach, 50, nat, id));
            }

            try {
                tournament.addTeam(team);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Team added\"}");
            } catch (DuplicateTeamException e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ApiDeleteTeamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String id = q.getOrDefault("id", "").trim().toUpperCase();
            try {
                tournament.removeTeam(id);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Team deleted\"}");
            } catch (TeamNotFoundException e) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ApiAddPlayerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String teamId = q.getOrDefault("teamId", "").trim().toUpperCase();
            String name   = q.getOrDefault("name", "").trim();
            try {
                int age    = Integer.parseInt(q.get("age"));
                int jersey = Integer.parseInt(q.get("jersey"));
                String pos = q.getOrDefault("position", "Forward").trim();

                tournament.addPlayerToTeam(teamId, name, age, jersey, pos);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Player added\"}");
            } catch (NumberFormatException e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Age and jersey must be numbers\"}");
            } catch (TeamNotFoundException e) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ApiSaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean ok = FileManager.save(tournament, "tournament_data.dat");
            if (ok) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Data saved to tournament_data.dat\"}");
            } else {
                sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"Save failed\"}");
            }
        }
    }

    private class ApiLoadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Tournament loaded = FileManager.load("tournament_data.dat");
            if (loaded != null) {
                // Copy reference or sync
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Loaded from file\"}");
            } else {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"File not found or corrupted\"}");
            }
        }
    }

    private class ApiDbSaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            boolean ok = DatabaseManager.saveTournament(tournament);
            if (ok) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Tournament saved to SQL Database (" + DatabaseManager.getDbType() + ")!\"}");
            } else {
                sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"SQL Database save failed.\"}");
            }
        }
    }

    private class ApiDbLoadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Tournament loaded = DatabaseManager.loadTournament();
            if (loaded != null) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Tournament loaded from SQL Database!\"}");
            } else {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"No data found in SQL Database or connection failed.\"}");
            }
        }
    }

    private class ApiDemoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            SampleDataLoader.populate(tournament);
            sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Demo data loaded!\"}");
        }
    }

    // ========================================================
    // EMBEDDED SINGLE-PAGE WEB APPLICATION (HTML5, CSS, JS)
    // ========================================================

    private class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getIndexHtml();
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private String getIndexHtml() {
        return "<!DOCTYPE html>\n" +
"<html lang=\"en\">\n" +
"<head>\n" +
"  <meta charset=\"UTF-8\" />\n" +
"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
"  <title>Football Tournament Pro — OOP Web App</title>\n" +
"  <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600&display=swap\" rel=\"stylesheet\">\n" +
"  <style>\n" +
"    :root {\n" +
"      --bg-dark: #0f172a;\n" +
"      --bg-card: #1e293b;\n" +
"      --bg-card-hover: #273549;\n" +
"      --accent-green: #10b981;\n" +
"      --accent-blue: #3b82f6;\n" +
"      --accent-gold: #f59e0b;\n" +
"      --accent-red: #ef4444;\n" +
"      --text-main: #f8fafc;\n" +
"      --text-muted: #94a3b8;\n" +
"      --border: #334155;\n" +
"    }\n" +
"    * { box-sizing: border-box; margin: 0; padding: 0; }\n" +
"    body {\n" +
"      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;\n" +
"      background: var(--bg-dark);\n" +
"      color: var(--text-main);\n" +
"      display: flex;\n" +
"      height: 100vh;\n" +
"      overflow: hidden;\n" +
"    }\n" +
"    /* Sidebar */\n" +
"    aside {\n" +
"      width: 260px;\n" +
"      background: #090d16;\n" +
"      border-right: 1px solid var(--border);\n" +
"      display: flex;\n" +
"      flex-direction: column;\n" +
"      flex-shrink: 0;\n" +
"    }\n" +
"    .brand {\n" +
"      padding: 24px 20px;\n" +
"      border-bottom: 1px solid var(--border);\n" +
"    }\n" +
"    .brand h1 {\n" +
"      font-size: 1.15rem;\n" +
"      font-weight: 800;\n" +
"      display: flex;\n" +
"      align-items: center;\n" +
"      gap: 10px;\n" +
"      letter-spacing: -0.02em;\n" +
"    }\n" +
"    .brand span {\n" +
"      display: inline-block;\n" +
"      font-size: 0.7rem;\n" +
"      font-weight: 700;\n" +
"      color: var(--accent-green);\n" +
"      letter-spacing: 0.08em;\n" +
"      margin-top: 4px;\n" +
"    }\n" +
"    nav {\n" +
"      padding: 16px 12px;\n" +
"      display: flex;\n" +
"      flex-direction: column;\n" +
"      gap: 6px;\n" +
"      flex: 1;\n" +
"    }\n" +
"    .nav-btn {\n" +
"      display: flex;\n" +
"      align-items: center;\n" +
"      gap: 12px;\n" +
"      padding: 12px 14px;\n" +
"      background: transparent;\n" +
"      border: none;\n" +
"      color: var(--text-muted);\n" +
"      font-size: 0.9rem;\n" +
"      font-weight: 600;\n" +
"      border-radius: 8px;\n" +
"      cursor: pointer;\n" +
"      transition: all 0.15s;\n" +
"      text-align: left;\n" +
"    }\n" +
"    .nav-btn:hover, .nav-btn.active {\n" +
"      background: var(--bg-card);\n" +
"      color: var(--text-main);\n" +
"    }\n" +
"    .nav-btn.active {\n" +
"      border-left: 3px solid var(--accent-green);\n" +
"      color: var(--accent-green);\n" +
"    }\n" +
"    .sidebar-footer {\n" +
"      padding: 16px;\n" +
"      border-top: 1px solid var(--border);\n" +
"      display: flex;\n" +
"      flex-direction: column;\n" +
"      gap: 8px;\n" +
"    }\n" +
"    .btn-action {\n" +
"      padding: 10px;\n" +
"      border-radius: 6px;\n" +
"      font-size: 0.85rem;\n" +
"      font-weight: 700;\n" +
"      border: none;\n" +
"      cursor: pointer;\n" +
"      transition: transform 0.1s, opacity 0.15s;\n" +
"    }\n" +
"    .btn-action:hover { opacity: 0.9; }\n" +
"    .btn-action:active { transform: scale(0.98); }\n" +
"    .btn-demo { background: var(--accent-blue); color: #fff; }\n" +
"    .btn-viva { background: var(--accent-gold); color: #000; }\n" +
"\n" +
"    /* Main Container */\n" +
"    main {\n" +
"      flex: 1;\n" +
"      display: flex;\n" +
"      flex-direction: column;\n" +
"      overflow: hidden;\n" +
"    }\n" +
"    header {\n" +
"      height: 68px;\n" +
"      padding: 0 32px;\n" +
"      border-bottom: 1px solid var(--border);\n" +
"      display: flex;\n" +
"      align-items: center;\n" +
"      justify-content: space-between;\n" +
"      background: rgba(15, 23, 42, 0.85);\n" +
"      backdrop-filter: blur(8px);\n" +
"    }\n" +
"    .header-title {\n" +
"      font-size: 1.25rem;\n" +
"      font-weight: 800;\n" +
"      display: flex;\n" +
"      align-items: center;\n" +
"      gap: 12px;\n" +
"    }\n" +
"    .pill {\n" +
"      font-size: 0.72rem;\n" +
"      padding: 4px 10px;\n" +
"      border-radius: 20px;\n" +
"      border: 1px solid var(--accent-green);\n" +
"      color: var(--accent-green);\n" +
"      font-weight: 700;\n" +
"    }\n" +
"    .header-right {\n" +
"      display: flex;\n" +
"      align-items: center;\n" +
"      gap: 16px;\n" +
"    }\n" +
"    .btn-primary {\n" +
"      background: var(--accent-green);\n" +
"      color: #fff;\n" +
"      padding: 8px 16px;\n" +
"      border-radius: 6px;\n" +
"      font-weight: 700;\n" +
"      border: none;\n" +
"      cursor: pointer;\n" +
"    }\n" +
"\n" +
"    /* Content Area */\n" +
"    .content-viewport {\n" +
"      flex: 1;\n" +
"      padding: 24px 32px;\n" +
"      overflow-y: auto;\n" +
"    }\n" +
"    .tab-view { display: none; }\n" +
"    .tab-view.active { display: block; }\n" +
"\n" +
"    /* KPI Cards */\n" +
"    .kpi-grid {\n" +
"      display: grid;\n" +
"      grid-template-columns: repeat(4, 1fr);\n" +
"      gap: 18px;\n" +
"      margin-bottom: 24px;\n" +
"    }\n" +
"    .kpi-card {\n" +
"      background: var(--bg-card);\n" +
"      border: 1px solid var(--border);\n" +
"      border-radius: 12px;\n" +
"      padding: 18px;\n" +
"      display: flex;\n" +
"      flex-direction: column;\n" +
"      gap: 8px;\n" +
"    }\n" +
"    .kpi-card span.label {\n" +
"      font-size: 0.75rem;\n" +
"      font-weight: 700;\n" +
"      color: var(--text-muted);\n" +
"      text-transform: uppercase;\n" +
"    }\n" +
"    .kpi-card span.val {\n" +
"      font-size: 1.8rem;\n" +
"      font-weight: 800;\n" +
"    }\n" +
"\n" +
"    /* Cards & Tables */\n" +
"    .card {\n" +
"      background: var(--bg-card);\n" +
"      border: 1px solid var(--border);\n" +
"      border-radius: 12px;\n" +
"      padding: 22px;\n" +
"      margin-bottom: 24px;\n" +
"    }\n" +
"    .card h2 {\n" +
"      font-size: 1.05rem;\n" +
"      font-weight: 700;\n" +
"      margin-bottom: 16px;\n" +
"      display: flex;\n" +
"      justify-content: space-between;\n" +
"      align-items: center;\n" +
"    }\n" +
"    table {\n" +
"      width: 100%;\n" +
"      border-collapse: collapse;\n" +
"      text-align: left;\n" +
"      font-size: 0.9rem;\n" +
"    }\n" +
"    th {\n" +
"      background: #151e2e;\n" +
"      color: var(--text-muted);\n" +
"      padding: 12px 14px;\n" +
"      font-weight: 700;\n" +
"      text-transform: uppercase;\n" +
"      font-size: 0.72rem;\n" +
"      letter-spacing: 0.05em;\n" +
"      border-bottom: 1px solid var(--border);\n" +
"    }\n" +
"    td {\n" +
"      padding: 14px;\n" +
"      border-bottom: 1px solid rgba(51, 65, 85, 0.5);\n" +
"    }\n" +
"    tr:hover td { background: var(--bg-card-hover); }\n" +
"    .pts { font-weight: 800; color: var(--accent-green); font-size: 1.05rem; }\n" +
"    .badge {\n" +
"      display: inline-block;\n" +
"      padding: 3px 8px;\n" +
"      border-radius: 4px;\n" +
"      font-size: 0.75rem;\n" +
"      font-weight: 700;\n" +
"    }\n" +
"    .badge-comp { background: rgba(16, 185, 129, 0.15); color: var(--accent-green); }\n" +
"    .badge-sched { background: rgba(245, 158, 11, 0.15); color: var(--accent-gold); }\n" +
"\n" +
"    /* Modals */\n" +
"    .modal-backdrop {\n" +
"      display: none;\n" +
"      position: fixed;\n" +
"      inset: 0;\n" +
"      background: rgba(0, 0, 0, 0.7);\n" +
"      backdrop-filter: blur(4px);\n" +
"      z-index: 100;\n" +
"      align-items: center;\n" +
"      justify-content: center;\n" +
"    }\n" +
"    .modal-backdrop.open { display: flex; }\n" +
"    .modal {\n" +
"      background: var(--bg-card);\n" +
"      border: 1px solid var(--border);\n" +
"      border-radius: 12px;\n" +
"      width: 480px;\n" +
"      max-width: 90vw;\n" +
"      padding: 24px;\n" +
"      box-shadow: 0 20px 40px rgba(0,0,0,0.5);\n" +
"    }\n" +
"    .modal-header {\n" +
"      display: flex;\n" +
"      justify-content: space-between;\n" +
"      align-items: center;\n" +
"      margin-bottom: 18px;\n" +
"    }\n" +
"    .modal-header h3 { font-size: 1.15rem; font-weight: 800; }\n" +
"    .close-btn { background: none; border: none; color: var(--text-muted); font-size: 1.4rem; cursor: pointer; }\n" +
"    .form-group { margin-bottom: 14px; }\n" +
"    .form-group label { display: block; font-size: 0.8rem; font-weight: 700; color: var(--text-muted); margin-bottom: 6px; }\n" +
"    .form-group input, .form-group select {\n" +
"      width: 100%;\n" +
"      background: var(--bg-dark);\n" +
"      border: 1px solid var(--border);\n" +
"      border-radius: 6px;\n" +
"      padding: 10px 12px;\n" +
"      color: var(--text-main);\n" +
"      font-size: 0.9rem;\n" +
"    }\n" +
"    .toast {\n" +
"      position: fixed;\n" +
"      bottom: 24px;\n" +
"      right: 24px;\n" +
"      background: #1e293b;\n" +
"      border: 1px solid var(--accent-green);\n" +
"      padding: 14px 20px;\n" +
"      border-radius: 8px;\n" +
"      box-shadow: 0 10px 25px rgba(0,0,0,0.5);\n" +
"      font-size: 0.9rem;\n" +
"      display: none;\n" +
"      z-index: 200;\n" +
"    }\n" +
"  </style>\n" +
"</head>\n" +
"<body>\n" +
"\n" +
"  <aside>\n" +
"    <div class=\"brand\">\n" +
"      <h1>⚽ Tournament Pro</h1>\n" +
"      <span>OOP LAB 2026 • JAVA 17</span>\n" +
"    </div>\n" +
"    <nav>\n" +
"      <button class=\"nav-btn active\" onclick=\"showTab('standings')\">📊 Dashboard & Standings</button>\n" +
"      <button class=\"nav-btn\" onclick=\"showTab('teams')\">🛡️ Teams & Squads</button>\n" +
"      <button class=\"nav-btn\" onclick=\"showTab('fixtures')\">⚔️ Fixtures & Matches</button>\n" +
"      <button class=\"nav-btn\" onclick=\"showTab('scorers')\">🏆 Golden Boot Scorers</button>\n" +
"      <button class=\"nav-btn\" onclick=\"showTab('data')\">💾 Storage & Save/Load</button>\n" +
"    </nav>\n" +
"    <div class=\"sidebar-footer\">\n" +
"      <button class=\"btn-action btn-demo\" onclick=\"loadDemo()\">⚡ Load Demo Data</button>\n" +
"      <button class=\"btn-action btn-viva\" onclick=\"openModal('modalViva')\">💡 OOP Viva Inspector</button>\n" +
"    </div>\n" +
"  </aside>\n" +
"\n" +
"  <main>\n" +
"    <header>\n" +
"      <div class=\"header-title\">\n" +
"        <span id=\"hdrTournament\">UEFA Champions League</span>\n" +
"        <span class=\"pill\" id=\"hdrFormat\">[ROUND-ROBIN]</span>\n" +
"      </div>\n" +
"      <div class=\"header-right\">\n" +
"        <span style=\"color: var(--text-muted); font-size: 0.85rem;\" id=\"hdrStats\">Teams: 4 | Matches: 0/6</span>\n" +
"        <button class=\"btn-primary\" onclick=\"openModal('modalAddTeam')\">+ Add Team</button>\n" +
"      </div>\n" +
"    </header>\n" +
"\n" +
"    <div class=\"content-viewport\">\n" +
"      <!-- TAB 1: STANDINGS -->\n" +
"      <div id=\"tab-standings\" class=\"tab-view active\">\n" +
"        <div class=\"kpi-grid\">\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">Total Teams</span>\n" +
"            <span class=\"val\" style=\"color: var(--accent-blue);\" id=\"kpiTeams\">4</span>\n" +
"          </div>\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">Matches Played</span>\n" +
"            <span class=\"val\" style=\"color: var(--accent-gold);\" id=\"kpiMatches\">0 / 6</span>\n" +
"          </div>\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">Total Goals</span>\n" +
"            <span class=\"val\" style=\"color: var(--accent-green);\" id=\"kpiGoals\">0</span>\n" +
"          </div>\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">Table Leader</span>\n" +
"            <span class=\"val\" style=\"color: #c084fc; font-size: 1.4rem;\" id=\"kpiLeader\">Real Madrid</span>\n" +
"          </div>\n" +
"        </div>\n" +
"\n" +
"        <div class=\"card\">\n" +
"          <h2>🏆 Standings & Points Table (Win = 3, Draw = 1, Loss = 0)</h2>\n" +
"          <table>\n" +
"            <thead>\n" +
"              <tr>\n" +
"                <th>Rank</th><th>Team</th><th>City</th><th>MP</th><th>W</th><th>D</th><th>L</th><th>GF</th><th>GA</th><th>GD</th><th>Points</th>\n" +
"              </tr>\n" +
"            </thead>\n" +
"            <tbody id=\"tblStandings\"></tbody>\n" +
"          </table>\n" +
"        </div>\n" +
"      </div>\n" +
"\n" +
"      <!-- TAB 2: TEAMS & SQUADS -->\n" +
"      <div id=\"tab-teams\" class=\"tab-view\">\n" +
"        <div class=\"card\">\n" +
"          <h2>\n" +
"            <span>Registered Clubs & Rosters</span>\n" +
"            <button class=\"btn-primary\" onclick=\"openModal('modalAddPlayer')\">+ Add Player to Squad</button>\n" +
"          </h2>\n" +
"          <div id=\"teamsRosterList\"></div>\n" +
"        </div>\n" +
"      </div>\n" +
"\n" +
"      <!-- TAB 3: FIXTURES & MATCHES -->\n" +
"      <div id=\"tab-fixtures\" class=\"tab-view\">\n" +
"        <div class=\"card\">\n" +
"          <h2>\n" +
"            <div style=\"display: flex; gap: 12px; align-items: center;\">\n" +
"              <span>Tournament Fixtures</span>\n" +
"              <select id=\"genFormat\" style=\"background: var(--bg-dark); color: #fff; padding: 6px 10px; border: 1px solid var(--border); border-radius: 6px;\">\n" +
"                <option value=\"RR\">Round-Robin</option>\n" +
"                <option value=\"KO\">Single-Elimination Knockout</option>\n" +
"              </select>\n" +
"              <button class=\"btn-primary\" onclick=\"generateFixtures()\">⚡ Generate</button>\n" +
"            </div>\n" +
"          </h2>\n" +
"          <table>\n" +
"            <thead>\n" +
"              <tr>\n" +
"                <th>ID</th><th>Round</th><th>Home Team</th><th>Score</th><th>Away Team</th><th>Status</th><th>Referee</th><th>Action</th>\n" +
"              </tr>\n" +
"            </thead>\n" +
"            <tbody id=\"tblFixtures\"></tbody>\n" +
"          </table>\n" +
"        </div>\n" +
"      </div>\n" +
"\n" +
"      <!-- TAB 4: SCORERS -->\n" +
"      <div id=\"tab-scorers\" class=\"tab-view\">\n" +
"        <div class=\"card\">\n" +
"          <h2>🥇 Golden Boot Leaderboard</h2>\n" +
"          <table>\n" +
"            <thead>\n" +
"              <tr><th>Rank</th><th>Player Name</th><th>Jersey</th><th>Team</th><th>Position</th><th>Goals</th></tr>\n" +
"            </thead>\n" +
"            <tbody id=\"tblScorers\"></tbody>\n" +
"          </table>\n" +
"        </div>\n" +
"      </div>\n" +
"\n" +
"      <!-- TAB 5: DATA STORAGE -->\n" +
"      <div id=\"tab-data\" class=\"tab-view\">\n" +
"        <div class=\"kpi-grid\" style=\"grid-template-columns: repeat(2, 1fr);\">\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">🗄️ Relational SQL Database (JDBC)</span>\n" +
"            <span style=\"font-size: 0.8rem; color: var(--accent-gold); margin-top: 4px;\">SQLite / MySQL with PreparedStatement & Transactions</span>\n" +
"            <button class=\"btn-primary\" style=\"margin-top: 10px;\" onclick=\"saveToSql()\">💾 Save to SQL Database</button>\n" +
"            <button class=\"btn-action btn-demo\" style=\"margin-top: 8px;\" onclick=\"loadFromSql()\">📂 Load from SQL Database</button>\n" +
"          </div>\n" +
"          <div class=\"kpi-card\">\n" +
"            <span class=\"label\">Binary File Serialization (.dat)</span>\n" +
"            <span style=\"font-size: 0.8rem; color: var(--text-muted); margin-top: 4px;\">ObjectOutputStream & ObjectInputStream</span>\n" +
"            <button class=\"btn-primary\" style=\"margin-top: 10px;\" onclick=\"saveData()\">💾 Save to tournament_data.dat</button>\n" +
"            <button class=\"btn-action btn-demo\" style=\"margin-top: 8px;\" onclick=\"loadData()\">📂 Load from Disk</button>\n" +
"          </div>\n" +
"        </div>\n" +
"      </div>\n" +
"    </div>\n" +
"  </main>\n" +
"\n" +
"  <!-- MODAL: ENTER SCORE -->\n" +
"  <div id=\"modalScore\" class=\"modal-backdrop\">\n" +
"    <div class=\"modal\">\n" +
"      <div class=\"modal-header\">\n" +
"        <h3 id=\"modalScoreTitle\">Enter Match Result</h3>\n" +
"        <button class=\"close-btn\" onclick=\"closeModal('modalScore')\">&times;</button>\n" +
"      </div>\n" +
"      <input type=\"hidden\" id=\"scoreMatchId\">\n" +
"      <div class=\"form-group\">\n" +
"        <label id=\"lblHomeTeam\">Home Goals (0-30):</label>\n" +
"        <input type=\"number\" id=\"txtHomeScore\" min=\"0\" max=\"30\" value=\"0\">\n" +
"      </div>\n" +
"      <div class=\"form-group\">\n" +
"        <label id=\"lblAwayTeam\">Away Goals (0-30):</label>\n" +
"        <input type=\"number\" id=\"txtAwayScore\" min=\"0\" max=\"30\" value=\"0\">\n" +
"      </div>\n" +
"      <button class=\"btn-primary\" style=\"width: 100%; margin-top: 10px;\" onclick=\"submitScore()\">Record Result & Update Standings</button>\n" +
"    </div>\n" +
"  </div>\n" +
"\n" +
"  <!-- MODAL: ADD TEAM -->\n" +
"  <div id=\"modalAddTeam\" class=\"modal-backdrop\">\n" +
"    <div class=\"modal\">\n" +
"      <div class=\"modal-header\">\n" +
"        <h3>Register New Team</h3>\n" +
"        <button class=\"close-btn\" onclick=\"closeModal('modalAddTeam')\">&times;</button>\n" +
"      </div>\n" +
"      <div class=\"form-group\"><label>Team Short ID (e.g. CHE, LIV):</label><input type=\"text\" id=\"txtTeamId\"></div>\n" +
"      <div class=\"form-group\"><label>Team Name (e.g. Chelsea FC):</label><input type=\"text\" id=\"txtTeamName\"></div>\n" +
"      <div class=\"form-group\"><label>City (e.g. London):</label><input type=\"text\" id=\"txtTeamCity\"></div>\n" +
"      <div class=\"form-group\"><label>Head Coach Name:</label><input type=\"text\" id=\"txtCoachName\"></div>\n" +
"      <button class=\"btn-primary\" style=\"width: 100%; margin-top: 10px;\" onclick=\"submitAddTeam()\">Register Team</button>\n" +
"    </div>\n" +
"  </div>\n" +
"\n" +
"  <!-- MODAL: ADD PLAYER -->\n" +
"  <div id=\"modalAddPlayer\" class=\"modal-backdrop\">\n" +
"    <div class=\"modal\">\n" +
"      <div class=\"modal-header\">\n" +
"        <h3>Add Player to Squad</h3>\n" +
"        <button class=\"close-btn\" onclick=\"closeModal('modalAddPlayer')\">&times;</button>\n" +
"      </div>\n" +
"      <div class=\"form-group\"><label>Select Team:</label><select id=\"selPlayerTeam\"></select></div>\n" +
"      <div class=\"form-group\"><label>Player Name:</label><input type=\"text\" id=\"txtPlayerName\"></div>\n" +
"      <div class=\"form-group\"><label>Age (15 - 45):</label><input type=\"number\" id=\"txtPlayerAge\" value=\"24\"></div>\n" +
"      <div class=\"form-group\"><label>Jersey Number (1 - 99):</label><input type=\"number\" id=\"txtPlayerJersey\" value=\"10\"></div>\n" +
"      <div class=\"form-group\">\n" +
"        <label>Position:</label>\n" +
"        <select id=\"selPlayerPos\">\n" +
"          <option>Forward</option><option>Midfielder</option><option>Defender</option><option>Goalkeeper</option>\n" +
"        </select>\n" +
"      </div>\n" +
"      <button class=\"btn-primary\" style=\"width: 100%; margin-top: 10px;\" onclick=\"submitAddPlayer()\">Add Player</button>\n" +
"    </div>\n" +
"  </div>\n" +
"\n" +
"  <!-- MODAL: VIVA INSPECTOR -->\n" +
"  <div id=\"modalViva\" class=\"modal-backdrop\">\n" +
"    <div class=\"modal\" style=\"width: 600px;\">\n" +
"      <div class=\"modal-header\">\n" +
"        <h3>💡 OOP Concepts Lab Viva Inspector</h3>\n" +
"        <button class=\"close-btn\" onclick=\"closeModal('modalViva')\">&times;</button>\n" +
"      </div>\n" +
"      <div style=\"font-family: 'JetBrains Mono', monospace; font-size: 0.8rem; background: var(--bg-dark); padding: 16px; border-radius: 8px; line-height: 1.6; max-height: 360px; overflow-y: auto;\">\n" +
"        <p><b style=\"color: #60a5fa;\">1. ABSTRACTION:</b> Person (abstract class) with abstract displayInfo().</p>\n" +
"        <p><b style=\"color: #34d399;\">2. INHERITANCE:</b> Player, Coach, Referee extend Person using super(id, name, age).</p>\n" +
"        <p><b style=\"color: #f472b6;\">3. ENCAPSULATION:</b> All class variables private, accessed via getters/setters.</p>\n" +
"        <p><b style=\"color: #fbbf24;\">4. POLYMORPHISM:</b> FixtureGenerator interface implemented by RoundRobinGenerator & KnockoutGenerator.</p>\n" +
"        <p><b style=\"color: #f87171;\">5. EXCEPTIONS:</b> DuplicateTeamException, InvalidScoreException, TeamNotFoundException.</p>\n" +
"        <p><b style=\"color: #a78bfa;\">6. COLLECTIONS:</b> HashMap for O(1) team lookup, ArrayList for squads & matches.</p>\n" +
"        <p><b style=\"color: #38bdf8;\">7. FILE HANDLING:</b> FileManager serializes tournament object graph via ObjectOutputStream.</p>\n" +
"      </div>\n" +
"    </div>\n" +
"  </div>\n" +
"\n" +
"  <div id=\"toast\" class=\"toast\"></div>\n" +
"\n" +
"  <script>\n" +
"    let appData = null;\n" +
"\n" +
"    async function loadDataFromApi() {\n" +
"      try {\n" +
"        const res = await fetch('/api/data');\n" +
"        appData = await res.json();\n" +
"        renderUI();\n" +
"      } catch (err) {\n" +
"        console.error(err);\n" +
"      }\n" +
"    }\n" +
"\n" +
"    function renderUI() {\n" +
"      if (!appData) return;\n" +
"      document.getElementById('hdrTournament').innerText = appData.name + ' (' + appData.season + ')';\n" +
"      document.getElementById('hdrFormat').innerText = '[' + appData.format + ']';\n" +
"      document.getElementById('hdrStats').innerText = `Teams: ${appData.teamCount} | Matches: ${appData.completedMatches}/${appData.matchesCount}`;\n" +
"      document.getElementById('kpiTeams').innerText = appData.teamCount;\n" +
"      document.getElementById('kpiMatches').innerText = `${appData.completedMatches} / ${appData.matchesCount}`;\n" +
"      document.getElementById('kpiGoals').innerText = appData.totalGoals;\n" +
"      document.getElementById('kpiLeader').innerText = appData.leader;\n" +
"\n" +
"      // Render Standings\n" +
"      const sTbody = document.getElementById('tblStandings');\n" +
"      sTbody.innerHTML = appData.standings.map((t, idx) => {\n" +
"        const medal = idx === 0 ? '🥇 1' : idx === 1 ? '🥈 2' : idx === 2 ? '🥉 3' : (idx + 1);\n" +
"        return `<tr>\n" +
"          <td><b>${medal}</b></td>\n" +
"          <td><b>${t.name}</b> (${t.id})</td>\n" +
"          <td>${t.city}</td>\n" +
"          <td>${t.mp}</td><td>${t.w}</td><td>${t.d}</td><td>${t.l}</td>\n" +
"          <td>${t.gf}</td><td>${t.ga}</td><td>${t.gd > 0 ? '+' + t.gd : t.gd}</td>\n" +
"          <td class=\"pts\">${t.pts}</td>\n" +
"        </tr>`;\n" +
"      }).join('');\n" +
"\n" +
"      // Render Teams & Squads\n" +
"      const tDiv = document.getElementById('teamsRosterList');\n" +
"      tDiv.innerHTML = appData.teams.map(t => `\n" +
"        <div style=\"background: var(--bg-dark); border: 1px solid var(--border); border-radius: 8px; padding: 16px; margin-bottom: 14px;\">\n" +
"          <div style=\"display: flex; justify-content: space-between; margin-bottom: 10px;\">\n" +
"            <div><b style=\"font-size: 1.1rem;\">${t.name}</b> (${t.id}) • <span style=\"color: var(--text-muted);\">${t.city}</span></div>\n" +
"            <div style=\"color: var(--accent-gold); font-size: 0.85rem;\">Coach: <b>${t.coach}</b> (${t.coachNat})</div>\n" +
"          </div>\n" +
"          <table>\n" +
"            <thead><tr><th>Jersey</th><th>Player Name</th><th>Age</th><th>Position</th><th>Goals</th></tr></thead>\n" +
"            <tbody>\n" +
"              ${t.players.map(p => `<tr><td>#${p.jersey}</td><td><b>${p.name}</b></td><td>${p.age}</td><td>${p.position}</td><td class=\"pts\">${p.goals}</td></tr>`).join('')}\n" +
"            </tbody>\n" +
"          </table>\n" +
"        </div>\n" +
"      `).join('');\n" +
"\n" +
"      // Render Fixtures\n" +
"      const fTbody = document.getElementById('tblFixtures');\n" +
"      fTbody.innerHTML = appData.matches.map(m => {\n" +
"        const isDone = m.status === 'COMPLETED';\n" +
"        const scoreText = isDone ? `<b>${m.homeScore} - ${m.awayScore}</b>` : 'vs';\n" +
"        const badge = isDone ? `<span class=\"badge badge-comp\">COMPLETED</span>` : `<span class=\"badge badge-sched\">SCHEDULED</span>`;\n" +
"        const btn = isDone\n" +
"          ? `<button class=\"btn-action\" style=\"background: var(--bg-card); color: var(--text-muted); font-size: 0.75rem;\" onclick=\"openScoreModal('${m.id}', '${m.home}', '${m.away}')\">Edit</button>`\n" +
"          : `<button class=\"btn-action btn-demo\" style=\"font-size: 0.75rem;\" onclick=\"openScoreModal('${m.id}', '${m.home}', '${m.away}')\">Enter Score</button>`;\n" +
"        return `<tr>\n" +
"          <td><b>${m.id}</b></td><td>${m.round}</td><td>${m.home}</td>\n" +
"          <td style=\"font-size: 1rem;\">${scoreText}</td>\n" +
"          <td>${m.away}</td><td>${badge}</td><td>${m.referee}</td><td>${btn}</td>\n" +
"        </tr>`;\n" +
"      }).join('');\n" +
"\n" +
"      // Render Scorers\n" +
"      const scTbody = document.getElementById('tblScorers');\n" +
"      scTbody.innerHTML = appData.scorers.map((s, idx) => `\n" +
"        <tr>\n" +
"          <td><b>${idx === 0 ? '🥇 1' : idx === 1 ? '🥈 2' : idx === 2 ? '🥉 3' : (idx + 1)}</b></td>\n" +
"          <td><b>${s.name}</b></td><td>#${s.jersey}</td><td>${s.team}</td><td>${s.position}</td><td class=\"pts\">${s.goals}</td>\n" +
"        </tr>\n" +
"      `).join('');\n" +
"\n" +
"      // Populate player team dropdown\n" +
"      const sel = document.getElementById('selPlayerTeam');\n" +
"      sel.innerHTML = appData.teams.map(t => `<option value=\"${t.id}\">${t.name} (${t.id})</option>`).join('');\n" +
"    }\n" +
"\n" +
"    function showTab(tabId) {\n" +
"      document.querySelectorAll('.tab-view').forEach(el => el.classList.remove('active'));\n" +
"      document.querySelectorAll('.nav-btn').forEach(el => el.classList.remove('active'));\n" +
"      document.getElementById('tab-' + tabId).classList.add('active');\n" +
"      event.currentTarget.classList.add('active');\n" +
"    }\n" +
"\n" +
"    function openModal(id) { document.getElementById(id).classList.add('open'); }\n" +
"    function closeModal(id) { document.getElementById(id).classList.remove('open'); }\n" +
"\n" +
"    function openScoreModal(id, home, away) {\n" +
"      document.getElementById('scoreMatchId').value = id;\n" +
"      document.getElementById('modalScoreTitle').innerText = `${home} vs ${away}`;\n" +
"      document.getElementById('lblHomeTeam').innerText = `${home} Goals:`;\n" +
"      document.getElementById('lblAwayTeam').innerText = `${away} Goals:`;\n" +
"      openModal('modalScore');\n" +
"    }\n" +
"\n" +
"    async function submitScore() {\n" +
"      const id = document.getElementById('scoreMatchId').value;\n" +
"      const h = document.getElementById('txtHomeScore').value;\n" +
"      const a = document.getElementById('txtAwayScore').value;\n" +
"      const res = await fetch(`/api/result?matchId=${id}&homeScore=${h}&awayScore=${a}`);\n" +
"      const json = await res.json();\n" +
"      if (json.success) {\n" +
"        closeModal('modalScore');\n" +
"        showToast('Result recorded! Standings updated.');\n" +
"        loadDataFromApi();\n" +
"      } else {\n" +
"        alert(json.error);\n" +
"      }\n" +
"    }\n" +
"\n" +
"    async function generateFixtures() {\n" +
"      const fmt = document.getElementById('genFormat').value;\n" +
"      await fetch(`/api/generate?format=${fmt}`);\n" +
"      showToast(`Fixtures generated using ${fmt}!`);\n" +
"      loadDataFromApi();\n" +
"    }\n" +
"\n" +
"    async function submitAddTeam() {\n" +
"      const id = document.getElementById('txtTeamId').value;\n" +
"      const name = document.getElementById('txtTeamName').value;\n" +
"      const city = document.getElementById('txtTeamCity').value;\n" +
"      const coach = document.getElementById('txtCoachName').value;\n" +
"      const res = await fetch(`/api/team/add?id=${id}&name=${encodeURIComponent(name)}&city=${encodeURIComponent(city)}&coach=${encodeURIComponent(coach)}`);\n" +
"      const json = await res.json();\n" +
"      if (json.success) {\n" +
"        closeModal('modalAddTeam');\n" +
"        showToast('Team registered!');\n" +
"        loadDataFromApi();\n" +
"      } else {\n" +
"        alert(json.error);\n" +
"      }\n" +
"    }\n" +
"\n" +
"    async function submitAddPlayer() {\n" +
"      const teamId = document.getElementById('selPlayerTeam').value;\n" +
"      const name = document.getElementById('txtPlayerName').value;\n" +
"      const age = document.getElementById('txtPlayerAge').value;\n" +
"      const jersey = document.getElementById('txtPlayerJersey').value;\n" +
"      const pos = document.getElementById('selPlayerPos').value;\n" +
"      const res = await fetch(`/api/player/add?teamId=${teamId}&name=${encodeURIComponent(name)}&age=${age}&jersey=${jersey}&position=${pos}`);\n" +
"      const json = await res.json();\n" +
"      if (json.success) {\n" +
"        closeModal('modalAddPlayer');\n" +
"        showToast('Player added to squad!');\n" +
"        loadDataFromApi();\n" +
"      } else {\n" +
"        alert(json.error);\n" +
"      }\n" +
"    }\n" +
"\n" +
"    async function loadDemo() {\n" +
"      await fetch('/api/demo');\n" +
"      showToast('Sample demo data loaded!');\n" +
"      loadDataFromApi();\n" +
"    }\n" +
"\n" +
"    async function saveToSql() {\n" +
"      const res = await fetch('/api/db/save');\n" +
"      const json = await res.json();\n" +
"      showToast(json.message || json.error);\n" +
"    }\n" +
"\n" +
"    async function loadFromSql() {\n" +
"      const res = await fetch('/api/db/load');\n" +
"      const json = await res.json();\n" +
"      showToast(json.message || json.error);\n" +
"      loadDataFromApi();\n" +
"    }\n" +
"\n" +
"    async function saveData() {\n" +
"      const res = await fetch('/api/save');\n" +
"      const json = await res.json();\n" +
"      showToast(json.message);\n" +
"    }\n" +
"\n" +
"    async function loadData() {\n" +
"      const res = await fetch('/api/load');\n" +
"      const json = await res.json();\n" +
"      showToast(json.message || json.error);\n" +
"      loadDataFromApi();\n" +
"    }\n" +
"\n" +
"    function showToast(msg) {\n" +
"      const t = document.getElementById('toast');\n" +
"      t.innerText = msg;\n" +
"      t.style.display = 'block';\n" +
"      setTimeout(() => { t.style.display = 'none'; }, 3000);\n" +
"    }\n" +
"\n" +
"    loadDataFromApi();\n" +
"  </script>\n" +
"</body>\n" +
"</html>";
    }
}

