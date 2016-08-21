/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ssp365.android.freelight.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

import com.ssp365.android.freelight.model.Chenji;
import com.ssp365.android.freelight.model.ChenjiDetail;
import com.ssp365.android.freelight.model.Parameter;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Average temperature demo chart.
 */
public class ChartActivity extends AbstractChartClass {

    protected static final String TAG = "ChartActivity";

    //成绩列表
    private ArrayList<ArrayList<Chenji>> array_chenji_total = null;
    //最小记录和最大记录
    double min_record = 1000, max_record = 0;
    //成绩详细时的区间数
    int sub_count = -1;
    //速度/距离显示(true:速度,false:距离)
    boolean show_flag = true;

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    public String getName() {
        return "Average temperature";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The average temperature in 4 Greek islands (line chart)";
    }

    /**
     * 参数设定
     *
     * @param array_chenji
     * @return null
     */
    public void setValue(ArrayList<ArrayList<Chenji>> array_chenji_total) {
        this.array_chenji_total = array_chenji_total;
    }

    /**
     * Executes the chart demo.
     *
     * @param context the context
     * @return the built intent
     */
    public View execute(Context context) {

        //成绩详细时的区间数
        ArrayList<ChenjiDetail> array_chenji_detail = array_chenji_total.get(0).get(0).getChenji_detail();
        if (array_chenji_detail != null) {
            sub_count = array_chenji_detail.size();
        }

        //成绩详细分析的场合
        if (array_chenji_total.get(0).get(0).getChenji_detail() != null) {
            for (int i = 0; i < array_chenji_total.get(0).get(0).getChenji_detail().size(); i++) {
                //非空白点
                if (array_chenji_total.get(0).get(0).getChenji_detail().get(i).getModel_sub_no() != -1) {
                    //当距离为-1时，没法计算时间，为时间显示模式
                    show_flag = array_chenji_total.get(0).get(0).getChenji_detail().get(i).getModel_sub_length() != -1;
                    break;
                }
            }
            //成绩详细分析的场合
        } else {
            for (int i = 0; i < array_chenji_total.get(0).size(); i++) {
                //非空白点
                if (array_chenji_total.get(0).get(i).getModel_no() != -1) {
                    //当距离为-1时，没法计算时间，为时间显示模式
                    show_flag = array_chenji_total.get(0).get(i).getModel_total_length() != -1;
                    break;
                }
            }
        }


        //标识的定义
        String[] titles = null;
        //颜色的定义
        int[] colors;
        //点形的定义
        PointStyle[] styles;
        //横坐标容器
        List<double[]> x = new ArrayList<double[]>();
        //横坐标定义
        double[][] chenji_x = new double[0][0];
        //纵坐标定义
        double[][] chenji_y = new double[0][0];
        //曲线的定义
        List<double[]> values = new ArrayList<double[]>();
        //成绩明细分析
        if (sub_count > 0) {
            //标识
            titles = new String[array_chenji_total.size() * sub_count];
            //颜色
            colors = new int[array_chenji_total.size() * sub_count];
            //颜色
            styles = new PointStyle[array_chenji_total.size() * sub_count];
            //成绩分析
        } else {
            //标识
            titles = new String[array_chenji_total.size()];
            //颜色
            colors = new int[array_chenji_total.size()];
            //点形的定义
            styles = new PointStyle[array_chenji_total.size()];
        }
        for (int i = 0; i < array_chenji_total.size(); i++) {
            ArrayList<Chenji> array_chenji = array_chenji_total.get(i);
            if (array_chenji.get(0).getChenji_detail() != null) {
                chenji_x = new double[array_chenji.get(0).getChenji_detail().size()][array_chenji.size()];
                chenji_y = new double[array_chenji.get(0).getChenji_detail().size()][array_chenji.size()];
            } else {
                chenji_x = new double[1][array_chenji.size()];
                chenji_y = new double[1][array_chenji.size()];
            }
            for (int j = 0; j < array_chenji.size(); j++) {
                Chenji chenji = array_chenji.get(j);
                //成绩详细分析的场合
                if (chenji.getChenji_detail() != null) {
                    for (int m = 0; m < chenji.getChenji_detail().size(); m++) {
                        //标识
                        titles[chenji.getChenji_detail().size() * i + m] = array_chenji.get(0).getSporter_name() + "点" + (m + 1);
                        //横坐标
                        chenji_x[m][j] = j + 1;
                        Log.i(TAG, "chenji_x[" + m + "][" + j + "]" + chenji_x[m][j]);
                        //纵坐标
                        if (show_flag) {
                            chenji_y[m][j] = array_chenji.get(j).getChenji_detail().get(m).getModel_sub_speed();
                        } else {
                            chenji_y[m][j] = array_chenji.get(j).getChenji_detail().get(m).getModel_sub_time() / 1000;
                        }
                        Log.i(TAG, "chenji_y[" + m + "][" + j + "]" + chenji_y[m][j]);
                        //颜色
                        colors[chenji.getChenji_detail().size() * i + m] = Parameter.COLOR[i % Parameter.COLOR.length];
                        //点形
                        styles[chenji.getChenji_detail().size() * i + m] = Parameter.POINT_STYLE[m % Parameter.POINT_STYLE.length];
                        /*
                        if(min_record>chenji.getChenji_detail().get(m).getModel_sub_speed()
								&&chenji.getChenji_detail().get(m).getModel_sub_speed()!=-1){
							min_record = chenji.getChenji_detail().get(m).getModel_sub_speed();
						}else if(max_record<chenji.getChenji_detail().get(m).getModel_sub_speed()){
							max_record = chenji.getChenji_detail().get(m).getModel_sub_speed();
						}
						*/
                        if ((min_record > chenji_y[m][j]) && chenji_y[m][j] != -1) {
                            min_record = chenji_y[m][j];
                        }
                        if ((max_record < chenji_y[m][j]) && chenji_y[m][j] != -1) {
                            max_record = chenji_y[m][j];
                        }
                    }
                } else {
                    //标识
                    titles[i] = array_chenji.get(0).getSporter_name();
                    //横坐标
                    chenji_x[0][j] = j + 1;
                    //成绩
                    if (show_flag) {
                        chenji_y[0][j] = array_chenji.get(j).getModel_total_speed();
                    } else {
                        chenji_y[0][j] = array_chenji.get(j).getModel_total_time() / 1000;
                    }
                    //颜色
                    colors[i] = Parameter.COLOR[i % Parameter.COLOR.length];
                    Log.i(TAG, i + ":" + i % Parameter.COLOR.length);
                    //点形
                    styles[i] = Parameter.POINT_STYLE[0];
                    /*
                    if(min_record>chenji.getModel_total_speed()
							&&chenji.getModel_total_speed()!=-1){
						min_record = chenji.getModel_total_speed();
					}else if(max_record<chenji.getModel_total_speed()){
						max_record = chenji.getModel_total_speed();
					}
					*/
                    if ((min_record > chenji_y[0][j]) && chenji_y[0][j] != -1) {
                        min_record = chenji_y[0][j];
                    }
                    if ((max_record < chenji_y[0][j]) && chenji_y[0][j] != -1) {
                        max_record = chenji_y[0][j];
                    }
                }
            }
            for (int p = 0; p < chenji_x.length; p++) {
                x.add(chenji_x[p]);
                for (int q = 0; q < chenji_x[p].length; q++) {
                    Log.i(TAG, "chenji_x[" + p + "][" + q + "]" + chenji_x[p][q]);
                }
                values.add(chenji_y[p]);
                for (int q = 0; q < chenji_y[p].length; q++) {
                    Log.i(TAG, "chenji_y[" + p + "][" + q + "]" + chenji_y[p][q]);
                }
            }
        }

        for (int i = 0; i < colors.length; i++) {
            Log.i(TAG, "titles[" + i + "]:" + titles[i]);
            Log.i(TAG, "styles[" + i + "]:" + styles[i]);
            Log.i(TAG, "colors[" + i + "]:" + colors[i]);

            double[] array_x = x.get(i);
            for (int j = 0; j < array_x.length; j++) {
                Log.i(TAG, "array_x[" + j + "]:" + array_x[j]);
            }
            double[] array_y = values.get(i);
            for (int j = 0; j < array_y.length; j++) {
                Log.i(TAG, "array_y[" + j + "]:" + array_y[j]);
            }
        }

        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        //都设为实心点
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }

