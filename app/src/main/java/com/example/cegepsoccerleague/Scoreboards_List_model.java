package com.example.cegepsoccerleague;

public class Scoreboards_List_model {
    String match_id,match_date,
            team1_id,team1_icon,team1_name,
            team2_id,team2_icon,team2_name,
            team1_goals, team1_fouls, team1_freeKicks, team1_corners, team1_goalSaved,
            team2_goals, team2_fouls, team2_freeKicks, team2_corners, team2_goalSaved;

    public Scoreboards_List_model(String match_id, String match_date, String team1_id, String team1_icon, String team1_name, String team2_id, String team2_icon, String team2_name, String team1_goals, String team1_fouls, String team1_freeKicks, String team1_corners, String team1_goalSaved, String team2_goals, String team2_fouls, String team2_freeKicks, String team2_corners, String team2_goalSaved) {
        this.match_id = match_id;
        this.match_date = match_date;
        this.team1_id = team1_id;
        this.team1_icon = team1_icon;
        this.team1_name = team1_name;
        this.team2_id = team2_id;
        this.team2_icon = team2_icon;
        this.team2_name = team2_name;
        this.team1_goals = team1_goals;
        this.team1_fouls = team1_fouls;
        this.team1_freeKicks = team1_freeKicks;
        this.team1_corners = team1_corners;
        this.team1_goalSaved = team1_goalSaved;
        this.team2_goals = team2_goals;
        this.team2_fouls = team2_fouls;
        this.team2_freeKicks = team2_freeKicks;
        this.team2_corners = team2_corners;
        this.team2_goalSaved = team2_goalSaved;
    }

    public String getMatch_id() {
        return match_id;
    }

    public String getMatch_date() {
        return match_date;
    }

    public String getTeam1_id() {
        return team1_id;
    }

    public String getTeam1_icon() {
        return team1_icon;
    }

    public String getTeam1_name() {
        return team1_name;
    }

    public String getTeam2_id() {
        return team2_id;
    }

    public String getTeam2_icon() {
        return team2_icon;
    }

    public String getTeam2_name() {
        return team2_name;
    }

    public String getTeam1_goals() {
        return team1_goals;
    }

    public String getTeam1_fouls() {
        return team1_fouls;
    }

    public String getTeam1_freeKicks() {
        return team1_freeKicks;
    }

    public String getTeam1_corners() {
        return team1_corners;
    }

    public String getTeam1_goalSaved() {
        return team1_goalSaved;
    }

    public String getTeam2_goals() {
        return team2_goals;
    }

    public String getTeam2_fouls() {
        return team2_fouls;
    }

    public String getTeam2_freeKicks() {
        return team2_freeKicks;
    }

    public String getTeam2_corners() {
        return team2_corners;
    }

    public String getTeam2_goalSaved() {
        return team2_goalSaved;
    }
}
