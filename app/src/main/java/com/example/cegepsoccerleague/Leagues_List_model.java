package com.example.cegepsoccerleague;

public class Leagues_List_model {
    String league_id,league_name,league_icon,league_manager_id;

    public Leagues_List_model(String league_id, String league_name, String league_icon, String league_manager_id) {
        this.league_id = league_id;
        this.league_name = league_name;
        this.league_icon = league_icon;
        this.league_manager_id = league_manager_id;
    }

    public String getLeague_id() {
        return league_id;
    }

    public String getLeague_name() {
        return league_name;
    }

    public String getLeague_icon() {
        return league_icon;
    }

    public String getLeague_manager_id() {
        return league_manager_id;
    }
}
