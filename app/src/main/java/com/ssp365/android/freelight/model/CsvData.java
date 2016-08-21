package com.ssp365.android.freelight.model;

public class CsvData {

    //模式名称
    private String model_name;
    //队伍名称
    private String team_name;
    //运动员姓名
    private String sporter_name;
    //成绩编号
    private int chenji_no;
    //测试时间
    private String chenji_day;
    //测试平均成绩
    private String model_total_speed;
    //测试详细成绩
    private String model_sub_speed;
    //模式编号
    private String model_no;
    //模式子项编号
    private String model_sub_no;
    //队伍编号
    private String sporter_team_no;
    //运动员编号
    private String sporter_no;


    public CsvData() {
    }

    public CsvData(String model_name, String team_name, String sporter_name, int chenji_no, String chenji_day, String model_total_speed,
                   String model_sub_speed, String model_no, String model_sub_no, String sporter_team_no, String sporter_no) {
        super();
        this.setModel_name(model_name);
        this.setTeam_name(team_name);
        this.setSporter_name(sporter_name);
        this.setChenji_no(chenji_no);
        this.setChenji_day(chenji_day);
        this.setModel_total_speed(model_total_speed);
        this.setModel_sub_speed(model_sub_speed);
        this.setModel_no(model_no);
        this.setModel_sub_no(model_sub_no);
        this.setSporter_team_no(sporter_team_no);
        this.setSporter_no(sporter_no);
    }

    /**
     * @return the model_name
     */
    public String getModel_name() {
        return model_name;
    }

    /**
     * @param model_name the model_name to set
     */
    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    /**
     * @return the team_name
     */
    public String getTeam_name() {
        return team_name;
    }

    /**
     * @param team_name the team_name to set
     */
    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    /**
     * @return the sporter_name
     */
    public String getSporter_name() {
        return sporter_name;
    }

    /**
     * @param sporter_name the sporter_name to set
     */
    public void setSporter_name(String sporter_name) {
        this.sporter_name = sporter_name;
    }

    /**
     * @return the chenji_no
     */
    public int getChenji_no() {
        return chenji_no;
    }

    /**
     * @param chenji_no the chenji_no to set
     */
    public void setChenji_no(int chenji_no) {
        this.chenji_no = chenji_no;
    }

    /**
     * @return the chenji_day
     */
    public String getChenji_day() {
        return chenji_day;
    }

    /**
     * @param chenji_day the chenji_day to set
     */
    public void setChenji_day(String chenji_day) {
        this.chenji_day = chenji_day;
    }

    /**
     * @return the model_total_speed
     */
    public String getModel_total_speed() {
        return model_total_speed;
    }

    /**
     * @param model_total_speed the model_total_speed to set
     */
    public void setModel_total_speed(String model_total_speed) {
        this.model_total_speed = model_total_speed;
    }

    /**
     * @return the model_sub_speed
     */
    public String getModel_sub_speed() {
        return model_sub_speed;
    }

    /**
     * @param model_sub_speed the model_sub_speed to set
     */
    public void setModel_sub_speed(String model_sub_speed) {
        this.model_sub_speed = model_sub_speed;
    }

    /**
     * @return the model_no
     */
    public String getModel_no() {
        return model_no;
    }

    /**
     * @param model_no the model_no to set
     */
    public void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    /**
     * @return the model_sub_no
     */
    public String getModel_sub_no() {
        return model_sub_no;
    }

    /**
     * @param model_sub_no the model_sub_no to set
     */
    public void setModel_sub_no(String model_sub_no) {
        this.model_sub_no = model_sub_no;
    }

    /**
     * @return the sporter_team_no
     */
    public String getSporter_team_no() {
        return sporter_team_no;
    }

    /**
     * @param sporter_team_no the sporter_team_no to set
     */
    public void setSporter_team_no(String sporter_team_no) {
        this.sporter_team_no = sporter_team_no;
    }

    /**
     * @return the sporter_no
     */
    public String getSporter_no() {
        return sporter_no;
    }

    /**
     * @param sporter_no the sporter_no to set
     */
    public void setSporter_no(String sporter_no) {
        this.sporter_no = sporter_no;
    }


}
