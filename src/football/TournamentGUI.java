package football;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// ============================================================
// OOP CONCEPT: GRAPHICAL USER INTERFACE & DEMONSTRATION HARNESS
// TournamentGUI provides a modern, web-application-inspired Swing interface
// for the Football Tournament Management System.
// Implements responsive card navigation, live KPI metrics, styled tables,
// and custom exception reporting modals.
// ============================================================
public class TournamentGUI extends JFrame {

    private Tournament tournament;

    // --- Web SaaS Dark Theme Palette ---
    public static final Color BG_SIDEBAR   = new Color(15, 23, 42);   // Slate 900 (#0F172A)
    public static final Color BG_MAIN      = new Color(30, 41, 59);   // Slate 800 (#1E293B)
    public static final Color BG_CARD      = new Color(51, 65, 85);   // Slate 700 (#334155)
    public static final Color BG_CARD_HOVER= new Color(71, 85, 105);  // Slate 600 (#475569)
    public static final Color BG_ROW_ALT   = new Color(39, 52, 73);   // Row alternating background
    public static final Color ACCENT_GREEN = new Color(16, 185, 129); // Emerald 500 (#10B981)
    public static final Color ACCENT_BLUE  = new Color(59, 130, 246); // Blue 500 (#3B82F6)
    public static final Color ACCENT_GOLD  = new Color(245, 158, 11); // Amber 500 (#F59E0B)
    public static final Color ACCENT_RED   = new Color(239, 68, 68);  // Red 500 (#EF4444)
    public static final Color TEXT_LIGHT   = new Color(248, 250, 252);// Slate 50 (#F8FAFC)
    public static final Color TEXT_MUTED   = new Color(148, 163, 184);// Slate 400 (#94A3B8)
    public static final Color BORDER_COLOR = new Color(71, 85, 105); // Border

    // --- Typography ---
    public static final Font FONT_LOGO     = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_NUM = new Font("Segoe UI", Font.BOLD, 22);

    // Layout Panels
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel lblHeaderStats;
    private JLabel lblHeaderFormat;

    // View Models
    private DefaultTableModel standingsModel;
    private DefaultTableModel fixturesModel;
    private DefaultTableModel squadModel;
    private DefaultTableModel scorersModel;
    private DefaultListModel<String> teamListModel;
    private JList<String> teamList;

    // KPI Metric Labels
    private JLabel lblKpiTeams;
    private JLabel lblKpiMatches;
    private JLabel lblKpiGoals;
    private JLabel lblKpiLeader;

    // Squad detail card labels
    private JLabel lblTeamDetailName;
    private JLabel lblTeamDetailCity;
    private JLabel lblTeamDetailCoach;

    public TournamentGUI() {
        this.tournament = new Tournament("UEFA Champions League", "2026-27");

        // Seed with sample data automatically so UI is vibrant and ready on open
        SampleDataLoader.populate(tournament);

        initializeFrame();
        buildUI();
        refreshAllViews();
    }

    private void initializeFrame() {
        setTitle("Football Tournament Pro — OOP Lab Mini Project (Java 17)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        // 1. Sidebar Navigation (Left)
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Right Side: Top Navigation Bar + Main Content Area
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(BG_MAIN);

        JPanel topBar = buildTopBar();
        rightWrapper.add(topBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_MAIN);

        // Add 5 distinct Web-like views
        contentPanel.add(buildStandingsView(), "STANDINGS");
        contentPanel.add(buildTeamsView(),     "TEAMS");
        contentPanel.add(buildFixturesView(),  "FIXTURES");
        contentPanel.add(buildScorersView(),   "SCORERS");
        contentPanel.add(buildDataView(),      "DATA");

        rightWrapper.add(contentPanel, BorderLayout.CENTER);
        add(rightWrapper, BorderLayout.CENTER);
    }

    // ========================================================
    // SIDEBAR NAVIGATION (Web Application Look & Feel)
    // ========================================================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Brand Logo & Title
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(22, 20, 20, 20));

        JLabel logo = new JLabel("⚽  TOURNAMENT PRO");
        logo.setFont(FONT_LOGO);
        logo.setForeground(TEXT_LIGHT);

        JLabel badge = new JLabel("   OOP LAB MINI PROJECT  •  JAVA 17");
        badge.setFont(FONT_SMALL);
        badge.setForeground(ACCENT_GREEN);

        brandPanel.add(logo, BorderLayout.NORTH);
        brandPanel.add(badge, BorderLayout.SOUTH);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Nav Links
        JPanel navMenu = new JPanel(new GridLayout(6, 1, 0, 6));
        navMenu.setOpaque(false);
        navMenu.setBorder(new EmptyBorder(10, 14, 10, 14));

