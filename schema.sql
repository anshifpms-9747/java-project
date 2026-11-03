-- ============================================================
-- FOOTBALL TOURNAMENT MANAGEMENT SYSTEM - SQL SCHEMA
-- Database Engine: SQLite / MySQL Compatible
-- Demonstrates Relational Modeling, Primary Keys, Foreign Keys,
-- and ACID Integrity for Java OOP Mini Project.
-- ============================================================

-- 1. Tournament Metadata Table
CREATE TABLE IF NOT EXISTS tournament_meta (
    tournament_name VARCHAR(100) NOT NULL,
    season VARCHAR(50) NOT NULL,
    fixture_format VARCHAR(20) DEFAULT 'RR',
    PRIMARY KEY (tournament_name, season)
);

-- 2. Teams Table (Stores Club Details, Coach Information, and Standings)
CREATE TABLE IF NOT EXISTS teams (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    coach_id VARCHAR(20),
    coach_name VARCHAR(100),
    coach_age INT,
    coach_nationality VARCHAR(50),
    matches_played INT DEFAULT 0,
    wins INT DEFAULT 0,
    draws INT DEFAULT 0,
    losses INT DEFAULT 0,
    goals_for INT DEFAULT 0,
    goals_against INT DEFAULT 0,
    points INT DEFAULT 0
);

-- 3. Players Table (Squad Members linked to Teams via Foreign Key)
CREATE TABLE IF NOT EXISTS players (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    jersey_number INT NOT NULL,
    position VARCHAR(30) NOT NULL,
    goals_scored INT DEFAULT 0,
    team_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    UNIQUE (team_id, jersey_number)
);

-- 4. Referees Table (Independent Match Officials)
CREATE TABLE IF NOT EXISTS referees (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    certification VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL
);

-- 5. Matches Table (Fixtures, Results, and Knockout Tree Links)
CREATE TABLE IF NOT EXISTS matches (
    match_id VARCHAR(20) PRIMARY KEY,
    home_team_id VARCHAR(10) NOT NULL,
    home_team_name VARCHAR(100) NOT NULL,
    away_team_id VARCHAR(10) NOT NULL,
    away_team_name VARCHAR(100) NOT NULL,
    home_score INT DEFAULT 0,
    away_score INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    round_number INT NOT NULL,
    round_label VARCHAR(50),
    scheduled_time VARCHAR(50),
    referee_name VARCHAR(100),
    next_match_id VARCHAR(20),
    is_home_for_next INT DEFAULT 1
);
