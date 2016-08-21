package com.ssp365.android.freelight.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.ModelDetail;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 */
public class DBModelDAO {
    private static final String TAG = "DBModelDAO";
    private SQLiteDatabase db;

    public DBModelDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 查找模式信息
     *
     * @param model_no
     * @return Team
     */
    public Model find(int model_no) {
        //db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji from t_model where model_no = ?",
                new String[]{String.valueOf(model_no)});
        if (cursor.moveToNext()) {
            Model model = new Model(cursor.getInt(cursor.getColumnIndex("model_no")),
                    cursor.getString(cursor.getColumnIndex("model_name")),
                    cursor.getString(cursor.getColumnIndex("model_name_py")),
                    cursor.getString(cursor.getColumnIndex("model_pic")),
                    cursor.getInt(cursor.getColumnIndex("model_count")),
                    cursor.getInt(cursor.getColumnIndex("model_point_count")),
                    cursor.getInt(cursor.getColumnIndex("model_length")),
                    cursor.getInt(cursor.getColumnIndex("model_fdji")));
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return model;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

    /**
     * 更新模式信息
     *
     * @param model
     */
    public void update(Model model) {
        db.execSQL("update t_model set model_name = ?,model_name_py = ? where model_no = ?",
                new Object[]{model.getModel_name(), model.getModel_name_py(), model.getModel_no()});
    }

    /**
     * 查找模式信息
     *
     * @return ArrayList<Model>
     */
    public ArrayList<Model> find() {
        //db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji from t_model order by model_name_py", null);
        ArrayList<Model> list_model = new ArrayList<Model>();
        while (cursor.moveToNext()) {
            list_model.add(new Model(cursor.getInt(cursor.getColumnIndex("model_no")),
                    cursor.getString(cursor.getColumnIndex("model_name")),
                    cursor.getString(cursor.getColumnIndex("model_name_py")),
                    cursor.getString(cursor.getColumnIndex("model_pic")),
                    cursor.getInt(cursor.getColumnIndex("model_count")),
                    cursor.getInt(cursor.getColumnIndex("model_point_count")),
                    cursor.getInt(cursor.getColumnIndex("model_length")),
                    cursor.getInt(cursor.getColumnIndex("model_fdji"))));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_model;
    }

    /**
     * 查找模式编号信息
     *
     * @return int[]
     */
    public int[] findModelNo() {
        ArrayList<Model> list_team = find();
        int[] model_nos = new int[list_team.size()];
        for (int i = 0; i < list_team.size(); i++) {
            model_nos[i] = list_team.get(i).getModel_no();
        }
        return model_nos;
    }

    /**
     * 查找模式详细信息
     *
     * @return ArrayList<Team>
     */
    public ArrayList<ModelDetail> findDetail(int model_no) {
        //db = helper.getWritableDatabase();
        Log.i(TAG, "model_no:" + model_no);

        Cursor cursor = db.rawQuery("select model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type from t_model_detail where model_no = ? order by model_pre_no",
                new String[]{String.valueOf(model_no)});
        ArrayList<ModelDetail> list_model_detail_tmp = new ArrayList<ModelDetail>();
        while (cursor.moveToNext()) {
            list_model_detail_tmp.add(new ModelDetail(cursor.getInt(cursor.getColumnIndex("model_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_no")),
                    cursor.getInt(cursor.getColumnIndex("model_pre_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_length")),
                    cursor.getString(cursor.getColumnIndex("model_sub_position_type")),
                    cursor.getString(cursor.getColumnIndex("model_sub_check_type"))));
            Log.i(TAG, "list_model_detail_tmp:" + list_model_detail_tmp.get(list_model_detail_tmp.size() - 1).getModel_no());

        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        //按照实际先后顺序，构造新的数据结构
        ArrayList<ModelDetail> list_model_detail = new ArrayList<ModelDetail>();
        list_model_detail.add(list_model_detail_tmp.get(0));
        list_model_detail_tmp.remove(0);
        int model_sub_no = list_model_detail.get(0).getModel_sub_no();
        for (int k = list_model_detail_tmp.size(); k > 0; k--) {
            for (int i = 0; i < list_model_detail_tmp.size(); i++) {
                if (model_sub_no == list_model_detail_tmp.get(i).getModel_pre_no()) {
                    list_model_detail.add(list_model_detail_tmp.get(i));
                    model_sub_no = list_model_detail_tmp.get(i).getModel_sub_no();
                    list_model_detail_tmp.remove(i);
                    break;
                }
            }
        }

        return list_model_detail;
    }

    //取得柱子通过顺序（用于只响应当前信号柱信号）
    public int[] getPointAccessArray(int model_no, int model_count) {

        Vector<String> vecPoint = new Vector<String>();
        int arrayIndex = 2;
        int[] pointArray = new int[model_count];
        pointArray[0] = 1;
        int nextPoint = findNextPoint(model_no, 1);
        if (nextPoint != 0) {
            pointArray[1] = nextPoint;
            vecPoint.add("1" + " " + nextPoint);
        }
        while (nextPoint != 0) {
            nextPoint = findNextPoint(model_no, nextPoint);
            if (nextPoint != 0) {
                boolean findFlag = false;
                for (int i = 0; i < vecPoint.size(); i++) {
                    //重复检出时，检索结束
                    if ((pointArray[arrayIndex - 1] + " " + nextPoint).equals(vecPoint.get(i))) {
                        findFlag = true;
                        break;
                    }
                }
                if (findFlag) {
                    break;
                }
                pointArray[arrayIndex] = nextPoint;
                vecPoint.add(pointArray[arrayIndex - 1] + " " + pointArray[arrayIndex]);
                arrayIndex++;
            }
        }
        for (int i = 0; i < pointArray.length; i++) {
            Log.i(TAG, "pointArray[:" + i + "]:" + pointArray[i]);
        }
        return pointArray;
    }

    /**
     * 取得下个信号柱的编号
     *
     * @return int
     */
    public int findNextPoint(int model_no, int model_pre_no) {
        Log.i(TAG, "model_pre_no:" + model_pre_no);
        int next_point = 0;
        Cursor cursor = db.rawQuery("select model_sub_no from t_model_detail where model_no = ? and model_pre_no = ?",
                new String[]{String.valueOf(model_no), String.valueOf(model_pre_no)});
        while (cursor.moveToNext()) {
            next_point = cursor.getInt(cursor.getColumnIndex("model_sub_no"));
            Log.i(TAG, "next_point:" + next_point);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return next_point;
    }

    //取得各柱子信号序列
    //model_count 模式计时次数
    //model_point_count 模式测试柱个数
    public String[][] getPointStateArray(int model_no, int model_count, int model_point_count) {

        Log.i(TAG, "model_count:" + model_count + " model_point_count:" + model_point_count);

        String[][] rePointStateArray = new String[model_point_count][model_count];

        Cursor cursor = db.rawQuery("select model_index,point_no,point_state from t_point_state where model_no = ? order by point_no,model_index",
                new String[]{String.valueOf(model_no)});
        String point_no = "";
        String point_state = "";
        int model_point_index = 0;
        int model_index = 0;
        while (cursor.moveToNext()) {
            point_no = cursor.getString(cursor.getColumnIndex("point_no"));
            point_state = cursor.getString(cursor.getColumnIndex("point_state"));
            Log.i(TAG, "point_no:" + point_no + " point_state:" + point_state);
            rePointStateArray[model_point_index][model_index] = point_state;
            model_index++;
            //一个测试柱的状态设置完了后，自动迁移到下个测试桩
            if (model_index == model_count) {
                model_point_index++;
                model_index = 0;
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return rePointStateArray;
    }

    //取得各柱子信号响应序列
    //model_count 模式计时次数
    //model_point_count 模式测试柱个数
    public String[][] getWaitStateArray(int model_no, int model_count, int model_point_count) {

        Log.i(TAG, "model_count:" + model_count + " model_point_count:" + model_point_count);

        String[][] reWaitStateArray = new String[model_point_count][model_count];

        Cursor cursor = db.rawQuery("select model_index,point_no,wait_state from t_point_state where model_no = ? order by point_no,model_index",
                new String[]{String.valueOf(model_no)});
        String point_no = "";
        String wait_state = "";
        int model_point_index = 0;
        int model_index = 0;
        while (cursor.moveToNext()) {
            point_no = cursor.getString(cursor.getColumnIndex("point_no"));
            wait_state = cursor.getString(cursor.getColumnIndex("wait_state"));
            Log.i(TAG, "point_no:" + point_no + " wait_state:" + wait_state);
            reWaitStateArray[model_point_index][model_index] = wait_state;
            model_index++;
            //一个测试柱的状态设置完了后，自动迁移到下个测试桩
            if (model_index == model_count) {
                model_point_index++;
                model_index = 0;
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return reWaitStateArray;
    }

    /**
     * 检查模式名称
     *
     * @param model
     */
    public boolean checkModelName(Model model) {
        //db = helper.getWritableDatabase();
        Cursor cursor = null;
        //更新处理的检查
        if (model.getModel_no() != 0) {
            cursor = db.rawQuery("select model_no,model_name,model_name_py from t_model where model_no <> ? and model_name = ?",
                    new String[]{String.valueOf(model.getModel_no()), model.getModel_name()});
            if (cursor.moveToNext()) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return false;
            }
            //添加处理的检查
        } else {
            cursor = db.rawQuery("select model_no,model_name,model_name_py from t_model where model_name = ?",
                    new String[]{model.getModel_name()});
            if (cursor.moveToNext()) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return false;
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return true;
    }

    /**
     * 添加新的模式信息
     *
     * @param model
     */
    public Model addModel(Model model) {
        Model reModel = null;

        db.beginTransaction();
        //先添加模式信息
        db.execSQL("insert into t_model (model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values (?,?,?,?,?,?)",
                new Object[]{model.getModel_name(), model.getModel_name_py(), model.getModel_pic(), model.getModel_count()
                        , model.getModel_length(), model.getModel_fdji()});

        Cursor cursor = db.rawQuery("select model_no,model_name,model_name_py,model_pic,model_count,model_length,model_fdji from t_model where model_name = ?",
                new String[]{model.getModel_name()});
        if (cursor.moveToNext()) {
            reModel = new Model(cursor.getInt(cursor.getColumnIndex("model_no")),
                    cursor.getString(cursor.getColumnIndex("model_name")),
                    cursor.getString(cursor.getColumnIndex("model_name_py")),
                    cursor.getString(cursor.getColumnIndex("model_pic")),
                    cursor.getInt(cursor.getColumnIndex("model_count")),
                    cursor.getInt(cursor.getColumnIndex("model_point_count")),
                    cursor.getInt(cursor.getColumnIndex("model_length")),
                    cursor.getInt(cursor.getColumnIndex("model_fdji")));
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        //后添加模式详细信息
        db.execSQL("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values (?,?,?,?,?,?)",
                new Object[]{reModel.getModel_no(), 100, 0, -1, 3, '1'});

        db.setTransactionSuccessful();
        db.endTransaction();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return reModel;
    }

    /**
     * 删除模式信息
     *
     * @param model_nos
     */
    public void delete(Integer... model_nos) {

        db.beginTransaction();
        //先删除模式信息
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < model_nos.length; i++) {
            sb.append('?').append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        db.execSQL("delete from t_model where model_no in (" + sb + ")", model_nos);

        //先删除模式详细信息
        db.execSQL("delete from t_model_detail where model_no in (" + sb + ")", model_nos);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 检查模式能否删除（如果该模式下还有成绩则提示是否确认删除）
     *
     * @param model
     */
    public boolean checkDeleteModel(Model model) {
        Cursor cursor = null;
        cursor = db.rawQuery("select chenji_no from t_chenji where model_no = ?",
                new String[]{String.valueOf(model.getModel_no())});
        if (cursor.moveToNext()) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return false;
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return true;
    }

    /**
     * 获取模式数量
     *
     * @return int
     */
    public int getCount() {
        //db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(model_no) from t_model", null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return count;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return 0;
    }
}
