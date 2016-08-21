package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.adapter.PYAdapter;
import com.ssp365.android.freelight.adapter.SporterListAdapter;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBSporterDAO;
import com.ssp365.android.freelight.model.Sporter;
import com.ssp365.android.freelight.wight.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SporterListActivity extends Activity {

    public static final String START_TYPE_SET = "SET";
    public static final String START_TYPE_GET = "GET";
    public static final int DIALOG_SPORTER_DELETE_CONFIRM = 2;
    protected static final String TAG = "SporterListActivity";
    protected static final int SPORTER_ADD = 0;
    protected static final int SPORTER_UPDATE = 1;
    public String start_type = "";
    //对话框中选定的运动员
    public Sporter selectedSporter = null;
    public SmartSportApplication mApplication = null;
    //DB关联
    DBSporterDAO sporterDAO = null;
    private SporterListAdapter adapter;
    private ArrayList<Sporter> webNameArr;
    private Sporter[] arrayItems;
    private WindowManager windowManager;
    //用来放在WindowManager中显示提示字符
    private TextView txtOverlay;
    private ImageButton bt_sporter_cancel;
    private TextView bt_sporter_add, bt_sporter_ok;
    private Handler handler;
    private DisapearThread disapearThread;
    //滚动的状态
    private int scrollState;
    private ListView list, listview;
    //查询窗口
    private ClearEditText mClearEditText;

    //将选中的py与stringArr的首字符进行匹配并返回对应字符串在数组中的位置
    public static int binSearch(Sporter[] arrayItems, String s) {
        Log.i(TAG, "arrayItems.length" + arrayItems.length);
        for (int i = 0; i < arrayItems.length; i++) {
            //不区分大小写
            if (s.equalsIgnoreCase("" + arrayItems[i].getSporter_name_py().charAt(0))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        mApplication = (SmartSportApplication) getApplication();
        super.onCreate(savedInstanceState);

        //启动模式的初始化，如果没有初始值，则设为设定模式
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        start_type = bundle.getString("start_type");
        if (start_type == null) {
            start_type = "SET";
        }
        setContentView(R.layout.sporterlist_activity);

        txtOverlay = (TextView) LayoutInflater.from(this).inflate(R.layout.sporterlist_activity_popup_char_hint, null);
        // 默认设置为不可见。
        txtOverlay.setVisibility(View.INVISIBLE);
        //设置WindowManager
        LayoutParams lp =
                new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.TYPE_APPLICATION,
                        //设置为无焦点状态
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE,
                        //半透明效果
                        PixelFormat.TRANSLUCENT);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(txtOverlay, lp);
        handler = new Handler();
        disapearThread = new DisapearThread();

        // 查询输入框
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //添加运动员按钮的处理
        bt_sporter_add = (TextView) findViewById(R.id.button_sporter_add);
        bt_sporter_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SporterListActivity.this, SporterActivity.class);
                intent.putExtra("start_type", SporterActivity.SPORTER_TYPE_ADD);
                startActivityForResult(intent, SPORTER_ADD);
            }
        });

        //运动员确定的处理
        bt_sporter_ok = (TextView) findViewById(R.id.button_sporterlist_ok);
        bt_sporter_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Sporter> arraySelectedSporters = new ArrayList<>();
                for (int i = 0; i < webNameArr.size(); i++) {
                    if (webNameArr.get(i).isSelected()) {
                        arraySelectedSporters.add(webNameArr.get(i));
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("arraySporter", arraySelectedSporters);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //添加运动员取消的处理
        bt_sporter_cancel = (ImageButton) findViewById(R.id.button_sporterlist_cancle);
        bt_sporter_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sporterDAO = new DBSporterDAO(mApplication.getDb());
        webNameArr = sporterDAO.find();

        //arrayItems = webNameArr.toArray(new Sporter[1]);
        arrayItems = new Sporter[webNameArr.size()];
        for (int m = 0; m < webNameArr.size(); m++) {
            arrayItems[m] = webNameArr.get(m);
            Log.i(TAG, arrayItems[m].getSporter_name() + " " + arrayItems[m].getSporter_name_py());
        }

        Log.i(TAG, "webNameArr.length:" + webNameArr.size());
        Log.i(TAG, "arrayItems.length:" + arrayItems.length);

        Arrays.sort(arrayItems, new ListItemComparator());
        webNameArr.clear();
        for (Sporter arrayItem : arrayItems) {
            webNameArr.add(arrayItem);
            Log.i(TAG, arrayItem.getSporter_name() + " " + arrayItem.getSporter_name_py());
        }

        //选定运动员的设定
        if (mApplication.getArraySporter() != null) {
            for (int i = 0; i < webNameArr.size(); i++) {
                for (int j = 0; j < mApplication.getArraySporter().size(); j++) {
                    if (webNameArr.get(i).getSporter_no() == mApplication.getArraySporter().get(j).getSporter_no()) {
                        webNameArr.get(i).setSelected(true);
                        break;
                    }
                }
            }
        }

        //运动员ListView
        list = (ListView) this.findViewById(R.id.list);
        //adapter = new MyListAdapter(this,webNameArr);
        adapter = new SporterListAdapter(this, webNameArr, R.layout.sporterlist_activity_item, start_type);
        //将数据适配器与Activity进行绑定
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                if (START_TYPE_GET.equals(start_type)) {
                    // 如果是选择按钮，不进行任何操作
                    return;
                }
                selectedSporter = (Sporter) adapter.getItem(position);

                Intent intent = new Intent(SporterListActivity.this, SporterActivity.class);
                intent.putExtra("start_type", SporterActivity.SPORTER_TYPE_UPDATE);
                intent.putExtra("sporter", selectedSporter);
                startActivityForResult(intent, SPORTER_UPDATE);
            }
        });
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                selectedSporter = (Sporter) adapter.getItem(position);

                new AlertDialog.Builder(SporterListActivity.this)
                        .setTitle("删除确认")
                        .setMessage("确认删除运动员：" + selectedSporter.getSporter_name() + "？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                sporterDAO.detele(selectedSporter.getSporter_no());
                                deleteArrayList(selectedSporter);
                                Toast.makeText(SporterListActivity.this, "运动员删除成功！", Toast.LENGTH_SHORT).show();
                                deleteArrayList(selectedSporter);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();

                return true;
            }
        });

        //拼音查询ListView
        listview = (ListView) this.findViewById(R.id.listview);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        PYAdapter adapter1 = new PYAdapter(this, R.layout.sporterlist_activity_textview, size.y);
        listview.setAdapter(adapter1);
        listview.setDivider(null);
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {

                Log.i(TAG, "onItemClick START");

                String s = ((TextView) view).getText().toString();
                txtOverlay.setText(s);
                txtOverlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(disapearThread);
                // 提示延迟1.5s再消失
                handler.postDelayed(disapearThread, 1500);
                int localPosition = binSearch(arrayItems, s); //接收返回值
                if (localPosition != -1) {
                    //防止点击出现的txtOverlay与滚动出现的txtOverlay冲突
                    list.setSelection(localPosition);
                }
            }
        });

        if (START_TYPE_SET.equals(start_type)) {
            bt_sporter_ok.setVisibility(View.GONE);
            bt_sporter_add.setVisibility(View.VISIBLE);
        } else {
            bt_sporter_ok.setVisibility(View.VISIBLE);
            bt_sporter_add.setVisibility(View.GONE);
        }

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        ArrayList<Sporter> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = webNameArr;
        } else {
            filterDateList.clear();
            for (Sporter tSporter : webNameArr) {
                String name = tSporter.getSporter_name();
                if (filterStr.contains(name)) {
                    filterDateList.add(tSporter);
                }
            }
        }

        filterDateList = arraySort(filterDateList, new ListItemComparator());
        adapter.updateListView(filterDateList);
        list.setSelection(0);
    }
    
    /*
    //继承BaseAdapter来设置ListView每行的内容
    public final  class SporterListItem {
    	public CheckBox checked;
        public TextView firstCharHintTextView;
        public TextView orderTextView;   
        public TextView nameTextView;
        public ImageView sporter_GenderImg;
        public ImageButton bt_delete;
		public ImageButton bt_update;
    }
    
    private class MyListAdapter extends BaseAdapter {
    	private LayoutInflater inflater;
		private SporterListItem selectedItem;
		private ArrayList<Sporter> listBean;
        public MyListAdapter(Context context,ArrayList<Sporter> listBean) {
            this.inflater = LayoutInflater.from(context);   
            this.listBean = listBean;
        }
        public int getCount() {   
            return  listBean.size();   
        }
        public Object getItem(int position) {
            return listBean.get(position);
        }   
        public long getItemId(int position) {   
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
        	SporterListItem sporterListItem = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sporterlist_activity_item, null);   
                sporterListItem = new SporterListItem();   
                sporterListItem.checked = (CheckBox) convertView.findViewById(R.id.sporter_checked);
                sporterListItem.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);   
                sporterListItem.orderTextView = (TextView) convertView.findViewById(R.id.sporter_py);
                sporterListItem.nameTextView = (TextView) convertView.findViewById(R.id.sporter_nm);
                sporterListItem.sporter_GenderImg = (ImageView) convertView.findViewById(R.id.sporter_GenderImg);
                sporterListItem.bt_delete = (ImageButton) convertView.findViewById(R.id.sporter_button_delete);
                sporterListItem.bt_update = (ImageButton) convertView.findViewById(R.id.sporter_button_update);
                convertView.setTag(sporterListItem);
            } else {   
            	sporterListItem = (SporterListItem) convertView.getTag();
            }
            //保持checkBox的选项
            sporterListItem.checked.setChecked(listBean.get(position).isSelected());
            //添加构造方法（放到判断中就不好用还不知道原因）
            sporterListItem.checked.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(listBean.get(position).isSelected()){
						listBean.get(position).setSelected(false);
					}else{
						listBean.get(position).setSelected(true);
					}
				}
			});
            
            if(start_type.equals(START_TYPE_SET)){
            	sporterListItem.checked.setVisibility(View.INVISIBLE);
            }else{
            	sporterListItem.checked.setVisibility(View.VISIBLE);
            }
            
            sporterListItem.bt_delete.setTag(R.id.DIALOG_TAT_SPORTER,listBean.get(position));
            sporterListItem.bt_update.setTag(R.id.DIALOG_TAT_SPORTER,listBean.get(position));
            
            sporterListItem.orderTextView.setText(listBean.get(position).getSporter_name_py());
            sporterListItem.nameTextView.setText(listBean.get(position).getSporter_name());      
            int idx = position - 1;   
            //判断前后Item是否匹配，如果不匹配则设置并显示，匹配则取消
            char previewChar = idx >= 0 ? arrayItems[idx].getSporter_name_py().charAt(0) : ' ';
            char currentChar = arrayItems[position].getSporter_name_py().charAt(0); 
            //将小写字符转换为大写字符
            char newPreviewChar = Character.toUpperCase(previewChar);  
            char newCurrentChar = Character.toUpperCase(currentChar);  
            if (newCurrentChar != newPreviewChar) {   
            	sporterListItem.firstCharHintTextView.setVisibility(View.VISIBLE);   
            	sporterListItem.firstCharHintTextView.setText(String.valueOf(newCurrentChar));   
            } else {   
                // 此段代码不可缺：实例化一个CurrentView后，会被多次赋值并且只有最后一次赋值的position是正确   
            	sporterListItem.firstCharHintTextView.setVisibility(View.GONE);   
            }
            if(listBean.get(position).getSporter_xingbie().equals("0")){
            	sporterListItem.sporter_GenderImg.setImageDrawable(getResources().getDrawable(R.drawable.man));
            }else{
            	sporterListItem.sporter_GenderImg.setImageDrawable(getResources().getDrawable(R.drawable.woman));
            }
            
            return convertView;   
        }   
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode:" + requestCode);
        Log.i(TAG, "resultCode:" + resultCode);
        if (data != null) {
            if (requestCode == SPORTER_ADD) {
                if (resultCode == SporterActivity.SPORTER_ADD) {
                    Log.i(TAG, "SPORTER_ADD");
                    Bundle bundle = data.getExtras();
                    Sporter sporter = (Sporter) bundle.get("sporter");
                    Log.i(TAG, "Sporter_no:" + (sporter != null ? sporter.getSporter_no() : -1));
                    addArrayList(sporter);
                }
            } else if (requestCode == SPORTER_UPDATE) {
                if (resultCode == SporterActivity.SPORTER_UPDATE) {
                    Log.i(TAG, "SPORTER_UPDATE");
                    Bundle bundle = data.getExtras();
                    Sporter sporter = (Sporter) bundle.get("sporter");
                    Log.i(TAG, "Sporter_no:" + (sporter != null ? sporter.getSporter_no() : -1));
                    changeArrayList(sporter);
                }
            }
        }
    }

    /*
     * 修改运动员列表的某个运动员
     */
    public void changeArrayList(Sporter sporter) {
        for (int i = 0; i < webNameArr.size(); i++) {
            if (sporter.getSporter_no() == webNameArr.get(i).getSporter_no()) {
                webNameArr.set(i, sporter);
                break;
            }
        }
        webNameArr = arraySort(webNameArr, new ListItemComparator());
        adapter.updateListView(webNameArr);
        list.setSelection(getPosition(webNameArr, sporter.getSporter_no()));
    }

    /*
     * 运动员列表添加运动员
     */
    public void addArrayList(Sporter sporter) {
        webNameArr.add(sporter);
        webNameArr = arraySort(webNameArr, new ListItemComparator());
        adapter.updateListView(webNameArr);
        list.setSelection(getPosition(webNameArr, sporter.getSporter_no()));
    }

    /*
     * 删除运动员列表的某个运动员
     */
    public void deleteArrayList(Sporter sporter) {
        int position = getPosition(webNameArr, sporter.getSporter_no());
        position = position - 1;
        if (position < 0) position = 0;

        for (int i = 0; i < webNameArr.size(); i++) {
            if (sporter.getSporter_no() == webNameArr.get(i).getSporter_no()) {
                webNameArr.remove(i);
                break;
            }
        }

        webNameArr = arraySort(webNameArr, new ListItemComparator());
        adapter.updateListView(webNameArr);
        list.setSelection(getPosition(webNameArr, sporter.getSporter_no()));
    }

    private ArrayList<Sporter> arraySort(ArrayList<Sporter> arrayList_sporter, ListItemComparator listItemComparator) {
        arrayItems = new Sporter[arrayList_sporter.size()];
        for (int m = 0; m < arrayList_sporter.size(); m++) {
            arrayItems[m] = arrayList_sporter.get(m);
        }

        Arrays.sort(arrayItems, listItemComparator);
        arrayList_sporter.clear();
        Collections.addAll(arrayList_sporter, arrayItems);
        return arrayList_sporter;
    }

    private int getPosition(ArrayList<Sporter> arrayList_sporter, int no) {
        for (int i = 0; i < arrayList_sporter.size(); i++) {
            if (no == arrayList_sporter.get(i).getSporter_no()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.i(TAG, "onCreateDialog");
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_SPORTER_DELETE_CONFIRM:
                builder.setMessage("");
                // 返回键是否可以关闭对话框
                builder.setCancelable(false);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sporterDAO.detele(selectedSporter.getSporter_no());
                        deleteArrayList(selectedSporter);
                        Toast.makeText(SporterListActivity.this, "运动员删除成功！", Toast.LENGTH_SHORT).show();
                        deleteArrayList(selectedSporter);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return builder.create();
            default:
                break;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        Log.i(TAG, "onPrepareDialog");
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DIALOG_SPORTER_DELETE_CONFIRM:
                Log.i(TAG, "DIALOG_TEAM_DELETE_CONFIRM");
                ((AlertDialog) dialog).setMessage("确认删除运动员：" + selectedSporter.getSporter_name() + "？");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // 将txtOverlay删除。
        txtOverlay.setVisibility(View.INVISIBLE);
        windowManager.removeView(txtOverlay);
    }

    private class DisapearThread implements Runnable {
        public void run() {
            // 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
            if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
                txtOverlay.setVisibility(View.INVISIBLE);
            }
        }
    }


}

class ListItemComparator implements Comparator {
    public final int compare(Object pFirst, Object pSecond) {
        String firstPY = ((Sporter) pFirst).getSporter_name_py();
        String secondPY = ((Sporter) pSecond).getSporter_name_py();
        if (firstPY.compareTo(secondPY) > 0) {
            return 1;
        } else if (firstPY.compareTo(secondPY) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}

