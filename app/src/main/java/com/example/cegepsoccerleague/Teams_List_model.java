package com.example.cegepsoccerleague;

public class Teams_List_model {
    String team_id, team_name, team_icon, team_manager_id,team_manager_name, team_manager_contact, team_manager_email, league_id;

    public Teams_List_model(String team_id, String team_name, String team_icon, String team_manager_id, String team_manager_name, String team_manager_contact,String team_manager_email, String league_id) {
        this.team_id = team_id;
        this.team_name = team_name;
        this.team_icon = team_icon;
        this.team_manager_id = team_manager_id;
        this.team_manager_name = team_manager_name;
        this.team_manager_contact = team_manager_contact;
        this.team_manager_email = team_manager_email;
        this.league_id = league_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public String getTeam_icon() {
        return team_icon;
    }

    public String getTeam_manager_id() {
        return team_manager_id;
    }

    public String getTeam_manager_name() {
        return team_manager_name;
    }

    public String getTeam_manager_contact() {
        return team_manager_contact;
    }

    public String getTeam_manager_email() {
        return team_manager_email;
    }

    public String getLeague_id() {
        return league_id;
    }
}
