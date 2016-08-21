package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.model.Chenji;

import java.util.ArrayList;

public class ChenjiListActivity extends Activity {

    protected static final String TAG = "ChenjiListActivity";

    //成绩一览
    private ListView chenji_list = null;
    private ArrayList<ChenjiListItemObject> array_chenji_list = new ArrayList<ChenjiListItemObject>();

    //成绩情报
    private SmartSportApplication mApplication = null;
    private ArrayList<ArrayList<Chenji>> array_chenji_total = null;

    //计时点数
    int sub_count = 1;

    //速度/距离显示(true:速度,false:距离)
    boolean show_flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chenji_list_activity);
        chenji_list = (ListView) findViewById(R.id.chenji_list);
        chenji_list.setScrollBarStyle(BIND_AUTO_CREATE);

        mApplication = (SmartSportApplication) getApplication();
        array_chenji_total = mApplication.getArray_chenji_total();

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

        //计时点数
        if (array_chenji_total.get(0).get(0).getChenji_detail() != null) {
            sub_count = array_chenji_total.get(0).get(0).getChenji_detail().size();
        }

        //成绩列表的初始化
        for (int i = 0; i < (array_chenji_total.get(0).size() * sub_count) + 1; i++) {
            ChenjiListItemObject chenjiListItemObject = new ChenjiListItemObject();
            array_chenji_list.add(chenjiListItemObject);
        }
        array_chenji_list.get(0).chenji_day = "日期";
        array_chenji_list.get(0).chenji_k = "区间";

        //成绩列表值设定
        for (int i = 0; i < array_chenji_total.size(); i++) {
            ArrayList<Chenji> array_chenji = array_chenji_total.get(i);
            Log.i(TAG, "array_chenji:" + array_chenji.get(0).getSporter_name());
            if (i == 0) {
                array_chenji_list.get(0).chenji_sporter1 = array_chenji.get(0).getSporter_name();
            } else if (i == 1) {
                array_chenji_list.get(0).chenji_sporter2 = array_chenji.get(0).getSporter_name();
            } else if (i == 2) {
                array_chenji_list.get(0).chenji_sporter3 = array_chenji.get(0).getSporter_name();
            } else if (i == 3) {
                array_chenji_list.get(0).chenji_sporter4 = array_chenji.get(0).getSporter_name();
            } else if (i == 4) {
                array_chenji_list.get(0).chenji_sporter5 = array_chenji.get(0).getSporter_name();
            } else if (i == 5) {
                array_chenji_list.get(0).chenji_sporter6 = array_chenji.get(0).getSporter_name();
            } else if (i == 6) {
                array_chenji_list.get(0).chenji_sporter7 = array_chenji.get(0).getSporter_name();
            } else if (i == 7) {
                array_chenji_list.get(0).chenji_sporter8 = array_chenji.get(0).getSporter_name();
            } else if (i == 8) {
                array_chenji_list.get(0).chenji_sporter9 = array_chenji.get(0).getSporter_name();
            } else if (i == 9) {
                array_chenji_list.get(0).chenji_sporter10 = array_chenji.get(0).getSporter_name();
            } else if (i == 10) {
                array_chenji_list.get(0).chenji_sporter11 = array_chenji.get(0).getSporter_name();
            } else if (i == 11) {
                array_chenji_list.get(0).chenji_sporter12 = array_chenji.get(0).getSporter_name();
            } else if (i == 12) {
                array_chenji_list.get(0).chenji_sporter13 = array_chenji.get(0).getSporter_name();
            } else if (i == 13) {
                array_chenji_list.get(0).chenji_sporter14 = array_chenji.get(0).getSporter_name();
            } else if (i == 14) {
                array_chenji_list.get(0).chenji_sporter15 = array_chenji.get(0).getSporter_name();
            } else if (i == 15) {
                array_chenji_list.get(0).chenji_sporter16 = array_chenji.get(0).getSporter_name();
            } else if (i == 16) {
                array_chenji_list.get(0).chenji_sporter17 = array_chenji.get(0).getSporter_name();
            } else if (i == 17) {
                array_chenji_list.get(0).chenji_sporter18 = array_chenji.get(0).getSporter_name();
            } else if (i == 18) {
                array_chenji_list.get(0).chenji_sporter19 = array_chenji.get(0).getSporter_name();
            } else if (i == 19) {
                array_chenji_list.get(0).chenji_sporter20 = array_chenji.get(0).getSporter_name();
            } else if (i == 20) {
                array_chenji_list.get(0).chenji_sporter21 = array_chenji.get(0).getSporter_name();
            } else if (i == 21) {
                array_chenji_list.get(0).chenji_sporter22 = array_chenji.get(0).getSporter_name();
            } else if (i == 22) {
                array_chenji_list.get(0).chenji_sporter23 = array_chenji.get(0).getSporter_name();
            } else if (i == 23) {
                array_chenji_list.get(0).chenji_sporter24 = array_chenji.get(0).getSporter_name();
            } else if (i == 24) {
                array_chenji_list.get(0).chenji_sporter25 = array_chenji.get(0).getSporter_name();
            } else if (i == 25) {
                array_chenji_list.get(0).chenji_sporter26 = array_chenji.get(0).getSporter_name();
            } else if (i == 26) {
                array_chenji_list.get(0).chenji_sporter27 = array_chenji.get(0).getSporter_name();
            } else if (i == 27) {
                array_chenji_list.get(0).chenji_sporter28 = array_chenji.get(0).getSporter_name();
            } else if (i == 28) {
                array_chenji_list.get(0).chenji_sporter29 = array_chenji.get(0).getSporter_name();
            } else if (i == 29) {
                array_chenji_list.get(0).chenji_sporter30 = array_chenji.get(0).getSporter_name();
            }
            for (int j = 0; j < array_chenji.size(); j++) {
                Chenji chenji_tmp = array_chenji.get(j);
                Log.i(TAG, "chenji_tmp:" + chenji_tmp.getSporter_name());
                for (int m = 0; m < sub_count; m++) {
                    if (i == 0) {
                        //一组数据时，只有第一条显示
                        if (m == 0) {
                            //只显示日期，不显示时间
                            array_chenji_list.get(sub_count * j + m + 1).chenji_day = chenji_tmp.getChenji_day().substring(0, 10);
                        }
                        array_chenji_list.get(sub_count * j + m + 1).chenji_k = m + 1 + "";
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter1 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter1 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter1 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter1 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter1 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter1 = "-";
                            }
                        }
                    } else if (i == 1) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter2 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter2 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter2 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter2 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter2 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter2 = "-";
                            }
                        }
                    } else if (i == 2) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter3 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter3 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter3 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter3 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter3 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter3 = "-";
                            }
                        }
                    } else if (i == 3) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter4 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter4 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter4 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter4 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter4 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter4 = "-";
                            }
                        }
                    } else if (i == 4) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter5 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter5 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter5 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter5 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter5 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter5 = "-";
                            }
                        }
                    } else if (i == 5) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter6 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter6 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter6 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter6 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter6 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter6 = "-";
                            }
                        }
                    } else if (i == 6) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter7 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter7 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter7 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter7 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter7 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter7 = "-";
                            }
                        }
                    } else if (i == 7) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter8 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter8 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter8 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter8 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter8 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter8 = "-";
                            }
                        }
                    } else if (i == 8) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter9 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter9 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter9 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter9 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter9 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter9 = "-";
                            }
                        }
                    } else if (i == 9) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter10 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter10 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter10 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter10 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter10 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter10 = "-";
                            }
                        }
                    } else if (i == 10) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter11 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter11 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter11 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter11 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter11 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter11 = "-";
                            }
                        }
                    } else if (i == 11) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter12 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter12 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter12 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter12 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter12 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter12 = "-";
                            }
                        }
                    } else if (i == 12) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter13 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter13 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter13 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter13 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter13 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter13 = "-";
                            }
                        }
                    } else if (i == 13) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter14 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter14 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter14 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter14 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter14 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter14 = "-";
                            }
                        }
                    } else if (i == 14) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter15 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter15 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter15 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter15 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter15 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter15 = "-";
                            }
                        }
                    } else if (i == 15) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter16 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter16 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter16 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter16 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter16 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter16 = "-";
                            }
                        }
                    } else if (i == 16) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter17 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter17 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter17 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter17 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter17 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter17 = "-";
                            }
                        }
                    } else if (i == 17) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter18 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter18 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter18 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter18 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter18 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter18 = "-";
                            }
                        }
                    } else if (i == 18) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter19 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter19 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter19 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter19 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter19 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter19 = "-";
                            }
                        }
                    } else if (i == 19) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter20 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter20 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter20 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter20 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter20 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter20 = "-";
                            }
                        }
                    } else if (i == 20) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter21 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter21 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter21 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter21 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter21 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter21 = "-";
                            }
                        }
                    } else if (i == 21) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter22 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter22 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter22 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter22 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter22 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter22 = "-";
                            }
                        }
                    } else if (i == 22) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter23 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter23 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter23 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter23 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter23 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter23 = "-";
                            }
                        }
                    } else if (i == 23) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter24 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter24 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter24 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter24 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter24 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter24 = "-";
                            }
                        }
                    } else if (i == 24) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter25 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter25 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter25 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter25 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter25 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter25 = "-";
                            }
                        }
                    } else if (i == 25) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter26 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter26 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter26 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter26 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter26 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter26 = "-";
                            }
                        }
                    } else if (i == 26) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter27 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter27 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter27 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter27 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter27 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter27 = "-";
                            }
                        }
                    } else if (i == 27) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter28 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter28 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter28 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter28 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter28 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter28 = "-";
                            }
                        }
                    } else if (i == 28) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter29 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter29 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter29 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter29 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter29 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter29 = "-";
                            }
                        }
                    } else if (i == 29) {
                        //详细成绩显示时
                        if (sub_count > 1) {
                            if (chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter30 = chenji_tmp.getChenji_detail().get(m).getModel_sub_speed() + "";
                                } else {
                                    array_chenji_list.get(sub_count * j + m + 1).chenji_sporter30 = chenji_tmp.getChenji_detail().get(m).getModel_sub_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(sub_count * j + m + 1).chenji_sporter30 = "-";
                            }
                            //平均成绩显示时
                        } else {
                            if (chenji_tmp.getModel_total_speed() != -1) {
                                if (show_flag) {
                                    array_chenji_list.get(j + 1).chenji_sporter30 = chenji_tmp.getModel_total_speed() + "";
                                } else {
                                    array_chenji_list.get(j + 1).chenji_sporter30 = chenji_tmp.getModel_total_time() / 1000 + "";
                                }
                            } else {
                                array_chenji_list.get(j + 1).chenji_sporter30 = "-";
                            }
                        }
                    }
                }
            }
        }
        //成绩一览列表
        chenji_list = (ListView) this.findViewById(R.id.chenji_list);
        ChenjiListAdapter adapter = new ChenjiListAdapter(this);
        //将数据适配器与Activity进行绑定
        chenji_list.setAdapter(adapter);


    }

    //一览项目(数据)
    private final class ChenjiListItemObject {
        public String chenji_day = "";
        public String chenji_k = "";
        public String chenji_sporter1 = "";
        public String chenji_sporter2 = "";
        public String chenji_sporter3 = "";
        public String chenji_sporter4 = "";
        public String chenji_sporter5 = "";
        public String chenji_sporter6 = "";
        public String chenji_sporter7 = "";
        public String chenji_sporter8 = "";
        public String chenji_sporter9 = "";
        public String chenji_sporter10 = "";
        public String chenji_sporter11 = "";
        public String chenji_sporter12 = "";
        public String chenji_sporter13 = "";
        public String chenji_sporter14 = "";
        public String chenji_sporter15 = "";
        public String chenji_sporter16 = "";
        public String chenji_sporter17 = "";
        public String chenji_sporter18 = "";
        public String chenji_sporter19 = "";
        public String chenji_sporter20 = "";
        public String chenji_sporter21 = "";
        public String chenji_sporter22 = "";
        public String chenji_sporter23 = "";
        public String chenji_sporter24 = "";
        public String chenji_sporter25 = "";
        public String chenji_sporter26 = "";
        public String chenji_sporter27 = "";
        public String chenji_sporter28 = "";
        public String chenji_sporter29 = "";
        public String chenji_sporter30 = "";
    }

    //一览项目(画面)
    private final class ChenjiListItem {
        public TextView chenji_day;
        public TextView chenji_k;
        public TextView chenji_sporter1;
        public TextView chenji_sporter2;
        public TextView chenji_sporter3;
        public TextView chenji_sporter4;
        public TextView chenji_sporter5;
        public TextView chenji_sporter6;
        public TextView chenji_sporter7;
        public TextView chenji_sporter8;
        public TextView chenji_sporter9;
        public TextView chenji_sporter10;
        public TextView chenji_sporter11;
        public TextView chenji_sporter12;
        public TextView chenji_sporter13;
        public TextView chenji_sporter14;
        public TextView chenji_sporter15;
        public TextView chenji_sporter16;
        public TextView chenji_sporter17;
        public TextView chenji_sporter18;
        public TextView chenji_sporter19;
        public TextView chenji_sporter20;
        public TextView chenji_sporter21;
        public TextView chenji_sporter22;
        public TextView chenji_sporter23;
        public TextView chenji_sporter24;
        public TextView chenji_sporter25;
        public TextView chenji_sporter26;
        public TextView chenji_sporter27;
        public TextView chenji_sporter28;
        public TextView chenji_sporter29;
        public TextView chenji_sporter30;
    }

    private class ChenjiListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ChenjiListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return array_chenji_list.size();
        }

        public Object getItem(int position) {
            return array_chenji_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ChenjiListItem chenjiListItem = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.chenji_list_activity_item, null);
                chenjiListItem = new ChenjiListItem();
                chenjiListItem.chenji_day = (TextView) convertView.findViewById(R.id.chenji_day);
                chenjiListItem.chenji_k = (TextView) convertView.findViewById(R.id.chenji_k);
                chenjiListItem.chenji_sporter1 = (TextView) convertView.findViewById(R.id.chenji_sporter1);
                chenjiListItem.chenji_sporter2 = (TextView) convertView.findViewById(R.id.chenji_sporter2);
                chenjiListItem.chenji_sporter3 = (TextView) convertView.findViewById(R.id.chenji_sporter3);
                chenjiListItem.chenji_sporter4 = (TextView) convertView.findViewById(R.id.chenji_sporter4);
                chenjiListItem.chenji_sporter5 = (TextView) convertView.findViewById(R.id.chenji_sporter5);
                chenjiListItem.chenji_sporter6 = (TextView) convertView.findViewById(R.id.chenji_sporter6);
                chenjiListItem.chenji_sporter7 = (TextView) convertView.findViewById(R.id.chenji_sporter7);
                chenjiListItem.chenji_sporter8 = (TextView) convertView.findViewById(R.id.chenji_sporter8);
                chenjiListItem.chenji_sporter9 = (TextView) convertView.findViewById(R.id.chenji_sporter9);
                chenjiListItem.chenji_sporter10 = (TextView) convertView.findViewById(R.id.chenji_sporter10);
                chenjiListItem.chenji_sporter11 = (TextView) convertView.findViewById(R.id.chenji_sporter11);
                chenjiListItem.chenji_sporter12 = (TextView) convertView.findViewById(R.id.chenji_sporter12);
                chenjiListItem.chenji_sporter13 = (TextView) convertView.findViewById(R.id.chenji_sporter13);
                chenjiListItem.chenji_sporter14 = (TextView) convertView.findViewById(R.id.chenji_sporter14);
                chenjiListItem.chenji_sporter15 = (TextView) convertView.findViewById(R.id.chenji_sporter15);
                chenjiListItem.chenji_sporter16 = (TextView) convertView.findViewById(R.id.chenji_sporter16);
                chenjiListItem.chenji_sporter17 = (TextView) convertView.findViewById(R.id.chenji_sporter17);
                chenjiListItem.chenji_sporter18 = (TextView) convertView.findViewById(R.id.chenji_sporter18);
                chenjiListItem.chenji_sporter19 = (TextView) convertView.findViewById(R.id.chenji_sporter19);
                chenjiListItem.chenji_sporter20 = (TextView) convertView.findViewById(R.id.chenji_sporter20);
                chenjiListItem.chenji_sporter21 = (TextView) convertView.findViewById(R.id.chenji_sporter21);
                chenjiListItem.chenji_sporter22 = (TextView) convertView.findViewById(R.id.chenji_sporter22);
                chenjiListItem.chenji_sporter23 = (TextView) convertView.findViewById(R.id.chenji_sporter23);
                chenjiListItem.chenji_sporter24 = (TextView) convertView.findViewById(R.id.chenji_sporter24);
                chenjiListItem.chenji_sporter25 = (TextView) convertView.findViewById(R.id.chenji_sporter25);
                chenjiListItem.chenji_sporter26 = (TextView) convertView.findViewById(R.id.chenji_sporter26);
                chenjiListItem.chenji_sporter27 = (TextView) convertView.findViewById(R.id.chenji_sporter27);
                chenjiListItem.chenji_sporter28 = (TextView) convertView.findViewById(R.id.chenji_sporter28);
                chenjiListItem.chenji_sporter29 = (TextView) convertView.findViewById(R.id.chenji_sporter29);
                chenjiListItem.chenji_sporter30 = (TextView) convertView.findViewById(R.id.chenji_sporter30);
                convertView.setTag(chenjiListItem);
            } else {
                chenjiListItem = (ChenjiListItem) convertView.getTag();
            }
            if (position == 0) {
                convertView.setBackgroundColor(Color.YELLOW);
            /*
            }else if(position>3&&
            		(position==4||position==5||position==6
            		||(position-4)/sub_count%2==0||(position-5)/sub_count%2==0||(position-6)/sub_count%2==0)){
            	convertView.setBackgroundColor(Color.GREEN);
           	*/
            }

            chenjiListItem.chenji_day.setText(array_chenji_list.get(position).chenji_day);
            //详细成绩时才显示区分
            if (sub_count == 1) {
                chenjiListItem.chenji_k.setVisibility(View.GONE);
            }
            //运动员1
            if (array_chenji_list.get(0).chenji_sporter1.length() > 0) {
                chenjiListItem.chenji_sporter1.setText(array_chenji_list.get(position).chenji_sporter1);
            } else {
                chenjiListItem.chenji_sporter1.setVisibility(View.GONE);
            }
            //运动员2
            if (array_chenji_list.get(0).chenji_sporter2.length() > 0) {
                chenjiListItem.chenji_sporter2.setText(array_chenji_list.get(position).chenji_sporter2);
            } else {
                chenjiListItem.chenji_sporter2.setVisibility(View.GONE);
            }
            //运动员3
            if (array_chenji_list.get(0).chenji_sporter3.length() > 0) {
                chenjiListItem.chenji_sporter3.setText(array_chenji_list.get(position).chenji_sporter3);
            } else {
                chenjiListItem.chenji_sporter3.setVisibility(View.GONE);
            }
            //运动员4
            if (array_chenji_list.get(0).chenji_sporter4.length() > 0) {
                chenjiListItem.chenji_sporter4.setText(array_chenji_list.get(position).chenji_sporter4);
            } else {
                chenjiListItem.chenji_sporter4.setVisibility(View.GONE);
            }
            //运动员5
            if (array_chenji_list.get(0).chenji_sporter5.length() > 0) {
                chenjiListItem.chenji_sporter5.setText(array_chenji_list.get(position).chenji_sporter5);
            } else {
                chenjiListItem.chenji_sporter5.setVisibility(View.GONE);
            }
            //运动员6
            if (array_chenji_list.get(0).chenji_sporter6.length() > 0) {
                chenjiListItem.chenji_sporter6.setText(array_chenji_list.get(position).chenji_sporter6);
            } else {
                chenjiListItem.chenji_sporter6.setVisibility(View.GONE);
            }
            //运动员7
            if (array_chenji_list.get(0).chenji_sporter7.length() > 0) {
                chenjiListItem.chenji_sporter7.setText(array_chenji_list.get(position).chenji_sporter7);
            } else {
                chenjiListItem.chenji_sporter7.setVisibility(View.GONE);
            }
            //运动员8
            if (array_chenji_list.get(0).chenji_sporter8.length() > 0) {
                chenjiListItem.chenji_sporter8.setText(array_chenji_list.get(position).chenji_sporter8);
            } else {
                chenjiListItem.chenji_sporter8.setVisibility(View.GONE);
            }
            //运动员9
            if (array_chenji_list.get(0).chenji_sporter9.length() > 0) {
                chenjiListItem.chenji_sporter9.setText(array_chenji_list.get(position).chenji_sporter9);
            } else {
                chenjiListItem.chenji_sporter9.setVisibility(View.GONE);
            }
            //运动员10
            if (array_chenji_list.get(0).chenji_sporter10.length() > 0) {
                chenjiListItem.chenji_sporter10.setText(array_chenji_list.get(position).chenji_sporter10);
            } else {
                chenjiListItem.chenji_sporter10.setVisibility(View.GONE);
            }
            //运动员11
            if (array_chenji_list.get(0).chenji_sporter11.length() > 0) {
                chenjiListItem.chenji_sporter11.setText(array_chenji_list.get(position).chenji_sporter11);
            } else {
                chenjiListItem.chenji_sporter11.setVisibility(View.GONE);
            }
            //运动员12
            if (array_chenji_list.get(0).chenji_sporter12.length() > 0) {
                chenjiListItem.chenji_sporter12.setText(array_chenji_list.get(position).chenji_sporter12);
            } else {
                chenjiListItem.chenji_sporter12.setVisibility(View.GONE);
            }
            //运动员13
            if (array_chenji_list.get(0).chenji_sporter13.length() > 0) {
                chenjiListItem.chenji_sporter13.setText(array_chenji_list.get(position).chenji_sporter13);
            } else {
                chenjiListItem.chenji_sporter13.setVisibility(View.GONE);
            }
            //运动员14
            if (array_chenji_list.get(0).chenji_sporter14.length() > 0) {
                chenjiListItem.chenji_sporter14.setText(array_chenji_list.get(position).chenji_sporter14);
            } else {
                chenjiListItem.chenji_sporter14.setVisibility(View.GONE);
            }
            //运动员15
            if (array_chenji_list.get(0).chenji_sporter15.length() > 0) {
                chenjiListItem.chenji_sporter15.setText(array_chenji_list.get(position).chenji_sporter15);
            } else {
                chenjiListItem.chenji_sporter15.setVisibility(View.GONE);
            }
            //运动员16
            if (array_chenji_list.get(0).chenji_sporter16.length() > 0) {
                chenjiListItem.chenji_sporter16.setText(array_chenji_list.get(position).chenji_sporter16);
            } else {
                chenjiListItem.chenji_sporter16.setVisibility(View.GONE);
            }
            //运动员17
            if (array_chenji_list.get(0).chenji_sporter17.length() > 0) {
                chenjiListItem.chenji_sporter17.setText(array_chenji_list.get(position).chenji_sporter17);
            } else {
                chenjiListItem.chenji_sporter17.setVisibility(View.GONE);
            }
            //运动员18
            if (array_chenji_list.get(0).chenji_sporter18.length() > 0) {
                chenjiListItem.chenji_sporter18.setText(array_chenji_list.get(position).chenji_sporter18);
            } else {
                chenjiListItem.chenji_sporter18.setVisibility(View.GONE);
            }
            //运动员19
            if (array_chenji_list.get(0).chenji_sporter19.length() > 0) {
                chenjiListItem.chenji_sporter19.setText(array_chenji_list.get(position).chenji_sporter19);
            } else {
                chenjiListItem.chenji_sporter19.setVisibility(View.GONE);
            }
            //运动员20
            if (array_chenji_list.get(0).chenji_sporter20.length() > 0) {
                chenjiListItem.chenji_sporter20.setText(array_chenji_list.get(position).chenji_sporter20);
            } else {
                chenjiListItem.chenji_sporter20.setVisibility(View.GONE);
            }
            //运动员21
            if (array_chenji_list.get(0).chenji_sporter21.length() > 0) {
                chenjiListItem.chenji_sporter21.setText(array_chenji_list.get(position).chenji_sporter21);
            } else {
                chenjiListItem.chenji_sporter21.setVisibility(View.GONE);
            }
            //运动员22
            if (array_chenji_list.get(0).chenji_sporter22.length() > 0) {
                chenjiListItem.chenji_sporter22.setText(array_chenji_list.get(position).chenji_sporter22);
            } else {
                chenjiListItem.chenji_sporter22.setVisibility(View.GONE);
            }
            //运动员23
            if (array_chenji_list.get(0).chenji_sporter23.length() > 0) {
                chenjiListItem.chenji_sporter23.setText(array_chenji_list.get(position).chenji_sporter23);
            } else {
                chenjiListItem.chenji_sporter23.setVisibility(View.GONE);
            }
            //运动员24
            if (array_chenji_list.get(0).chenji_sporter24.length() > 0) {
                chenjiListItem.chenji_sporter24.setText(array_chenji_list.get(position).chenji_sporter24);
            } else {
                chenjiListItem.chenji_sporter24.setVisibility(View.GONE);
            }
            //运动员25
            if (array_chenji_list.get(0).chenji_sporter25.length() > 0) {
                chenjiListItem.chenji_sporter25.setText(array_chenji_list.get(position).chenji_sporter25);
            } else {
                chenjiListItem.chenji_sporter25.setVisibility(View.GONE);
            }
            //运动员26
            if (array_chenji_list.get(0).chenji_sporter26.length() > 0) {
                chenjiListItem.chenji_sporter26.setText(array_chenji_list.get(position).chenji_sporter26);
            } else {
                chenjiListItem.chenji_sporter26.setVisibility(View.GONE);
            }
            //运动员27
            if (array_chenji_list.get(0).chenji_sporter27.length() > 0) {
                chenjiListItem.chenji_sporter27.setText(array_chenji_list.get(position).chenji_sporter27);
            } else {
                chenjiListItem.chenji_sporter27.setVisibility(View.GONE);
            }
            //运动员28
            if (array_chenji_list.get(0).chenji_sporter28.length() > 0) {
                chenjiListItem.chenji_sporter28.setText(array_chenji_list.get(position).chenji_sporter28);
            } else {
                chenjiListItem.chenji_sporter28.setVisibility(View.GONE);
            }
            //运动员29
            if (array_chenji_list.get(0).chenji_sporter29.length() > 0) {
                chenjiListItem.chenji_sporter29.setText(array_chenji_list.get(position).chenji_sporter29);
            } else {
                chenjiListItem.chenji_sporter29.setVisibility(View.GONE);
            }
            //运动员30
            if (array_chenji_list.get(0).chenji_sporter30.length() > 0) {
                chenjiListItem.chenji_sporter30.setText(array_chenji_list.get(position).chenji_sporter30);
            } else {
                chenjiListItem.chenji_sporter30.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}

