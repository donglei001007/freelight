package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBModelDAO;
import com.ssp365.android.freelight.dialog.SimpleDialog;
import com.ssp365.android.freelight.inf.SimpleDialogListener;
import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.Pinyin4jUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModelActivity extends Activity {

    protected static final String TAG = "ModelActivity";

    //模式对话框标示
    public static final int DIALOG_TEAM_NAME_ERROR = 1;
    public static final int DIALOG_TEAM_DELETE_CONFIRM = 2;

    //启动区分：设定画面
    public static final String START_TYPE_SET = "SET";
    //启动区分：一览画面
    public static final String START_TYPE_GET = "GET";
    public String start_type = "";

    //返回按钮
    private TextView bt_model_ok, bt_model_add;
    //模式增加按钮
    private ImageButton bt_model_cancel;
    //模式列表
    private ListView model_list;

    //明细内情报：模式编号、模式名称
    private ArrayList<Model> models;
    ModelListAdapter list_view_adapter = null;

    //对话框中模式编号
    public int item_selected_model_no = -1;
    //对话框中模式名称
    public String item_selected_model_name = "";

    public SmartSportApplication mApplication = null;
    //DB关联
    DBModelDAO modelDAO = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_activity);

        model_list = (ListView) findViewById(R.id.list_model);

        //启动模式的初始化，如果没有初始值，则设为设定模式
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        start_type = bundle.getString("start_type");
        if (start_type == null) {
            start_type = "SET";
        }

        mApplication = (SmartSportApplication) getApplication();
        modelDAO = new DBModelDAO(mApplication.getDb());
        models = modelDAO.find();

        list_view_adapter = new ModelListAdapter(models, this);
        model_list.setAdapter(list_view_adapter);

        model_list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.i("MainActivity", "Position:" + position);
                Intent intent = new Intent(ModelActivity.this, ModelImageViewActivity.class);
                intent.putExtra("pic", models.get(position).getModel_pic());
                intent.putExtra("name", models.get(position).getModel_name());
                startActivity(intent);
            }
        });

        //设定完了按钮的处理
        bt_model_ok = (TextView) findViewById(R.id.button_model_ok);
        bt_model_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Model> arraySelectedModels = new ArrayList<Model>();
                for (int i = 0; i < models.size(); i++) {
                    if (models.get(i).isSelected()) {
                        arraySelectedModels.add(models.get(i));
                        Log.i(TAG, "Model_name:" + models.get(i).getModel_name());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("arrayModel", arraySelectedModels);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        //模式选择取消的处理
        bt_model_cancel = (ImageButton) findViewById(R.id.button_model_cancle);
        bt_model_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //添加模式按钮的处理
        bt_model_add = (TextView) findViewById(R.id.button_model_add);
        bt_model_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialog dialog = new SimpleDialog(ModelActivity.this, new SimpleDialogListener() {
                    @Override
                    public void onOkClick(String name) {
                        //模式名输入检查
                        if (!checkModel(name)) {
                            return;
                        }
                        //队名重复的检查
                        Model model = new Model(0, name, Pinyin4jUtil.getPinYinHeadChar(name), "", 2, 2, -1, 0);
                        if (!modelDAO.checkModelName(model)) {
                            showErrorMessage("错误提示", "测试项目名重复，请重新输入测试项目名");
                            return;
                        }
                        model = modelDAO.addModel(model);
                        addArrayList(model);
                        Toast.makeText(ModelActivity.this, "测试项添加成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelClick() {
                    }
                }, R.style.dialog, "请输入添加的测试项名");
                dialog.show();
            }
        });


        bt_model_add.setVisibility(View.INVISIBLE);
        if (START_TYPE_SET.equals(start_type)) {
            bt_model_ok.setVisibility(View.INVISIBLE);
        }
    }

    public void addArrayList(Model model) {
        models.add(model);
        models = arraySort(models, new ModelListItemComparator());
        list_view_adapter.flush(models);

        model_list.setSelection(getPosition(models, model.getModel_no()));
    }

    private int getPosition(ArrayList<Model> arrayList_model, int no) {
        for (int i = 0; i < arrayList_model.size(); i++) {
            if (no == arrayList_model.get(i).getModel_no()) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Model> arraySort(ArrayList<Model> arrayList_model, ModelListItemComparator modelListItemComparator) {
        Log.i(TAG, "排序前:");
        for (int i = 0; i < arrayList_model.size(); i++) {
            Log.i(TAG, arrayList_model.get(i).getModel_name() + ":" + arrayList_model.get(i).getModel_name_py());
        }

        Model[] model_list = arrayList_model.toArray(new Model[1]);
        Arrays.sort(model_list, modelListItemComparator);
        arrayList_model = new ArrayList<Model>();
        for (int i = 0; i < model_list.length; i++) {
            arrayList_model.add(model_list[i]);
        }

        Log.i(TAG, "排序后:");
        for (int i = 0; i < arrayList_model.size(); i++) {
            Log.i(TAG, arrayList_model.get(i).getModel_name() + ":" + arrayList_model.get(i).getModel_name_py());
        }
        return arrayList_model;
    }

    class ModelListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Model> modelListObjects;

        public ModelListAdapter(ArrayList<Model> models, Context context) {
            super();
            modelListObjects = models;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (null != modelListObjects) {
                return modelListObjects.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return modelListObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ModelListItem modelListItem;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.model_activity_item, null);
                modelListItem = new ModelListItem();
                modelListItem.checked = (CheckBox) convertView.findViewById(R.id.model_checked);
                modelListItem.model_image = (ImageView) convertView.findViewById(R.id.model_Img);
                modelListItem.model_no = (TextView) convertView.findViewById(R.id.model_no);
                modelListItem.model_name = (TextView) convertView.findViewById(R.id.model_name);
                modelListItem.model_name_py = (TextView) convertView.findViewById(R.id.model_name_py);
                modelListItem.bt_delete = (ImageButton) convertView.findViewById(R.id.button_model_delete);
                modelListItem.bt_update = (ImageButton) convertView.findViewById(R.id.button_model_update);
                convertView.setTag(modelListItem);
            } else {
                modelListItem = (ModelListItem) convertView.getTag();
            }

            Log.i(TAG, "" + modelListObjects.get(position).getModel_no());
            Log.i(TAG, "" + modelListObjects.get(position).getModel_name());
            Log.i(TAG, "" + modelListObjects.get(position).getModel_name_py());
            Log.i(TAG, modelListItem.toString());
            Log.i(TAG, modelListItem.model_no.toString());

            //保持checkBox的选项
            modelListItem.checked.setChecked(modelListObjects.get(position).isSelected());
            //添加构造方法（放到判断中就不好用还不知道原因）
            modelListItem.checked.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (models.get(position).isSelected()) {
                        models.get(position).setSelected(false);
                    } else {
                        models.get(position).setSelected(true);
                    }
                }
            });

            if (start_type.equals(START_TYPE_SET)) {
                modelListItem.checked.setVisibility(View.INVISIBLE);
            } else {
                modelListItem.checked.setVisibility(View.VISIBLE);
            }

            modelListItem.model_image.setImageResource(R.drawable.model);
            modelListItem.model_no.setText(modelListObjects.get(position).getModel_no() + "");
            modelListItem.model_name.setText(modelListObjects.get(position).getModel_name());
            modelListItem.model_name_py.setText(modelListObjects.get(position).getModel_name_py());
            modelListItem.bt_delete.setTag(R.id.DIALOG_TAT_MODEL_NO, modelListObjects.get(position).getModel_no());
            modelListItem.bt_delete.setTag(R.id.DIALOG_TAT_MODEL_NAME, modelListObjects.get(position).getModel_name());
            modelListItem.bt_update.setTag(R.id.DIALOG_TAT_MODEL_NO, modelListObjects.get(position).getModel_no());
            modelListItem.bt_update.setTag(R.id.DIALOG_TAT_MODEL_NAME, modelListObjects.get(position).getModel_name());
            //默认测试项不能修改，不显示修改按钮
            Log.i(TAG, "position:" + position + " Model_pic:" + modelListObjects.get(position).getModel_pic().trim());
            if (modelListObjects.get(position).getModel_pic().trim().length() > 0) {
                modelListItem.bt_delete.setVisibility(View.GONE);
                modelListItem.bt_update.setVisibility(View.GONE);
            } else {
                modelListItem.bt_delete.setVisibility(View.VISIBLE);
                modelListItem.bt_update.setVisibility(View.VISIBLE);
            }

            modelListItem.bt_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_selected_model_no = ((Integer) v.getTag(R.id.DIALOG_TAT_MODEL_NO)).intValue();
                    item_selected_model_name = (String) v.getTag(R.id.DIALOG_TAT_MODEL_NAME);
                    //确认该模式下是否有成绩，如果有成绩的话，确认是否删除
                    if (!modelDAO.checkDeleteModel(new Model(item_selected_model_no,
                            item_selected_model_name,
                            Pinyin4jUtil.getPinYinHeadChar(item_selected_model_name)
                            , " ", 2, 2, -1, 0))) {
                        new AlertDialog.Builder(ModelActivity.this)
                                .setTitle("删除确认")
                                .setMessage(item_selected_model_name + " 已经有测试成绩了，确实要删除？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        modelDAO.delete(item_selected_model_no);
                                        Model model = new Model(item_selected_model_no,
                                                item_selected_model_name,
                                                Pinyin4jUtil.getPinYinHeadChar(item_selected_model_name)
                                                , " ", 0, 0, 0, 0);
                                        deleteArrayList(model);
                                        Toast.makeText(ModelActivity.this, "测试项删除成功！", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create().show();
                    } else {
                        new AlertDialog.Builder(ModelActivity.this)
                                .setTitle("删除确认")
                                .setMessage("确实要删除测试项：" + item_selected_model_name + "？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        modelDAO.delete(item_selected_model_no);
                                        Model model = new Model(item_selected_model_no,
                                                item_selected_model_name,
                                                Pinyin4jUtil.getPinYinHeadChar(item_selected_model_name)
                                                , " ", 2, 2, -1, 0);
                                        deleteArrayList(model);
                                        Toast.makeText(ModelActivity.this, "测试项删除成功！", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create().show();
                    }
                }
            });
            modelListItem.bt_update.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_selected_model_no = ((Integer) v.getTag(R.id.DIALOG_TAT_MODEL_NO)).intValue();
                    item_selected_model_name = (String) v.getTag(R.id.DIALOG_TAT_MODEL_NAME);
                    SimpleDialog dialog = new SimpleDialog(inflater.getContext(), new SimpleDialogListener() {
                        @Override
                        public void onOkClick(String name) {
                            //测试项输入检查
                            if (!checkModel(name)) {
                                return;
                            }
                            //测试项名重复的检查
                            Model model = new Model(item_selected_model_no,
                                    name,
                                    Pinyin4jUtil.getPinYinHeadChar(name)
                                    , " ", 2, 2, -1, 0);
                            if (!modelDAO.checkModelName(model)) {
                                showErrorMessage("错误提示", "测试项名重复，请重新输入测试项名");
                                return;
                            }
                            modelDAO.update(model);
                            changArrayList(model);
                            Toast.makeText(ModelActivity.this, "测试项修改成功！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelClick() {
                        }
                    }, R.style.dialog, "请输入修改后的测试项名", item_selected_model_name);
                    dialog.show();
                }
            });

            Log.i("ModelActivity", "<<<<<<<" + position);
            return convertView;
        }

        public void flush(ArrayList<Model> models) {
            modelListObjects = models;
            notifyDataSetChanged();
        }

    }

    public void changArrayList(Model model) {
        for (int i = 0; i < models.size(); i++) {
            if (model.getModel_no() == models.get(i).getModel_no()) {
                models.set(i, model);
                break;
            }
        }
        models = arraySort(models, new ModelListItemComparator());
        list_view_adapter.flush(models);

        model_list.setSelection(getPosition(models, model.getModel_no()));
    }

    public void deleteArrayList(Model model) {
        int position = getPosition(models, model.getModel_no());
        position = position - 1;
        if (position < 0) {
            position = 0;
        }
        for (int i = 0; i < models.size(); i++) {
            if (model.getModel_no() == models.get(i).getModel_no()) {
                models.remove(i);
                break;
            }
        }
        models = arraySort(models, new ModelListItemComparator());
        list_view_adapter.flush(models);

        model_list.setSelection(position);
    }

    boolean checkModel(String model_name) {
        boolean reFlag = true;

        if (model_name == null || model_name.trim().length() == 0) {
            showErrorMessage("错误提示", "测试项名字没有输入");
            reFlag = false;
        }

        return reFlag;
    }

    private void showErrorMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    class ModelListItem {
        public CheckBox checked;
        public ImageView model_image;
        public TextView model_no;
        public TextView model_name;
        public TextView model_name_py;
        public ImageButton bt_delete;
        public ImageButton bt_update;
    }

}

class ModelListItemComparator implements Comparator<Object> {
    public final int compare(Object pFirst, Object pSecond) {
        String firstPY = ((Model) pFirst).getModel_name_py();
        String secondPY = ((Model) pSecond).getModel_name_py();
        if (firstPY.compareTo(secondPY) > 0) {
            return 1;
        } else if (firstPY.compareTo(secondPY) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}