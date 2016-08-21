package com.ssp365.android.freelight.model;

public class ChenjiDetail {

    //编号
    private int chenji_no;
    //计时点编号
    private int model_sub_no;
    //计时区间距离（单位：米）
    private int model_sub_length;
    //计时区间时间（单位：秒，保留两位小数）
    private double model_sub_time;
    //计时区间速度（单位：米/秒，保留两位小数）
    private double model_sub_speed;

    public ChenjiDetail() {
    }

    public ChenjiDetail(int chenji_no, int model_sub_no, int model_sub_length, double model_sub_time, double model_sub_speed) {
        super();
        this.setChenji_no(chenji_no);
        this.setModel_sub_no(model_sub_no);
        this.setModel_sub_length(model_sub_length);
        this.setModel_sub_time(model_sub_time);
        this.setModel_sub_speed(model_sub_speed);
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
     * @return the model_sub_no
     */
    public int getModel_sub_no() {
        return model_sub_no;
    }

    /**
     * @param model_sub_no the model_sub_no to set
     */
    public void setModel_sub_no(int model_sub_no) {
        this.model_sub_no = model_sub_no;
    }

    /**
     * @return the model_sub_length
     */
    public int getModel_sub_length() {
        return model_sub_length;
    }

    /**
     * @param model_sub_length the model_sub_length to set
     */
    public void setModel_sub_length(int model_sub_length) {
        this.model_sub_length = model_sub_length;
    }

    /**
     * @return the model_sub_time
     */
    public double getModel_sub_time() {
        return model_sub_time;
    }

    /**
     * @param model_sub_time the model_sub_time to set
     */
    public void setModel_sub_time(double model_sub_time) {
        this.model_sub_time = model_sub_time;
    }

    /**
     * @return the model_sub_speed
     */
    public double getModel_sub_speed() {
        return model_sub_speed;
    }

    /**
     * @param model_sub_speed the model_sub_speed to set
     */
    public void setModel_sub_speed(double model_sub_speed) {
        this.model_sub_speed = model_sub_speed;
    }


}
