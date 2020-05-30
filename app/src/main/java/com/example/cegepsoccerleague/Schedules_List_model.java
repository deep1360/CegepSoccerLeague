package com.example.cegepsoccerleague;

public class Schedules_List_model {
    String match_id,match_location,match_date,match_time,league_id,team1_id,team1_name,team1_icon,team2_id,team2_name,team2_icon;

    public Schedules_List_model(String match_id, String match_location, String match_date, String match_time, String league_id, String team1_id, String team1_name, String team1_icon, String team2_id, String team2_name, String team2_icon) {
        this.match_id = match_id;
        this.match_location = match_location;
        this.match_date = match_date;
        this.match_time = match_time;
        this.league_id = league_id;
        this.team1_id = team1_id;
        this.team1_name = team1_name;
        this.team1_icon = team1_icon;
        this.team2_id = team2_id;
        this.team2_name = team2_name;
        this.team2_icon = team2_icon;
    }

    public String getMatch_id() {
        return match_id;
    }

    public String getMatch_location() {
        return match_location;
    }

    public String getMatch_date() {
        return match_date;
    }

    public String getMatch_time() {
        return match_time;
    }

    public String getLeague_id() {
        return league_id;
    }

    public String getTeam1_id() {
        return team1_id;
    }

    public String getTeam1_name() {
        return team1_name;
    }

    public String getTeam1_icon() {
        return team1_icon;
    }

    public String getTeam2_id() {
        return team2_id;
    }

    public String getTeam2_name() {
        return team2_name;
    }

    public String getTeam2_icon() {
        return team2_icon;
    }
}
