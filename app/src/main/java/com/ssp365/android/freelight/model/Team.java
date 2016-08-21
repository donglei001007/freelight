package com.ssp365.android.freelight.model;

public class Team {
    //队伍编号
    private int team_no;

    //队伍名称
    private String team_name;

    //队伍名称拼音首字母
    private String team_name_py;

    public Team(int team_no, String team_name, String team_name_py) {
        super();
        this.team_no = team_no;
        this.team_name = team_name;
        this.team_name_py = team_name_py;
    }

    public int getTeam_no() {
        return team_no;
    }


    public void setTeam_no(int team_no) {
        this.team_no = team_no;
    }


    public String getTeam_name() {
        return team_name;
    }


    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_name_py() {
        return team_name_py;
    }

    public void setTeam_name_py(String team_name_py) {
        this.team_name_py = team_name_py;
    }

}
