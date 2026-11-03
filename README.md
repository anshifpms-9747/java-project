# ⚽ Football Tournament Management System

A complete, production-grade console & desktop application built in pure **Java (JDK 17)** demonstrating core Object-Oriented Programming (OOP) paradigms and **Relational SQL Database Integration (JDBC)** for academic laboratory defense and practical exams.

---

## 🌟 Key Features

- **Relational SQL Database Integration (JDBC)**:
  - Supports **SQLite** (`tournament.db` zero-configuration embedded database) and **MySQL** via `db.properties`.
  - Full relational schema (`teams`, `players`, `matches`, `referees`, `tournament_meta`) with primary keys, foreign keys, and batch statements.
  - ACID transaction integrity (`setAutoCommit(false)`, `commit()`, and automatic rollback on error).
- **Dual Mode Interface**:
  - **Modern Swing GUI**: Dark SaaS dashboard aesthetic with live KPI cards, master-detail squad rosters, match center, interactive score entry modals, and SQL database sync controls.
  - **Interactive CLI**: Menu-driven console interface with robust input validation.
  - **Embedded Web Application**: Built-in HTTP server (`http://localhost:8080`) providing a web SPA dashboard.
- **Fixture Generation**:
  - **Round-Robin**: Circle algorithm ensuring each team plays every opponent once per round.
  - **Single-Elimination Knockout**: Dynamic bracket generation with automatic winner advancement to future rounds.
- **Auto-Updating Points Table**: Standings sorted by Points (Win=3, Draw=1, Loss=0), then Goal Difference, then Goals Scored.
- **Top Scorers Leaderboard**: Golden Boot race tracking goals scored across all clubs.
- **Data Persistence**:
  - **SQL Database**: Save and load complete tournament state directly from relational tables.
  - **Binary Serialization**: `ObjectOutputStream` & `ObjectInputStream` saving to `.dat` files.
- **Built-in Sample Data**: 4 elite European clubs (Real Madrid, Manchester City, Bayern Munich, PSG), complete squads, coaches, and FIFA match officials.

---

## 🧱 OOP & Database Concepts Demonstrated

| Concept | Implementation in Code |
| :--- | :--- |
| **Abstraction** | `Person` abstract class defining abstract method `displayInfo()` |
| **Inheritance** | `Player`, `Coach`, and `Referee` extending `Person` with `super(...)` |
| **Encapsulation** | Private fields in all domain entities (`Team`, `Match`, `Person`, etc.) with public accessors and mutators |
| **Polymorphism (Interfaces)** | `FixtureGenerator` interface implemented by `RoundRobinGenerator` and `KnockoutGenerator` |
| **Polymorphism (Overriding)** | `displayInfo()` overridden across `Player`, `Coach`, and `Referee` via dynamic method dispatch |
| **Custom Exceptions** | `DuplicateTeamException`, `InvalidScoreException`, `TeamNotFoundException` (Checked exceptions) |
| **Collections Framework** | `HashMap<String, Team>` for $O(1)$ team lookup; `ArrayList` for squads and fixtures |
| **SQL Database (JDBC)** | `DatabaseManager` using `PreparedStatement`, batch execution, foreign keys, and transaction commits/rollbacks |
| **File Handling** | `FileManager` class implementing Java Object Serialization with try-with-resources |

---

## 🗄️ Database Architecture & Schema

The SQL schema is defined in [`schema.sql`](schema.sql) and auto-initialized by `DatabaseManager.java`:

- **`tournament_meta`**: Stores tournament name, season, and current fixture format.
- **`teams`**: Stores team ID, club name, home city, head coach credentials, and live standings stats.
- **`players`**: Stores player identity, jersey number, position, goals tally, and foreign key reference to `teams(id)`.
- **`referees`**: Stores licensed match officials, certification grade, and officiating experience.
- **`matches`**: Stores fixtures, scheduled times, completed scores, outcomes, assigned referee, and knockout tree links.

### Database Configuration (`db.properties`)
- **Default (SQLite)**: Uses `db.type=sqlite` and `db.file=tournament.db` — requires zero external server installation.
- **MySQL Mode**: Set `db.type=mysql` and specify JDBC URL, user, and password.

---

## 🚀 Quickstart & Running

### Option 1: Run the Swing GUI (with SQL & SQLite JDBC Driver)
Double-click `Run_Swing_UI.bat` or run:
```powershell
java -cp "bin;lib/*" football.TournamentGUI
```

### Option 2: Run the Console Menu (CLI)
```powershell
java -cp "bin;lib/*" football.Main --cli
```

### Option 3: Compile from Source
```powershell
# Compile all source files with JDBC drivers
$files = (Get-ChildItem src\football\*.java).FullName
javac -d bin -cp "lib/*" $files
```

---

## 📁 Project Structure

```
football-project/
├── .gitignore
├── MANIFEST.MF
├── README.md
├── db.properties               # Database configuration (SQLite/MySQL)
├── schema.sql                  # Relational database schema script
├── Run_Swing_UI.bat            # One-click GUI launcher
├── Run_Swing_Debug.bat         # GUI launcher with debug console
├── lib/                        # JDBC drivers (SQLite, MySQL, SLF4J)
│   ├── sqlite-jdbc-3.45.1.0.jar
│   ├── mysql-connector-j-8.3.0.jar
│   ├── slf4j-api-2.0.12.jar
│   └── slf4j-simple-2.0.12.jar
├── src/
│   └── football/
│       ├── DatabaseManager.java        # Relational SQL persistence (JDBC)
│       ├── Person.java                 # Abstract base class
│       ├── Player.java                 # Subclass extending Person
│       ├── Coach.java                  # Subclass extending Person
│       ├── Referee.java                # Subclass extending Person
│       ├── Team.java                   # Squad & statistics aggregator
│       ├── Match.java                  # Fixture state & progression
│       ├── FixtureGenerator.java       # Strategy interface
│       ├── RoundRobinGenerator.java    # Round-robin algorithm
│       ├── KnockoutGenerator.java      # Single-elimination tree
│       ├── DuplicateTeamException.java # Checked exception
│       ├── InvalidScoreException.java  # Checked exception
│       ├── TeamNotFoundException.java  # Checked exception
│       ├── FileManager.java            # Object serialization utility
│       ├── SampleDataLoader.java       # Demo data seeder
│       ├── Tournament.java             # Core domain model
│       ├── TournamentGUI.java          # Modern Swing GUI
│       ├── WebServer.java              # Embedded HTTP server
│       └── Main.java                   # Application entry point
└── bin/                                # Compiled bytecode (.class)
```

---

## 🎓 Academic Lab Viva Reference

1. **How is JDBC used in this project?**  
   In `DatabaseManager.java`, JDBC provides standard database connectivity via `DriverManager.getConnection()`. Queries and mutations are performed using `PreparedStatement` to prevent SQL injection and enhance query performance.
2. **How are ACID transactions maintained?**  
   `DatabaseManager` disables auto-commit (`conn.setAutoCommit(false)`), executes all insertions inside a batch, and calls `conn.commit()` only when all operations succeed. If an exception occurs, `conn.rollback()` restores previous database state.
3. **What is the database structure?**  
   The schema contains 5 relational tables (`tournament_meta`, `teams`, `players`, `referees`, `matches`) interconnected with primary keys and foreign keys (e.g. `players.team_id` referencing `teams.id`).
