package com.ssp365.android.freelight.model;

public class ModelDetail {

    //编号
    private int model_no;
    //计时点编号(100开始，起始点为0,为了对应中间追加点，初始每个点之间间隔100)
    private int model_sub_no;
    //前计时点编号
    private int model_pre_no;
    //计时区间距
    private int model_sub_length;
    //和前计时点的位置关系
    //0:和前点同一位置，1:0点方向，2:1.5点方向，3:3点方向，5:4.5点方向，
    //5:6点方向,6:7.5点方向，7:9点方向，8:11.5点方向
    private String model_sub_position_type;
    //计时点类别(0:压控开关，1:激光开关)
    private String model_sub_check_type;


    public ModelDetail(int model_no, int model_sub_no, int model_pre_no, int model_sub_length, String model_sub_position_type, String model_sub_check_type) {
        super();
        this.setModel_no(model_no);
        this.setModel_sub_no(model_sub_no);
        this.setModel_pre_no(model_pre_no);
        this.setModel_sub_length(model_sub_length);
        this.setModel_sub_position_type(model_sub_position_type);
        this.setModel_sub_check_type(model_sub_check_type);
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
     * @return the model_pre_no
     */
    public int getModel_pre_no() {
        return model_pre_no;
    }


    /**
     * @param model_pre_no the model_pre_no to set
     */
    public void setModel_pre_no(int model_pre_no) {
        this.model_pre_no = model_pre_no;
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
     * @return the model_sub_position_type
     */
    public String getModel_sub_position_type() {
        return model_sub_position_type;
    }


    /**
     * @param model_sub_position_type the model_sub_position_type to set
     */
    public void setModel_sub_position_type(String model_sub_position_type) {
        this.model_sub_position_type = model_sub_position_type;
    }


    /**
     * @return the model_sub_check_type
     */
    public String getModel_sub_check_type() {
        return model_sub_check_type;
    }


    /**
     * @param model_sub_check_type the model_sub_check_type to set
     */
    public void setModel_sub_check_type(String model_sub_check_type) {
        this.model_sub_check_type = model_sub_check_type;
    }


}
