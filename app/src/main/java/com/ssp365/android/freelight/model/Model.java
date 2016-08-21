package com.ssp365.android.freelight.model;

import android.util.Log;

import java.io.Serializable;

public class Model implements Serializable {

    //编号
    private int model_no;
    //名称
    private String model_name;
    //名称拼音首字母
    private String model_name_py;
    //图片名称
    private String model_pic;
    //模式的计时次数
    private int model_count;
    //模式的计时点个数
    private int model_point_count;
    //模式的距离
    private int model_length;
    //模式的分段计时标志位
    private int model_fdji;
    //运动员选择
    private boolean selected;

    public Model(int model_no, String model_name, String model_name_py, String model_pic, int model_count,
                 int model_point_count, int model_length, int model_fdji) {
        super();
        this.setModel_no(model_no);
        this.setModel_name(model_name);
        this.setModel_name_py(model_name_py);
        this.setModel_pic(model_pic);
        this.setModel_count(model_count);
        this.setModel_point_count(model_point_count);
        this.setModel_length(model_length);
        this.setModel_fdji(model_fdji);
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
     * @return the model_name_py
     */
    public String getModel_name_py() {
        return model_name_py;
    }

    /**
     * @param model_name_py the model_name_py to set
     */
    public void setModel_name_py(String model_name_py) {
        this.model_name_py = model_name_py;
    }

    /**
     * @return the model_pic
     */
    public String getModel_pic() {
        return model_pic;
    }

    /**
     * @param model_pic the model_pic to set
     */
    public void setModel_pic(String model_pic) {
        this.model_pic = model_pic;
    }

    /**
     * @return the model_count
     */
    public int getModel_count() {
        return model_count;
    }

    /**
     * @param model_count the model_count to set
     */
    public void setModel_count(int model_count) {
        this.model_count = model_count;
    }

    /**
     * @return the model_point_count
     */
    public int getModel_point_count() {
        return model_point_count;
    }

    /**
     * @param model_point_count the model_point_count to set
     */
    public void setModel_point_count(int model_point_count) {
        this.model_point_count = model_point_count;
    }

    /**
     * @return the model_length
     */
    public int getModel_length() {
        return model_length;
    }

    /**
     * @param model_length the model_length to set
     */
    public void setModel_length(int model_length) {
        this.model_length = model_length;
    }

    /**
     * @return the model_fdji
     */
    public int getModel_fdji() {
        return model_fdji;
    }

    /**
     * @param model_fdji the model_fdji to set
     */
    public void setModel_fdji(int model_fdji) {
        this.model_fdji = model_fdji;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        Log.i("EEEEEEEEEEEEE", "setSelected:" + selected);
        this.selected = selected;
    }

}
