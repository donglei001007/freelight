package com.ssp365.android.freelight.model;

import android.util.Log;

import java.io.Serializable;

public class Sporter implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //运动员编号
    private int sporter_no;

    //运动员姓名
    private String sporter_name;

    //运动员姓名首字母
    private String sporter_name_py;

    //运动员性别
    private String sporter_xingbie;

    //运动员生日(2011-01-01)
    private String sporter_birthday;

    //运动员所在队
    private int sporter_team_no;

    //运动员身高
    private String sporter_shengao;

    //运动员体重
    private String sporter_tizhong;

    //运动员选择
    private boolean selected;


    public Sporter() {
    }

    public Sporter(int sporter_no, String sporter_name, String sporter_name_py,
                   String sporter_xingbie, String sporter_birthday, int sporter_team_no,
                   String sporter_shengao, String sporter_tizhong) {
        super();
        this.sporter_no = sporter_no;
        this.sporter_name = sporter_name;
        this.sporter_name_py = sporter_name_py;
        this.sporter_xingbie = sporter_xingbie;
        this.sporter_birthday = sporter_birthday;
        this.sporter_team_no = sporter_team_no;
        this.sporter_shengao = sporter_shengao;
        this.sporter_tizhong = sporter_tizhong;
    }

    /**
     * @return the sporter_no
     */
    public int getSporter_no() {
        return sporter_no;
    }

    /**
     * @param sporter_no the sporter_no to set
     */
    public void setSporter_no(int sporter_no) {
        this.sporter_no = sporter_no;
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
     * @return the sporter_name_py
     */
    public String getSporter_name_py() {
        return sporter_name_py;
    }

    /**
     * @param sporter_name_py the sporter_name_py to set
     */
    public void setSporter_name_py(String sporter_name_py) {
        this.sporter_name_py = sporter_name_py;
    }

    /**
     * @return the sporter_xingbie
     */
    public String getSporter_xingbie() {
        return sporter_xingbie;
    }

    /**
     * @param sporter_xingbie the sporter_xingbie to set
     */
    public void setSporter_xingbie(String sporter_xingbie) {
        this.sporter_xingbie = sporter_xingbie;
    }

    /**
     * @return the sporter_birthday
     */
    public String getSporter_birthday() {
        return sporter_birthday;
    }

    /**
     * @param sporter_birthday the sporter_birthday to set
     */
    public void setSporter_birthday(String sporter_birthday) {
        this.sporter_birthday = sporter_birthday;
    }

    /**
     * @return the sporter_team_no
     */
    public int getSporter_team_no() {
        return sporter_team_no;
    }

    /**
     * @param sporter_team_no the sporter_team_no to set
     */
    public void setSporter_team_no(int sporter_team_no) {
        this.sporter_team_no = sporter_team_no;
    }

    /**
     * @return the sporter_shengao
     */
    public String getSporter_shengao() {
        return sporter_shengao;
    }

    /**
     * @param sporter_shengao the sporter_shengao to set
     */
    public void setSporter_shengao(String sporter_shengao) {
        this.sporter_shengao = sporter_shengao;
    }

    /**
     * @return the sporter_tizhong
     */
    public String getSporter_tizhong() {
        return sporter_tizhong;
    }

    /**
     * @param sporter_tizhong the sporter_tizhong to set
     */
    public void setSporter_tizhong(String sporter_tizhong) {
        this.sporter_tizhong = sporter_tizhong;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        Log.i("EEEEEEEEEEEEE", "setSelected:" + selected);
        this.selected = selected;
    }

}
