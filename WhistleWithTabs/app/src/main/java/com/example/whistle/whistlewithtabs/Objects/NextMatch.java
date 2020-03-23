package com.example.whistle.whistlewithtabs.Objects;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class NextMatch implements Serializable {

    private int RefereeID;
    private String RefereeName;
    private int SelectedTeam;
    private String SelectedTeamName;
    private int AgainstTeam;
    private String AgainstTeamName;
    private String FieldControl;
    private String MatchDate;
    private int Championship;

    public int getReferee() {
        return RefereeID;
    }

    public void setReferee(int referee) {
        RefereeID = referee;
    }

    public String getRefereeName() {
        return RefereeName;
    }

    public void setRefereeName(String refereeName) {
        String name;
        try {
            byte[] bytes = refereeName.getBytes("ISO-8859-1");
            name = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            name = refereeName;
            e.printStackTrace();
        }
        RefereeName = name;
    }

    public int getSelectedTeam() {
        return SelectedTeam;
    }

    public void setSelectedTeam(int selectedTeam) {
        SelectedTeam = selectedTeam;
    }

    public int getAgainstTeam() {
        return AgainstTeam;
    }

    public void setAgainstTeam(int againstTeam) {
        AgainstTeam = againstTeam;
    }

    public String getFieldControl() {
        return FieldControl;
    }

    public void setFieldControl(String fieldControl) {
        FieldControl = fieldControl;
    }

    public String getMatchDate() {
        return MatchDate;
    }

    public void setMatchDate(String matchDate) {
        String year = matchDate.substring(0, 4);
        String month = matchDate.substring(5, 7);
        String day = matchDate.substring(8, 10);

        //Check in the settings the selected date format
        //MatchDate = month + "/" + day + "/" + year;
        MatchDate = day + "/" + month + "/" + year;
    }

    public String getSelectedTeamName() {
        return SelectedTeamName;
    }

    public void setSelectedTeamName(String selectedTeamName) {
        String name;
        try {
            byte[] bytes = selectedTeamName.getBytes("ISO-8859-1");
            name = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            name = selectedTeamName;
            e.printStackTrace();
        }
        SelectedTeamName = name;
    }

    public String getAgainstTeamName() {
        return AgainstTeamName;
    }

    public void setAgainstTeamName(String againstTeamName) {
        String name;
        try {
            byte[] bytes = againstTeamName.getBytes("ISO-8859-1");
            name = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            name = againstTeamName;
            e.printStackTrace();
        }
        AgainstTeamName = name;
    }

    public int getChampionship() {
        return Championship;
    }

    public void setChampionship(int championship) {
        Championship = championship;
    }
}
