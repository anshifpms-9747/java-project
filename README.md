# ⚽ Football Tournament Management System

A complete, production-grade console & desktop application built in pure **Java (JDK 17)** demonstrating core Object-Oriented Programming (OOP) paradigms for academic laboratory defense and practical exams.

---

## 🌟 Key Features

- **Dual Mode Interface**:
  - **Modern Swing GUI**: Dark SaaS dashboard aesthetic with live KPI cards, master-detail squad rosters, match center, and interactive score entry modals.
  - **Interactive CLI**: Menu-driven console interface with robust input validation.
- **Fixture Generation**:
  - **Round-Robin**: Circle algorithm ensuring each team plays every opponent once per round.
  - **Single-Elimination Knockout**: Dynamic bracket generation with automatic winner advancement to future rounds.
- **Auto-Updating Points Table**: Standings sorted by Points (Win=3, Draw=1, Loss=0), then Goal Difference, then Goals Scored.
- **Top Scorers Leaderboard**: Golden Boot race tracking goals scored across all clubs.
- **Data Persistence**: Java Object Serialization (`ObjectOutputStream` & `ObjectInputStream`) saving the full object graph to `.dat` files.
- **Built-in Sample Data**: 4 elite European clubs (Real Madrid, Manchester City, Bayern Munich, PSG), complete squads, coaches, and FIFA match officials.

---

## 🧱 OOP Concepts Demonstrated

| OOP Concept | Implementation in Code |
| :--- | :--- |
| **Abstraction** | `Person` abstract class defining abstract method `displayInfo()` |
| **Inheritance** | `Player`, `Coach`, and `Referee` extending `Person` with `super(...)` |
| **Encapsulation** | Private fields in all domain entities (`Team`, `Match`, `Person`, etc.) with public accessors and mutators |
| **Polymorphism (Interfaces)** | `FixtureGenerator` interface implemented by `RoundRobinGenerator` and `KnockoutGenerator` |
| **Polymorphism (Overriding)** | `displayInfo()` overridden across `Player`, `Coach`, and `Referee` via dynamic method dispatch |
| **Custom Exceptions** | `DuplicateTeamException`, `InvalidScoreException`, `TeamNotFoundException` (Checked exceptions) |
| **Collections Framework** | `HashMap<String, Team>` for $O(1)$ team lookup; `ArrayList` for squads and fixtures |
| **File Handling** | `FileManager` class implementing Java Object Serialization with try-with-resources |

---

## 🚀 Quickstart & Running

### Option 1: Run the Swing GUI
Double-click `Run_Swing_UI.bat` or run:
```powershell
java -cp bin football.TournamentGUI
```

### Option 2: Run the Console Menu (CLI)
```powershell
java -jar football-tournament.jar
# or
java -cp bin football.Main --cli
```

### Option 3: Compile from Source
```powershell
# Compile all source files into bytecode
javac -d bin src/football/*.java

# Package into executable JAR
jar cfm football-tournament.jar MANIFEST.MF -C bin .
```

---

## 📁 Project Structure

```
football-project/
├── .gitignore
├── MANIFEST.MF
├── README.md
├── Run_Swing_UI.bat
├── Run_Swing_Debug.bat
├── src/
│   └── football/
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
│       ├── FileManager.java            # Serialization utility
│       ├── SampleDataLoader.java       # Demo data seeder
│       ├── Tournament.java             # Core domain model
│       ├── TournamentGUI.java          # Modern Swing GUI
│       ├── WebServer.java              # Embedded HTTP server
│       └── Main.java                   # Application entry point
└── bin/                                # Compiled bytecode (.class)
```

---

## 🎓 Academic Lab Viva Reference

1. **Why is `Person` an abstract class?**  
   It defines shared attributes (`id`, `name`, `age`) and enforces that every concrete role (`Player`, `Coach`, `Referee`) defines its own `displayInfo()` implementation.
2. **Where is Interface Polymorphism used?**  
   In `Tournament.java`, the tournament holds a reference to `FixtureGenerator` without knowing whether it is `RoundRobinGenerator` or `KnockoutGenerator`.
3. **Why use checked exceptions?**  
   Exceptions like `DuplicateTeamException` and `InvalidScoreException` represent domain rule violations that the user interface is expected to catch and display gracefully.
