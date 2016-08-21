package com.ssp365.android.freelight.common;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ssp365.android.freelight.db.DBOpenHelper;
import com.ssp365.android.freelight.model.Chenji;
import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.ModelDetail;
import com.ssp365.android.freelight.model.Sporter;
import com.ssp365.android.freelight.wifi.WifiServer;

import java.util.ArrayList;

public class SmartSportApplication extends Application {

    //共享变量:共享蓝牙信息
    private SmartSportHandler handler = null;

    //模式信息
    private Model model = null;
    private ArrayList<ModelDetail> modelDetails = new ArrayList<ModelDetail>();
    private int[] pointArray = null;
    private String[][] pointStateArray = null;
    private String[][] waitStateArray = null;


    //运动员信息
    private ArrayList<Sporter> arraySporter;
    //模式编号
    private ArrayList<Model> arrayModel;

    private WifiServer mWifiService = null;

    //DB用句柄
    private DBOpenHelper helper = null;
    private SQLiteDatabase db = null;

    //成绩记录
    ArrayList<ArrayList<Chenji>> array_chenji_total = null;


    //
    public ArrayList<Sporter> getArraySporter() {
        return arraySporter;
    }

    public void setArraySporter(ArrayList<Sporter> arraySporter) {
        this.arraySporter = arraySporter;
    }

    //
    public ArrayList<Model> getArrayModel() {
        return arrayModel;
    }

    public void setArrayModel(ArrayList<Model> arrayModel) {
        this.arrayModel = arrayModel;
    }

    // 
    public void setHandler(SmartSportHandler handler) {
        this.handler = handler;
    }

    public SmartSportHandler getHandler() {
        return handler;
    }

    //
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    //
    public ArrayList<ModelDetail> getModelDetails() {
        return modelDetails;
    }

    public void setModelDetails(ArrayList<ModelDetail> modelDetails) {
        this.modelDetails = modelDetails;
    }

    /**
     * @return the pointArray
     */
    public int[] getPointArray() {
        return pointArray;
    }

    /**
     * @param pointArray the pointArray to set
     */
    public void setPointArray(int[] pointArray) {
        this.pointArray = pointArray;
    }

    /**
     * @return the pointStateArray
     */
    public String[][] getPointStateArray() {
        return pointStateArray;
    }

    /**
     * @param pointStateArray the pointStateArray to set
     */
    public void setPointStateArray(String[][] pointStateArray) {
        this.pointStateArray = pointStateArray;
    }

    /**
     * @return the waitStateArray
     */
    public String[][] getWaitStateArray() {
        return waitStateArray;
    }

    /**
     * @param waitStateArray the waitStateArray to set
     */
    public void setWaitStateArray(String[][] waitStateArray) {
        this.waitStateArray = waitStateArray;
    }

    //
    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    //
    public DBOpenHelper getHelper() {
        return helper;
    }

    public void setHelper(DBOpenHelper helper) {
        this.helper = helper;
    }

    public ArrayList<ArrayList<Chenji>> getArray_chenji_total() {
        return array_chenji_total;
    }

    public void setArray_chenji_total(
            ArrayList<ArrayList<Chenji>> array_chenji_total) {
        this.array_chenji_total = array_chenji_total;
    }

    /**
     * @return the mWifiService
     */
    public WifiServer getmWifiService() {
        return mWifiService;
    }

    /**
     * @param mWifiService the mWifiService to set
     */
    public void setmWifiService(WifiServer mWifiService) {
        this.mWifiService = mWifiService;
    }

    //给各信号柱设置信号序列
    public void setPointMsg() {
        int[] pointArray = getPointArray();
    }


}
