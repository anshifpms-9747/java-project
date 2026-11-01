package football;

// ============================================================
// OOP CONCEPT: OBJECT CREATION & AGGREGATION
// SampleDataLoader provisions realistic, structured demo data
// so the system can be demonstrated instantly during viva and evaluation.
// ============================================================
public class SampleDataLoader {

    private SampleDataLoader() {}

    /**
     * Seeds the tournament with 4 European powerhouse clubs,
     * their coaching staff, star players with stats, and match officials.
     */
    public static void populate(Tournament tournament) {
        try {
            // ----------------------------------------------------
            // 1. Referees (Inheritance: Referee extends Person)
            // ----------------------------------------------------
            Referee ref1 = new Referee("REF01", "Szymon Marciniak", 43, "FIFA", 15);
            Referee ref2 = new Referee("REF02", "Daniele Orsato", 48, "FIFA", 20);
            Referee ref3 = new Referee("REF03", "Michael Oliver", 39, "UEFA", 12);
            tournament.addReferee(ref1);
            tournament.addReferee(ref2);
            tournament.addReferee(ref3);

            // ----------------------------------------------------
            // 2. Team 1: Real Madrid (RMA)
            // ----------------------------------------------------
            Team rma = new Team("RMA", "Real Madrid", "Madrid");
            Coach coachRma = new Coach("C01", "Carlo Ancelotti", 64, "Italian", "RMA");
            rma.setCoach(coachRma);

            rma.addPlayer(new Player("P001", "Vinicius Junior", 23, 7, "Forward", "RMA"));
            rma.addPlayer(new Player("P002", "Jude Bellingham", 20, 5, "Midfielder", "RMA"));
            rma.addPlayer(new Player("P003", "Antonio Rudiger", 31, 22, "Defender", "RMA"));
            rma.addPlayer(new Player("P004", "Thibaut Courtois", 32, 1, "Goalkeeper", "RMA"));
            tournament.addTeam(rma);

            // ----------------------------------------------------
            // 3. Team 2: Manchester City (MCI)
            // ----------------------------------------------------
            Team mci = new Team("MCI", "Manchester City", "Manchester");
            Coach coachMci = new Coach("C02", "Pep Guardiola", 53, "Spanish", "MCI");
            mci.setCoach(coachMci);

            mci.addPlayer(new Player("P005", "Erling Haaland", 23, 9, "Forward", "MCI"));
            mci.addPlayer(new Player("P006", "Kevin De Bruyne", 32, 17, "Midfielder", "MCI"));
            mci.addPlayer(new Player("P007", "Ruben Dias", 27, 3, "Defender", "MCI"));
            mci.addPlayer(new Player("P008", "Ederson Moraes", 30, 31, "Goalkeeper", "MCI"));
            tournament.addTeam(mci);

            // ----------------------------------------------------
            // 4. Team 3: Bayern Munich (BAY)
            // ----------------------------------------------------
            Team bay = new Team("BAY", "Bayern Munich", "Munich");
            Coach coachBay = new Coach("C03", "Thomas Tuchel", 50, "German", "BAY");
            bay.setCoach(coachBay);

            bay.addPlayer(new Player("P009", "Harry Kane", 30, 9, "Forward", "BAY"));
            bay.addPlayer(new Player("P010", "Jamal Musiala", 21, 42, "Midfielder", "BAY"));
            bay.addPlayer(new Player("P011", "Alphonso Davies", 23, 19, "Defender", "BAY"));
            bay.addPlayer(new Player("P012", "Manuel Neuer", 38, 1, "Goalkeeper", "BAY"));
            tournament.addTeam(bay);

            // ----------------------------------------------------
            // 5. Team 4: Paris Saint-Germain (PSG)
            // ----------------------------------------------------
            Team psg = new Team("PSG", "Paris Saint-Germain", "Paris");
            Coach coachPsg = new Coach("C04", "Luis Enrique", 54, "Spanish", "PSG");
            psg.setCoach(coachPsg);

            psg.addPlayer(new Player("P013", "Kylian Mbappe", 25, 7, "Forward", "PSG"));
            psg.addPlayer(new Player("P014", "Ousmane Dembele", 27, 10, "Forward", "PSG"));
            psg.addPlayer(new Player("P015", "Marquinhos", 30, 5, "Defender", "PSG"));
            psg.addPlayer(new Player("P016", "Gianluigi Donnarumma", 25, 99, "Goalkeeper", "PSG"));
            tournament.addTeam(psg);

            // Initial goals for top scorers demonstration
            tournament.addGoalsToPlayer("RMA", 7, 14);
            tournament.addGoalsToPlayer("RMA", 5, 19);
            tournament.addGoalsToPlayer("RMA", 22, 2);
            tournament.addGoalsToPlayer("MCI", 9, 27);
            tournament.addGoalsToPlayer("MCI", 17, 6);
            tournament.addGoalsToPlayer("BAY", 9, 36);
            tournament.addGoalsToPlayer("BAY", 42, 10);
            tournament.addGoalsToPlayer("PSG", 7, 27);
            tournament.addGoalsToPlayer("PSG", 10, 5);

            System.out.println("  [SAMPLE DATA] Loaded 4 elite teams, squads, coaches, and match officials!");
        } catch (DuplicateTeamException | TeamNotFoundException e) {
            System.err.println("  [SAMPLE DATA ERROR] " + e.getMessage());
        }
    }
}

