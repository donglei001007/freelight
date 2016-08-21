package com.ssp365.android.freelight.model;

import android.util.Log;

import java.util.ArrayList;

public class Chenji {

    // Debugging
    private static final String TAG = "Chenji";
    //编号
    private int chenji_no;
    //测试日
    private String chenji_day;
    //运动员编号
    private int sporter_no;
    //运动员姓名
    private String sporter_name;
    //模式编号
    private int model_no;
    //模式总距离（单位：米）
    private int model_total_length;
    //总时间（单位：秒，保留两位小数）
    private double model_total_time;
    //平均速度（单位：米/秒，保留两位小数）
    private double model_total_speed;
    //成绩详细
    private ArrayList<ChenjiDetail> chenji_detail;

    public Chenji() {
    }

    public Chenji(int chenji_no, String chenji_day, int sporter_no, int model_no, int model_total_length, double model_total_time, double model_total_speed) {
        super();
        this.setChenji_no(chenji_no);
        this.setChenji_day(chenji_day);
        this.setSporter_no(sporter_no);
        this.setModel_no(model_no);
        this.setModel_total_length(model_total_length);
        this.setModel_total_time(model_total_time);
        this.setModel_total_speed(model_total_speed);
    }

    public Chenji(int chenji_no, String chenji_day, int sporter_no, String sporter_name, int model_no, int model_total_length, double model_total_time, double model_total_speed) {
        super();
        this.setChenji_no(chenji_no);
        this.setChenji_day(chenji_day);
        this.setSporter_no(sporter_no);
        this.setSporter_name(sporter_name);
        this.setModel_no(model_no);
        this.setModel_total_length(model_total_length);
        this.setModel_total_time(model_total_time);
        this.setModel_total_speed(model_total_speed);
    }

    public Chenji(int chenji_no, String chenji_day, int sporter_no, String sporter_name, int model_no, int model_total_length, double model_total_time, double model_total_speed, ArrayList<ChenjiDetail> chenji_detail) {
        super();
        this.setChenji_no(chenji_no);
        this.setChenji_day(chenji_day);
        this.setSporter_no(sporter_no);
        this.setSporter_name(sporter_name);
        this.setModel_no(model_no);
        this.setModel_total_length(model_total_length);
        this.setModel_total_time(model_total_time);
        this.setModel_total_speed(model_total_speed);
        this.setChenji_detail(chenji_detail);
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
     * @return the model_no
     */
    public int getModel_no() {
        return model_no;
    }

    /**
     * @param model_no the model_no to set
     */
    public void setModel_no(int model_no) {
        this.model_no = model_no;
    }

    /**
     * @return the model_total_length
     */
    public int getModel_total_length() {
        return model_total_length;
    }

    /**
     * @param model_total_length the model_total_length to set
     */
    public void setModel_total_length(int model_total_length) {
        this.model_total_length = model_total_length;
    }

    /**
     * @return the model_total_time
     */
    public double getModel_total_time() {
        return model_total_time;
    }

    /**
     * @param model_total_time the model_total_time to set
     */
    public void setModel_total_time(double model_total_time) {
        this.model_total_time = model_total_time;
    }

    /**
     * @return the model_total_speed
     */
    public double getModel_total_speed() {
        return model_total_speed;
    }

    /**
     * @param model_total_speed the model_total_speed to set
     */
    public void setModel_total_speed(double model_total_speed) {
        this.model_total_speed = model_total_speed;
    }

    /**
     * @return the chenji_detail
     */
    public ArrayList<ChenjiDetail> getChenji_detail() {
        return chenji_detail;
    }

    /**
     * @param chenji_detail the chenji_detail to set
     */
    public void setChenji_detail(ArrayList<ChenjiDetail> chenji_detail) {
        this.chenji_detail = chenji_detail;
    }

    public void println() {
        Log.i(TAG, "chenji_no:" + chenji_no + " chenji_day:" + chenji_day + " sporter_no:" + sporter_no +
                " sporter_name:" + sporter_name + " model_no:" + model_no + " model_total_length:" + model_total_length +
                " model_total_time:" + model_total_time + " model_total_speed:" + model_total_speed);
        if (chenji_detail != null) {
            for (int i = 0; i < chenji_detail.size(); i++) {
                Log.i(TAG, "chenji_detail[" + i + "]:" + chenji_detail.get(i).getModel_sub_speed());
            }
        }
    }


}
