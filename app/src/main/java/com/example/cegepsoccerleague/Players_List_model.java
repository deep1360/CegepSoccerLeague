package com.example.cegepsoccerleague;

public class Players_List_model {
    String player_id,player_first_name, player_last_name, plaer_age, player_position, player_icon, team_id;

    public Players_List_model(String player_id, String player_first_name, String player_last_name, String plaer_age, String player_position, String player_icon, String team_id) {
        this.player_id = player_id;
        this.player_first_name = player_first_name;
        this.player_last_name = player_last_name;
        this.plaer_age = plaer_age;
        this.player_position = player_position;
        this.player_icon = player_icon;
        this.team_id = team_id;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public String getPlayer_first_name() {
        return player_first_name;
    }

    public String getPlayer_last_name() {
        return player_last_name;
    }

    public String getPlaer_age() {
        return plaer_age;
    }

    public String getPlayer_position() {
        return player_position;
    }

    public String getPlayer_icon() {
        return player_icon;
    }

    public String getTeam_id() {
        return team_id;
    }
}