        Log.i(TAG, "min_record:" + min_record);
        Log.i(TAG, "max_record:" + max_record);

        double chart_max_speed = 10;
        double chart_min_speed = -1;
        double chart_max_x = 13;
        chart_min_speed = (new BigDecimal((min_record - 0.5) + "")).intValue();
        //if(max_record>10){
        chart_max_speed = (new BigDecimal((max_record + 1) + "")).intValue();
        //}
        if (array_chenji_total.get(0).size() > 12) {
            chart_max_x = array_chenji_total.get(0).size() + 1;
        }

        //数据集中在一个点时，或点过于集中时，显示区域为2米每秒
        if (chart_min_speed == chart_max_speed
                || chart_max_speed - chart_min_speed < 4) {
            if ((chart_min_speed + chart_max_speed) / 2 - 1 < 0) {
                chart_min_speed = 0;
                chart_max_speed = 4;
            } else if (chart_min_speed == chart_max_speed) {
                chart_min_speed = chart_min_speed - 2;
                chart_max_speed = chart_max_speed + 2;
            } else {
                chart_min_speed = (chart_min_speed + chart_max_speed) / 2 - 2;
                chart_max_speed = (chart_min_speed + chart_max_speed) / 2 + 2;
            }
        }

        Log.i(TAG, "chart_min_speed:" + chart_min_speed);
        Log.i(TAG, "chart_max_speed:" + chart_max_speed);

        if (show_flag) {
            setChartSettings(renderer, "", "次数", "速度(m/s)", 0, 13, chart_min_speed, chart_max_speed,
                    Color.LTGRAY, Color.LTGRAY);
        } else {
            setChartSettings(renderer, "", "次数", "时间(s)", 0, 13, chart_min_speed, chart_max_speed,
                    Color.LTGRAY, Color.LTGRAY);
        }

        renderer.setXLabels(13);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);

        renderer.setZoomButtonsVisible(true);
        renderer.setPanLimits(new double[]{0, chart_max_x, 0, chart_max_speed});
        renderer.setZoomLimits(new double[]{0, chart_max_x, 0, chart_max_speed});
        renderer.setBackgroundColor(Color.BLACK);
        renderer.setApplyBackgroundColor(true);
    
	    /*
        Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
	    		renderer, "成绩曲线图");
	    return intent;
	    */

        //线图
        View chart = ChartFactory.getLineChartView(context, buildDataset(titles, x, values), renderer);
        return chart;
    }

}