        JButton btnStandings = createNavButton("📊   Dashboard & Standings", "STANDINGS");
        JButton btnTeams     = createNavButton("🛡️   Teams & Squads",        "TEAMS");
        JButton btnFixtures  = createNavButton("⚔️   Fixtures & Matches",    "FIXTURES");
        JButton btnScorers   = createNavButton("🏆   Top Scorers (Golden Boot)","SCORERS");
        JButton btnData      = createNavButton("💾   Save / Load Database",  "DATA");

        navMenu.add(btnStandings);
        navMenu.add(btnTeams);
        navMenu.add(btnFixtures);
        navMenu.add(btnScorers);
        navMenu.add(btnData);

        sidebar.add(navMenu, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 8));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 14, 20, 14));

        JButton btnQuickDemo = createActionButton("⚡ Load Demo Data", ACCENT_BLUE);
        btnQuickDemo.addActionListener(e -> {
            tournament = new Tournament("UEFA Champions League", "2026-27");
            SampleDataLoader.populate(tournament);
            refreshAllViews();
            showSuccessToast("Sample demo data reloaded with 4 elite teams and squads!");
        });

        JButton btnViva = createActionButton("💡 OOP Viva Inspector", ACCENT_GOLD);
        btnViva.addActionListener(e -> showVivaModal());

        footer.add(btnQuickDemo);
        footer.add(btnViva);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(BG_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BG_CARD);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_SIDEBAR);
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    // ========================================================
    // TOP NAVIGATION BAR
    // ========================================================

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 65));
        bar.setBackground(BG_MAIN);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Left: Tournament Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel title = new JLabel("UEFA CHAMPIONS LEAGUE 2026-27");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_LIGHT);

        lblHeaderFormat = new JLabel("  [ROUND-ROBIN]  ");
        lblHeaderFormat.setFont(FONT_SMALL);
        lblHeaderFormat.setForeground(ACCENT_GREEN);
        lblHeaderFormat.setBorder(new LineBorder(ACCENT_GREEN, 1, true));

        left.add(title);
        left.add(lblHeaderFormat);
        bar.add(left, BorderLayout.WEST);

        // Right: Live quick stats
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        lblHeaderStats = new JLabel("Teams: 4 | Matches: 0/0 | Goals: 0");
        lblHeaderStats.setFont(FONT_REGULAR);
        lblHeaderStats.setForeground(TEXT_MUTED);

        JButton btnAddQuick = createActionButton("+ Add Team", ACCENT_GREEN);
        btnAddQuick.addActionListener(e -> openAddTeamModal());

        right.add(lblHeaderStats);
        right.add(btnAddQuick);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ========================================================
    // VIEW 1: STANDINGS & DASHBOARD
    // ========================================================

    private JPanel buildStandingsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 24, 24, 24));

        // 1. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setPreferredSize(new Dimension(0, 95));

        lblKpiTeams   = new JLabel("0");
        lblKpiMatches = new JLabel("0 / 0");
        lblKpiGoals   = new JLabel("0");
        lblKpiLeader  = new JLabel("—");

        kpiRow.add(buildKpiCard("TOTAL TEAMS", lblKpiTeams, ACCENT_BLUE, "🛡️"));
        kpiRow.add(buildKpiCard("MATCHES PLAYED", lblKpiMatches, ACCENT_GOLD, "⚔️"));
        kpiRow.add(buildKpiCard("TOTAL GOALS", lblKpiGoals, ACCENT_GREEN, "⚽"));
        kpiRow.add(buildKpiCard("TABLE LEADER", lblKpiLeader, new Color(168, 85, 247), "👑"));

        panel.add(kpiRow, BorderLayout.NORTH);

        // 2. Standings Table Container
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(BG_CARD);
        tableCard.setBorder(new EmptyBorder(16, 18, 18, 18));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel tableTitle = new JLabel("Current Standings & Points Table (Win = 3 pts, Draw = 1 pt, Loss = 0 pts)");
        tableTitle.setFont(FONT_HEADER);
        tableTitle.setForeground(TEXT_LIGHT);
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        String[] cols = {"Rank", "Team Name", "City", "MP", "W", "D", "L", "GF", "GA", "GD", "Points"};
        standingsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(standingsModel);
        styleTable(table);

        // Custom Highlight for Points and Rank
        table.getColumnModel().getColumn(0).setCellRenderer(new RankCellRenderer());
        table.getColumnModel().getColumn(10).setCellRenderer(new PointsCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);

        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildKpiCard(String title, JLabel valueLabel, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout(8, 4));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblIcon, BorderLayout.EAST);

        valueLabel.setFont(FONT_STAT_NUM);
        valueLabel.setForeground(accent);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ========================================================
    // VIEW 2: TEAMS & SQUADS (Master-Detail Card View)
    // ========================================================

    private JPanel buildTeamsView() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Left Column: Team List & Actions
        JPanel leftListCard = new JPanel(new BorderLayout(0, 10));
        leftListCard.setPreferredSize(new Dimension(320, 0));
        leftListCard.setBackground(BG_CARD);
        leftListCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Registered Teams");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_LIGHT);
        leftListCard.add(title, BorderLayout.NORTH);

        teamListModel = new DefaultListModel<>();
        teamList = new JList<>(teamListModel);
        teamList.setBackground(BG_MAIN);
        teamList.setForeground(TEXT_LIGHT);
        teamList.setFont(FONT_BOLD);
        teamList.setSelectionBackground(ACCENT_BLUE);
        teamList.setSelectionForeground(TEXT_LIGHT);
        teamList.setFixedCellHeight(42);

        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedTeamView();
            }
        });

        JScrollPane listScroll = new JScrollPane(teamList);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        leftListCard.add(listScroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        JButton btnAdd = createActionButton("+ Add Team", ACCENT_GREEN);
        JButton btnDel = createActionButton("🗑 Delete", ACCENT_RED);

        btnAdd.addActionListener(e -> openAddTeamModal());
        btnDel.addActionListener(e -> deleteSelectedTeam());

        btnRow.add(btnAdd);
        btnRow.add(btnDel);
        leftListCard.add(btnRow, BorderLayout.SOUTH);

        panel.add(leftListCard, BorderLayout.WEST);

        // Right Column: Selected Team Details & Squad Table
        JPanel rightDetailCard = new JPanel(new BorderLayout(0, 14));
        rightDetailCard.setBackground(BG_CARD);
        rightDetailCard.setBorder(new EmptyBorder(16, 18, 18, 18));

        // Team Header Card
        JPanel teamHeader = new JPanel(new BorderLayout());
        teamHeader.setBackground(BG_MAIN);
        teamHeader.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setOpaque(false);
        lblTeamDetailName = new JLabel("Select a Team");
        lblTeamDetailName.setFont(FONT_TITLE);
        lblTeamDetailName.setForeground(TEXT_LIGHT);

        lblTeamDetailCity = new JLabel("City: —");
        lblTeamDetailCity.setFont(FONT_REGULAR);
        lblTeamDetailCity.setForeground(TEXT_MUTED);

        namePanel.add(lblTeamDetailName);
        namePanel.add(lblTeamDetailCity);

        lblTeamDetailCoach = new JLabel("Head Coach: —");
        lblTeamDetailCoach.setFont(FONT_REGULAR);
        lblTeamDetailCoach.setForeground(ACCENT_GOLD);

        teamHeader.add(namePanel, BorderLayout.WEST);
        teamHeader.add(lblTeamDetailCoach, BorderLayout.EAST);
        rightDetailCard.add(teamHeader, BorderLayout.NORTH);

        // Squad Table
        JPanel squadPanel = new JPanel(new BorderLayout(0, 8));
        squadPanel.setOpaque(false);

        JPanel squadTop = new JPanel(new BorderLayout());
        squadTop.setOpaque(false);
        JLabel squadLabel = new JLabel("Squad Roster & Player Stats");
        squadLabel.setFont(FONT_HEADER);
        squadLabel.setForeground(TEXT_LIGHT);

        JButton btnAddPlayer = createActionButton("+ Add Player to Squad", ACCENT_BLUE);
        btnAddPlayer.addActionListener(e -> openAddPlayerModal());

        squadTop.add(squadLabel, BorderLayout.WEST);
        squadTop.add(btnAddPlayer, BorderLayout.EAST);
        squadPanel.add(squadTop, BorderLayout.NORTH);

        String[] squadCols = {"Jersey #", "Player Name", "Age", "Position", "Goals Scored"};
        squadModel = new DefaultTableModel(squadCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable squadTable = new JTable(squadModel);
        styleTable(squadTable);

        JScrollPane squadScroll = new JScrollPane(squadTable);
        squadScroll.setBorder(BorderFactory.createEmptyBorder());
        squadScroll.getViewport().setBackground(BG_CARD);
        squadPanel.add(squadScroll, BorderLayout.CENTER);

        rightDetailCard.add(squadPanel, BorderLayout.CENTER);
        panel.add(rightDetailCard, BorderLayout.CENTER);

        return panel;
    }

    // ========================================================
    // VIEW 3: FIXTURES & MATCH CENTER
    // ========================================================

    private JPanel buildFixturesView() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Top Toolbar: Generator & Filters
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_CARD);
        toolbar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftTools.setOpaque(false);

        JLabel lblGen = new JLabel("Format:");
        lblGen.setFont(FONT_BOLD);
        lblGen.setForeground(TEXT_LIGHT);

        JComboBox<String> formatBox = new JComboBox<>(new String[]{"Round-Robin", "Single-Elimination Knockout"});
        formatBox.setBackground(BG_MAIN);
        formatBox.setForeground(TEXT_LIGHT);
        formatBox.setFont(FONT_REGULAR);

        JButton btnGenerate = createActionButton("⚡ Generate Fixtures", ACCENT_GREEN);
        btnGenerate.addActionListener(e -> {
            String choice = (String) formatBox.getSelectedItem();
            String code = (choice != null && choice.contains("Knockout")) ? "KO" : "RR";
            tournament.generateFixtures(code);
            refreshAllViews();
            showSuccessToast("Generated fixtures successfully using " + choice + "!");
        });

        leftTools.add(lblGen);
        leftTools.add(formatBox);
        leftTools.add(btnGenerate);
        toolbar.add(leftTools, BorderLayout.WEST);

        // Right side: Enter Result action
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightTools.setOpaque(false);

        JButton btnEnterScore = createActionButton("⚽ Enter Match Result", ACCENT_GOLD);
        btnEnterScore.addActionListener(e -> openEnterResultModal());

        rightTools.add(btnEnterScore);
        toolbar.add(rightTools, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);

        // Fixtures Table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(BG_CARD);
        tableCard.setBorder(new EmptyBorder(16, 18, 18, 18));

        String[] cols = {"Match ID", "Round", "Home Team", "Score", "Away Team", "Status", "Referee", "Outcome"};
        fixturesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable fixturesTable = new JTable(fixturesModel);
        styleTable(fixturesTable);
        fixturesTable.getColumnModel().getColumn(5).setCellRenderer(new MatchStatusCellRenderer());

        JScrollPane scroll = new JScrollPane(fixturesTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);

        tableCard.add(scroll, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ========================================================
    // VIEW 4: TOP SCORERS (Golden Boot)
    // ========================================================

    private JPanel buildScorersView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Header Title
        JLabel title = new JLabel("Golden Boot Leaderboard (Top Scorers)");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        // Scorers Table Container
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(BG_CARD);
        tableCard.setBorder(new EmptyBorder(16, 18, 18, 18));

        String[] cols = {"Rank", "Player Name", "Jersey #", "Team", "Position", "Total Goals"};
        scorersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable scorersTable = new JTable(scorersModel);
        styleTable(scorersTable);
        scorersTable.getColumnModel().getColumn(0).setCellRenderer(new RankCellRenderer());
        scorersTable.getColumnModel().getColumn(5).setCellRenderer(new PointsCellRenderer());

        JScrollPane scroll = new JScrollPane(scorersTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);

        tableCard.add(scroll, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ========================================================
    // VIEW 5: DATA STORAGE & SERIALIZATION
    // ========================================================

    private JPanel buildDataView() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Card 1: SQL Relational Database (JDBC)
        JPanel sqlCard = new JPanel(new GridLayout(5, 1, 0, 8));
        sqlCard.setPreferredSize(new Dimension(380, 240));
        sqlCard.setBackground(BG_CARD);
        sqlCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblSqlTitle = new JLabel("🗄️  SQL Database (JDBC)");
        lblSqlTitle.setFont(FONT_HEADER);
        lblSqlTitle.setForeground(TEXT_LIGHT);

        JLabel lblSqlDesc = new JLabel("Engine: " + DatabaseManager.getDbType().toUpperCase() + " | " + DatabaseManager.getDbUrl());
        lblSqlDesc.setFont(FONT_SMALL);
        lblSqlDesc.setForeground(ACCENT_GOLD);

        JLabel lblSqlInfo = new JLabel("Relational schema with ACID transactions (PreparedStatement).");
        lblSqlInfo.setFont(FONT_SMALL);
        lblSqlInfo.setForeground(TEXT_MUTED);

        JButton btnSqlSave = createActionButton("💾 Save to SQL Database", ACCENT_GREEN);
        btnSqlSave.addActionListener(e -> {
            boolean ok = DatabaseManager.saveTournament(tournament);
            if (ok) {
                showSuccessToast("Tournament successfully saved into SQL Database (" + DatabaseManager.getDbType() + ")!");
            } else {
                showErrorAlert("Failed to save to SQL Database. Check logs for details.");
            }
        });

        JButton btnSqlLoad = createActionButton("📂 Load from SQL Database", ACCENT_BLUE);
        btnSqlLoad.addActionListener(e -> {
            Tournament loaded = DatabaseManager.loadTournament();
            if (loaded != null) {
                this.tournament = loaded;
                refreshAllViews();
                showSuccessToast("Tournament loaded from SQL Database! (" + loaded.getTeamCount() + " teams)");
            } else {
                showErrorAlert("No tournament data found in SQL Database or connection failed.");
            }
        });

        sqlCard.add(lblSqlTitle);
        sqlCard.add(lblSqlDesc);
        sqlCard.add(lblSqlInfo);
        sqlCard.add(btnSqlSave);
        sqlCard.add(btnSqlLoad);

        // Card 2: Binary File Serialization
        JPanel saveCard = new JPanel(new GridLayout(4, 1, 0, 10));
        saveCard.setPreferredSize(new Dimension(340, 240));
        saveCard.setBackground(BG_CARD);
        saveCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblSaveTitle = new JLabel("💾  Binary Serialization (.dat)");
        lblSaveTitle.setFont(FONT_HEADER);
        lblSaveTitle.setForeground(TEXT_LIGHT);

        JLabel lblSaveDesc = new JLabel("Serializes all teams, players & match history to disk.");
        lblSaveDesc.setFont(FONT_SMALL);
        lblSaveDesc.setForeground(TEXT_MUTED);

        JTextField txtSaveFile = new JTextField("tournament_data.dat");
        txtSaveFile.setBackground(BG_MAIN);
        txtSaveFile.setForeground(TEXT_LIGHT);
        txtSaveFile.setCaretColor(TEXT_LIGHT);

        JButton btnSave = createActionButton("Save to File (.dat)", ACCENT_GREEN);
        btnSave.addActionListener(e -> {
            String fname = txtSaveFile.getText().trim();
            if (fname.isEmpty()) fname = "tournament_data.dat";
            boolean ok = FileManager.save(tournament, fname);
            if (ok) showSuccessToast("Tournament successfully serialized and saved to: " + fname);
            else showErrorAlert("Failed to save tournament to file.");
        });

        saveCard.add(lblSaveTitle);
        saveCard.add(lblSaveDesc);
        saveCard.add(txtSaveFile);
        saveCard.add(btnSave);

        // Card 3: Load Binary File
        JPanel loadCard = new JPanel(new GridLayout(4, 1, 0, 10));
        loadCard.setPreferredSize(new Dimension(340, 240));
        loadCard.setBackground(BG_CARD);
        loadCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblLoadTitle = new JLabel("📂  Load Binary File (.dat)");
        lblLoadTitle.setFont(FONT_HEADER);
        lblLoadTitle.setForeground(TEXT_LIGHT);

        JLabel lblLoadDesc = new JLabel("Restores tournament state from a serialized .dat file.");
        lblLoadDesc.setFont(FONT_SMALL);
        lblLoadDesc.setForeground(TEXT_MUTED);

        JTextField txtLoadFile = new JTextField("tournament_data.dat");
        txtLoadFile.setBackground(BG_MAIN);
        txtLoadFile.setForeground(TEXT_LIGHT);
        txtLoadFile.setCaretColor(TEXT_LIGHT);

        JButton btnLoad = createActionButton("Load from File (.dat)", ACCENT_BLUE);
        btnLoad.addActionListener(e -> {
            String fname = txtLoadFile.getText().trim();
            Tournament loaded = FileManager.load(fname);
            if (loaded != null) {
                this.tournament = loaded;
                refreshAllViews();
                showSuccessToast("Loaded: " + loaded.getName() + " (" + loaded.getTeamCount() + " teams)");
            } else {
                showErrorAlert("File '" + fname + "' not found or data is corrupt.");
            }
        });

        loadCard.add(lblLoadTitle);
        loadCard.add(lblLoadDesc);
        loadCard.add(txtLoadFile);
        loadCard.add(btnLoad);

        panel.add(sqlCard);
        panel.add(saveCard);
        panel.add(loadCard);
        return panel;
    }

    // ========================================================
    // MODAL DIALOGS (Input Forms with Exception Handling)
    // ========================================================

    private void openAddTeamModal() {
        JDialog dialog = createModalDialog("Register New Team", 380, 360);
        JPanel p = new JPanel(new GridLayout(8, 1, 0, 6));
        p.setOpaque(false);

        JTextField txtId   = createStyledTextField("Short ID (e.g. RMA, MCI)");
        JTextField txtName = createStyledTextField("Full Team Name (e.g. Real Madrid)");
        JTextField txtCity = createStyledTextField("Home City (e.g. Madrid)");
        JTextField txtCoach= createStyledTextField("Coach Name (e.g. Carlo Ancelotti)");
        JTextField txtNat  = createStyledTextField("Coach Nationality (e.g. Italian)");

        p.add(new JLabel("Team ID:"));
        p.add(txtId);
        p.add(new JLabel("Team Name:"));
        p.add(txtName);
        p.add(new JLabel("City:"));
        p.add(txtCity);
        p.add(new JLabel("Head Coach & Nationality (Optional):"));
        JPanel coachRow = new JPanel(new GridLayout(1, 2, 6, 0));
        coachRow.setOpaque(false);
        coachRow.add(txtCoach);
        coachRow.add(txtNat);
        p.add(coachRow);

        JButton btnSubmit = createActionButton("Create Team", ACCENT_GREEN);
        btnSubmit.addActionListener(e -> {
            String id   = txtId.getText().trim().toUpperCase();
            String name = txtName.getText().trim();
            String city = txtCity.getText().trim();

            if (id.isEmpty() || name.isEmpty() || city.isEmpty()) {
                showErrorAlert("Team ID, Name, and City are required!");
                return;
            }

            Team t = new Team(id, name, city);
            String cName = txtCoach.getText().trim();
            if (!cName.isEmpty()) {
                String cNat = txtNat.getText().trim().isEmpty() ? "Unknown" : txtNat.getText().trim();
                t.setCoach(new Coach("C_" + id, cName, 50, cNat, id));
            }

            // OOP CONCEPT: EXCEPTION HANDLING — catch DuplicateTeamException
            try {
                tournament.addTeam(t);
                refreshAllViews();
                dialog.dispose();
                showSuccessToast("Team '" + name + "' added successfully!");
            } catch (DuplicateTeamException ex) {
                showErrorAlert("DuplicateTeamException: " + ex.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(16, 18, 16, 18));
        wrapper.add(p, BorderLayout.CENTER);
        wrapper.add(btnSubmit, BorderLayout.SOUTH);

        dialog.getContentPane().add(wrapper);
        dialog.setVisible(true);
    }

    private void deleteSelectedTeam() {
        String selected = teamList.getSelectedValue();
        if (selected == null) {
            showErrorAlert("Please select a team from the list to delete.");
            return;
        }

        String teamId = selected.split(" - ")[0].trim();
        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete team [" + teamId + "]?\nAll associated matches will be removed.",
                "Confirm Team Deletion", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            // OOP CONCEPT: EXCEPTION HANDLING — catch TeamNotFoundException
            try {
                tournament.removeTeam(teamId);
                refreshAllViews();
                showSuccessToast("Team " + teamId + " deleted.");
            } catch (TeamNotFoundException ex) {
                showErrorAlert("TeamNotFoundException: " + ex.getMessage());
            }
        }
    }

    private void openAddPlayerModal() {
        String selected = teamList.getSelectedValue();
        if (selected == null) {
            showErrorAlert("Please select a team on the left before adding a player.");
            return;
        }
        String teamId = selected.split(" - ")[0].trim();

        JDialog dialog = createModalDialog("Add Player to " + teamId, 380, 360);
        JPanel p = new JPanel(new GridLayout(8, 1, 0, 6));
        p.setOpaque(false);

        JTextField txtName   = createStyledTextField("Player Name");
        JTextField txtAge    = createStyledTextField("Age (15-45)");
        JTextField txtJersey = createStyledTextField("Jersey # (1-99)");
        JComboBox<String> posBox = new JComboBox<>(new String[]{"Forward", "Midfielder", "Defender", "Goalkeeper"});
        posBox.setBackground(BG_MAIN);
        posBox.setForeground(TEXT_LIGHT);

        p.add(new JLabel("Player Name:"));
        p.add(txtName);
        p.add(new JLabel("Age:"));
        p.add(txtAge);
        p.add(new JLabel("Jersey Number:"));
        p.add(txtJersey);
        p.add(new JLabel("Position:"));
        p.add(posBox);

        JButton btnSubmit = createActionButton("Add Player to Squad", ACCENT_BLUE);
        btnSubmit.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                int age     = Integer.parseInt(txtAge.getText().trim());
                int jersey  = Integer.parseInt(txtJersey.getText().trim());
                String pos  = (String) posBox.getSelectedItem();

                if (name.isEmpty()) {
                    showErrorAlert("Player name cannot be blank!");
                    return;
                }

                // OOP CONCEPT: EXCEPTION HANDLING
                tournament.addPlayerToTeam(teamId, name, age, jersey, pos);
                refreshAllViews();
                dialog.dispose();
                showSuccessToast("Player " + name + " (#" + jersey + ") added to squad!");

            } catch (NumberFormatException nfe) {
                showErrorAlert("Age and Jersey Number must be valid integers!");
            } catch (TeamNotFoundException ex) {
                showErrorAlert("TeamNotFoundException: " + ex.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(16, 18, 16, 18));
        wrapper.add(p, BorderLayout.CENTER);
        wrapper.add(btnSubmit, BorderLayout.SOUTH);

        dialog.getContentPane().add(wrapper);
        dialog.setVisible(true);
    }

    private void openEnterResultModal() {
        List<Match> matches = tournament.getMatches();
        if (matches.isEmpty()) {
            showErrorAlert("No matches available! Generate fixtures first.");
            return;
        }

        JDialog dialog = createModalDialog("Enter Match Result", 460, 380);
        JPanel p = new JPanel(new GridLayout(6, 1, 0, 8));
        p.setOpaque(false);

        JComboBox<String> matchBox = new JComboBox<>();
        matchBox.setBackground(BG_MAIN);
        matchBox.setForeground(TEXT_LIGHT);
        for (Match m : matches) {
            matchBox.addItem(m.getMatchId() + ": " + m.getHomeTeamName() + " vs " + m.getAwayTeamName());
        }

        JTextField txtHomeScore = createStyledTextField("Home Team Goals (0-30)");
        JTextField txtAwayScore = createStyledTextField("Away Team Goals (0-30)");

        p.add(new JLabel("Select Fixture:"));
        p.add(matchBox);
        p.add(new JLabel("Home Score:"));
        p.add(txtHomeScore);
        p.add(new JLabel("Away Score:"));
        p.add(txtAwayScore);

        JButton btnSubmit = createActionButton("Record Result & Update Standings", ACCENT_GOLD);
        btnSubmit.addActionListener(e -> {
            String selectedItem = (String) matchBox.getSelectedItem();
            if (selectedItem == null) return;
            String matchId = selectedItem.split(":")[0].trim();

            try {
                int hScore = Integer.parseInt(txtHomeScore.getText().trim());
                int aScore = Integer.parseInt(txtAwayScore.getText().trim());

                // OOP CONCEPT: EXCEPTION HANDLING — catch InvalidScoreException & TeamNotFoundException
                tournament.enterResult(matchId, hScore, aScore);
                refreshAllViews();
                dialog.dispose();
                showSuccessToast("Result saved! Standings and Points Table updated.");

            } catch (NumberFormatException nfe) {
                showErrorAlert("Goals must be numeric values!");
            } catch (InvalidScoreException ise) {
                showErrorAlert("InvalidScoreException: " + ise.getMessage());
            } catch (TeamNotFoundException tnfe) {
                showErrorAlert("TeamNotFoundException: " + tnfe.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(16, 18, 16, 18));
        wrapper.add(p, BorderLayout.CENTER);
        wrapper.add(btnSubmit, BorderLayout.SOUTH);

        dialog.getContentPane().add(wrapper);
        dialog.setVisible(true);
    }

    private void showVivaModal() {
        JDialog dialog = createModalDialog("OOP Concepts Inspector (Viva Q&A)", 650, 480);
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 18, 16, 18));

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setBackground(BG_MAIN);
        txt.setForeground(TEXT_LIGHT);
        txt.setFont(new Font("Consolas", Font.PLAIN, 12));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setText(
            "=== LIVE OOP CONCEPTS DEMONSTRATION IN MEMORY ===\n\n" +
            "1. ABSTRACTION:\n" +
            "   - 'Person' (abstract class) defines abstract method 'displayInfo()'.\n" +
            "   - Subclasses (Player, Coach, Referee) must implement displayInfo().\n\n" +
            "2. INHERITANCE:\n" +
            "   - 'Player extends Person', 'Coach extends Person', 'Referee extends Person'.\n" +
            "   - Reuses base fields (id, name, age) via 'super(id, name, age)'.\n\n" +
            "3. ENCAPSULATION:\n" +
            "   - All fields in Person, Team, Match, Player are 'private'.\n" +
            "   - Access strictly governed by getters/setters and business methods.\n\n" +
            "4. POLYMORPHISM:\n" +
            "   - Interface 'FixtureGenerator' implemented by:\n" +
            "     * 'RoundRobinGenerator' (Circle algorithm)\n" +
            "     * 'KnockoutGenerator' (Single-elimination tree)\n" +
            "   - Overridden 'displayInfo()' executed via dynamic method dispatch.\n\n" +
            "5. EXCEPTION HANDLING (Checked Exceptions):\n" +
            "   - 'DuplicateTeamException' (Thrown when team ID/name already exists)\n" +
            "   - 'InvalidScoreException' (Thrown for negative, >30, or knockout draws)\n" +
            "   - 'TeamNotFoundException' (Thrown when queried team doesn't exist)\n\n" +
            "6. COLLECTIONS FRAMEWORK:\n" +
            "   - 'HashMap<String, Team>' in Tournament for O(1) team lookup.\n" +
            "   - 'ArrayList<Player>' in Team for squad roster.\n" +
            "   - 'ArrayList<Match>' in Tournament for fixtures.\n\n" +
            "7. FILE HANDLING & OBJECT SERIALIZATION:\n" +
            "   - 'FileManager.save()' / 'load()' serializes the complete Tournament\n" +
            "     object graph to 'tournament_data.dat' via ObjectOutputStream.\n"
        );

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JButton btnClose = createActionButton("Close Inspector", ACCENT_BLUE);
        btnClose.addActionListener(e -> dialog.dispose());

        p.add(scroll, BorderLayout.CENTER);
        p.add(btnClose, BorderLayout.SOUTH);
        dialog.getContentPane().add(p);
        dialog.setVisible(true);
    }

    // ========================================================
    // REFRESH & DATA SYNC
    // ========================================================

    private void refreshAllViews() {
        // 1. Refresh Standings Table
        standingsModel.setRowCount(0);
        List<Team> standings = tournament.getPointsTable();
        int rank = 1;
        for (Team t : standings) {
            standingsModel.addRow(new Object[]{
                    rank++, t.getName(), t.getCity(),
                    t.getMatchesPlayed(), t.getWins(), t.getDraws(), t.getLosses(),
                    t.getGoalsFor(), t.getGoalsAgainst(), t.getGoalDifference(), t.getPoints()
            });
        }

        // 2. Refresh Fixtures Table
        fixturesModel.setRowCount(0);
        List<Match> matches = tournament.getMatches();
        for (Match m : matches) {
            String scoreStr = m.isCompleted() ? (m.getHomeScore() + " - " + m.getAwayScore()) : "vs";
            String refStr   = (m.getReferee() != null) ? m.getReferee().getName() : "TBD";
            fixturesModel.addRow(new Object[]{
                    m.getMatchId(), m.getRoundLabel(), m.getHomeTeamName(),
                    scoreStr, m.getAwayTeamName(),
                    m.getStatus().name(), refStr, m.getOutcome()
            });
        }

        // 3. Refresh Team List
        teamListModel.clear();
        List<Team> allTeams = tournament.getAllTeams();
        for (Team t : allTeams) {
            teamListModel.addElement(t.getId() + " - " + t.getName());
        }
        if (!allTeams.isEmpty()) {
            teamList.setSelectedIndex(0);
            updateSelectedTeamView();
        }

        // 4. Refresh Top Scorers
        scorersModel.setRowCount(0);
        List<Player> topScorers = tournament.getTopScorers();
        int sRank = 1;
        for (Player p : topScorers) {
            scorersModel.addRow(new Object[]{
                    sRank++, p.getName(), "#" + p.getJerseyNumber(),
                    p.getTeamId(), p.getPosition(), p.getGoalsScored()
            });
        }

        // 5. Refresh KPI Metrics
        lblKpiTeams.setText(String.valueOf(tournament.getTeamCount()));
        lblKpiMatches.setText(tournament.getCompletedMatchCount() + " / " + matches.size());
        lblKpiGoals.setText(String.valueOf(tournament.getTotalGoalsScored()));
        lblKpiLeader.setText(!standings.isEmpty() ? standings.get(0).getName() : "—");

        lblHeaderStats.setText(String.format("Teams: %d | Matches: %d/%d | Goals: %d",
                tournament.getTeamCount(), tournament.getCompletedMatchCount(),
                matches.size(), tournament.getTotalGoalsScored()));

        lblHeaderFormat.setText("  [" + tournament.getFormat() + "]  ");
    }

    private void updateSelectedTeamView() {
        String selected = teamList.getSelectedValue();
        if (selected == null) return;

        String teamId = selected.split(" - ")[0].trim();
        try {
            Team team = tournament.findTeam(teamId);
            lblTeamDetailName.setText(team.getName() + " (" + team.getId() + ")");
            lblTeamDetailCity.setText("City: " + team.getCity() + " | Registered Squad: " + team.getPlayers().size());

            if (team.getCoach() != null) {
                lblTeamDetailCoach.setText("Head Coach: " + team.getCoach().getName() + " (" + team.getCoach().getNationality() + ")");
            } else {
                lblTeamDetailCoach.setText("Head Coach: [None Assigned]");
            }

            squadModel.setRowCount(0);
            for (Player p : team.getPlayers()) {
                squadModel.addRow(new Object[]{
                        "#" + p.getJerseyNumber(), p.getName(),
                        p.getAge(), p.getPosition(), p.getGoalsScored()
                });
            }
        } catch (TeamNotFoundException ignored) {}
    }

    // ========================================================
    // UI STYLING & HELPER UTILITIES
    // ========================================================

    private void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(36);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(BG_CARD_HOVER);
        table.setSelectionForeground(TEXT_LIGHT);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SIDEBAR);
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_BOLD);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1 && i != 2) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(BG_MAIN);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private JDialog createModalDialog(String title, int w, int h) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_CARD);
        return dialog;
    }

    private void showSuccessToast(String message) {
        JOptionPane.showMessageDialog(this, message, "Operation Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorAlert(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error / Exception", JOptionPane.ERROR_MESSAGE);
    }

    // ========================================================
    // CUSTOM TABLE CELL RENDERERS (Badges & Medals)
    // ========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TournamentGUI().setVisible(true);
        });
    }

    private static class RankCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(FONT_BOLD);

            if (row == 0) {
                lbl.setText("🥇 1");
                lbl.setForeground(ACCENT_GOLD);
            } else if (row == 1) {
                lbl.setText("🥈 2");
                lbl.setForeground(new Color(203, 213, 225));
            } else if (row == 2) {
                lbl.setText("🥉 3");
                lbl.setForeground(new Color(217, 119, 6));
            } else {
                lbl.setText(String.valueOf(row + 1));
                lbl.setForeground(TEXT_MUTED);
            }
            return lbl;
        }
    }

    private static class PointsCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(FONT_BOLD);
            lbl.setForeground(ACCENT_GREEN);
            return lbl;
        }
    }

    private static class MatchStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(FONT_SMALL);

            String status = (value != null) ? value.toString() : "";
            if ("COMPLETED".equalsIgnoreCase(status)) {
                lbl.setText("✔ COMPLETED");
                lbl.setForeground(ACCENT_GREEN);
            } else {
                lbl.setText("⏳ SCHEDULED");
                lbl.setForeground(ACCENT_GOLD);
            }
            return lbl;
        }
    }
}

