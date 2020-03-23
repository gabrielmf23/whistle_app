package com.example.whistle.whistlewithtabs.Objects;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Team implements Serializable {

    private int team;
    private String teamName;
    private int Championship;

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        String name;
        try {
            byte[] bytes = teamName.getBytes("ISO-8859-1");
            name = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            name = teamName;
            e.printStackTrace();
        }
        this.teamName = name;
    }

    public int getChampionship() {
        return Championship;
    }

    public void setChampionship(int championship) {
        Championship = championship;
    }
}
