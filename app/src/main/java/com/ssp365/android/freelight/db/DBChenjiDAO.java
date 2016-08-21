package com.ssp365.android.freelight.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ssp365.android.freelight.model.Chenji;
import com.ssp365.android.freelight.model.ChenjiDetail;
import com.ssp365.android.freelight.model.CsvData;

import java.util.ArrayList;

/**
 *
 */
public class DBChenjiDAO {
    private static final String TAG = "DBChenjiDAO";
    private SQLiteDatabase db;

    public DBChenjiDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 添加成绩信息
     *
     * @param chenji
     */
    public void addChenji(Chenji chenji) {
        db.execSQL("insert into t_chenji (chenji_no,chenji_day,sporter_no,model_no,model_total_length,model_total_time,model_total_speed) " +
                        "values (?,?,?,?,?,?,?);",
                new Object[]{chenji.getChenji_no(),
                        chenji.getChenji_day(),
                        chenji.getSporter_no(),
                        chenji.getModel_no(),
                        chenji.getModel_total_length(),
                        chenji.getModel_total_time(),
                        chenji.getModel_total_speed()});
    }

    /**
     * 添加成绩详细信息
     *
     * @param chenjiDetail
     */
    public void addChenjiDetail(ChenjiDetail chenjiDetail) {
        db.execSQL("insert into t_chenji_detail (chenji_no,model_sub_no,model_sub_length,model_sub_time,model_sub_speed) " +
                        "values (?,?,?,?,?);",
                new Object[]{chenjiDetail.getChenji_no(),
                        chenjiDetail.getModel_sub_no(),
                        chenjiDetail.getModel_sub_length(),
                        chenjiDetail.getModel_sub_time(),
                        chenjiDetail.getModel_sub_speed()});
    }

    /**
     * 查找成绩信息
     *
     * @param sporter_no，model_no，day_str，day_end
     * @return ArrayList<Chenji>
     */
    public ArrayList<Chenji> findChenji(int[] sporter_no, int model_no, String day_str, String day_end) {
        //db = helper.getWritableDatabase();
        String str_sporter_no = "";
        String[] str_para = new String[sporter_no.length + 3];
        for (int i = 0; i < sporter_no.length; i++) {
            if (i == 0) {
                //str_sporter_no = "'" + sporter_no[i] + "'";
                str_sporter_no = "(a.sporter_no = ? ";
            } else {
                //str_sporter_no = str_sporter_no + ",'" + sporter_no[i] + "'";
                str_sporter_no = str_sporter_no + "or a.sporter_no = ? ";
            }
        }
        str_sporter_no = str_sporter_no + ")";
        String strSQL = "select a.chenji_no chenji_no,a.chenji_day chenji_day,a.sporter_no sporter_no,b.sporter_name sporter_name,a.model_no model_no," +
                "a.model_total_length model_total_length,a.model_total_time model_total_time,a.model_total_speed model_total_speed " +
                "from t_chenji a,t_sporter b where " + str_sporter_no + " and a.model_no = ? and (a.chenji_day >= ? and a.chenji_day <= ?)" +
                "and a.sporter_no = b.sporter_no order by a.sporter_no,a.chenji_day;";

        for (int i = 0; i < sporter_no.length; i++) {
            str_para[i] = String.valueOf(sporter_no[i]);
        }
        str_para[sporter_no.length] = String.valueOf(model_no);
        str_para[sporter_no.length + 1] = day_str;
        str_para[sporter_no.length + 2] = day_end;

        Log.i(TAG, "strSQL:" + strSQL);
        for (int i = 0; i < str_para.length; i++) {
            Log.i(TAG, "str_para:" + str_para[i]);
        }

        Cursor cursor = db.rawQuery(strSQL, str_para);
        ArrayList<Chenji> list_chenji = new ArrayList<Chenji>();
        while (cursor.moveToNext()) {
            list_chenji.add(new Chenji(cursor.getInt(cursor.getColumnIndex("chenji_no")),
                    cursor.getString(cursor.getColumnIndex("chenji_day")),
                    cursor.getInt(cursor.getColumnIndex("sporter_no")),
                    cursor.getString(cursor.getColumnIndex("sporter_name")),
                    cursor.getInt(cursor.getColumnIndex("model_no")),
                    cursor.getInt(cursor.getColumnIndex("model_total_length")),
                    cursor.getDouble(cursor.getColumnIndex("model_total_time")),
                    cursor.getDouble(cursor.getColumnIndex("model_total_speed"))));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_chenji;
    }

    /**
     * 查找成绩详细
     *
     * @param sporter_no，model_no，day_str，day_end
     * @return ArrayList<Chenji>
     */
    public ArrayList<Chenji> findChenjiDetail(int[] sporter_no, int model_no, String day_str, String day_end) {
        //db = helper.getWritableDatabase();
        String str_sporter_no = "";
        String[] str_para = new String[sporter_no.length + 3];
        for (int i = 0; i < sporter_no.length; i++) {
            if (i == 0) {
                //str_sporter_no = "'" + sporter_no[i] + "'";
                str_sporter_no = "(a.sporter_no = ? ";
            } else {
                //str_sporter_no = str_sporter_no + ",'" + sporter_no[i] + "'";
                str_sporter_no = str_sporter_no + "or a.sporter_no = ? ";
            }
        }
        str_sporter_no = str_sporter_no + ")";
        String strSQL = "select a.model_no model_no,a.sporter_no sporter_no,b.sporter_name sporter_name,a.chenji_day chenji_day,a.chenji_no chenji_no," +
                "a.model_total_length model_total_length,a.model_total_time model_total_time,a.model_total_speed model_total_speed," +
                "c.model_sub_no model_sub_no,c.model_sub_length model_sub_length,c.model_sub_time model_sub_time,c.model_sub_speed model_sub_speed " +
                "from t_chenji a,t_sporter b,t_chenji_detail c where " + str_sporter_no + " and a.model_no = ? and (a.chenji_day >= ? and a.chenji_day <= ?)" +
                "and a.sporter_no = b.sporter_no and a.chenji_no = c.chenji_no order by a.sporter_no,a.chenji_day,a.chenji_no,c.model_sub_no;";

        for (int i = 0; i < sporter_no.length; i++) {
            str_para[i] = String.valueOf(sporter_no[i]);
        }
        str_para[sporter_no.length] = String.valueOf(model_no);
        str_para[sporter_no.length + 1] = day_str;
        str_para[sporter_no.length + 2] = day_end;

        Log.i(TAG, "strSQL:" + strSQL);
        for (int i = 0; i < str_para.length; i++) {
            Log.i(TAG, "str_para:" + str_para[i]);
        }

        Cursor cursor = db.rawQuery(strSQL, str_para);
        ArrayList<Chenji> list_chenji = new ArrayList<Chenji>();
        //前回成绩号
        int chenji_no_bf = -1;
        Chenji chenji_tmp = null;
        ArrayList<ChenjiDetail> array_chenji_detail = new ArrayList<ChenjiDetail>();
        while (cursor.moveToNext()) {
            if (chenji_no_bf != cursor.getInt(cursor.getColumnIndex("chenji_no"))) {
                if (chenji_tmp != null) {
                    chenji_tmp.setChenji_detail(array_chenji_detail);
                    list_chenji.add(chenji_tmp);
                    chenji_tmp.println();
                    array_chenji_detail = new ArrayList<ChenjiDetail>();
                    chenji_no_bf = cursor.getInt(cursor.getColumnIndex("chenji_no"));
                } else {
                    chenji_no_bf = cursor.getInt(cursor.getColumnIndex("chenji_no"));
                }
                chenji_tmp = new Chenji(cursor.getInt(cursor.getColumnIndex("chenji_no")),
                        cursor.getString(cursor.getColumnIndex("chenji_day")),
                        cursor.getInt(cursor.getColumnIndex("sporter_no")),
                        cursor.getString(cursor.getColumnIndex("sporter_name")),
                        cursor.getInt(cursor.getColumnIndex("model_no")),
                        cursor.getInt(cursor.getColumnIndex("model_total_length")),
                        cursor.getDouble(cursor.getColumnIndex("model_total_time")),
                        cursor.getDouble(cursor.getColumnIndex("model_total_speed")));
            }
            ChenjiDetail chenji_detail = new ChenjiDetail(cursor.getInt(cursor.getColumnIndex("chenji_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_length")),
                    cursor.getDouble(cursor.getColumnIndex("model_sub_time")),
                    cursor.getDouble(cursor.getColumnIndex("model_sub_speed")));
            array_chenji_detail.add(chenji_detail);
        }
        //最后一条数据的处理
        if (chenji_tmp != null) {
            chenji_tmp.setChenji_detail(array_chenji_detail);
            list_chenji.add(chenji_tmp);
            array_chenji_detail = new ArrayList<ChenjiDetail>();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_chenji;
    }

    /**
     * 查找成绩详细信息
     *
     * @param chenji_no
     * @return ArrayList<ChenjiDetail>
     */
    public ArrayList<ChenjiDetail> find(int chenji_no) {
        //db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select chenji_no,model_sub_no,model_sub_length,model_sub_time,model_sub_speed " +
                        "from t_chenji_detail where chenji_no = ?;",
                new String[]{String.valueOf(chenji_no)});
        ArrayList<ChenjiDetail> list_chenjiDetail = new ArrayList<ChenjiDetail>();
        while (cursor.moveToNext()) {
            list_chenjiDetail.add(new ChenjiDetail(cursor.getInt(cursor.getColumnIndex("chenji_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_no")),
                    cursor.getInt(cursor.getColumnIndex("model_sub_length")),
                    cursor.getDouble(cursor.getColumnIndex("model_sub_time")),
                    cursor.getDouble(cursor.getColumnIndex("model_sub_speed"))));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_chenjiDetail;
    }

    /**
     * 查找成绩详细信息
     *
     * @param
     * @return ArrayList<CsvData>
     */
    public ArrayList<CsvData> find() {
        String sql = "select e.model_name model_name,d.team_name team_name,c.sporter_name sporter_name,a.chenji_no chenji_no," +
                "a.chenji_day chenji_day,a.model_total_speed model_total_speed,b.model_sub_speed model_sub_speed,a.model_no model_no," +
                "b.model_sub_no model_sub_no,c.sporter_team_no sporter_team_no,a.sporter_no sporter_no " +
                "from t_chenji a,t_chenji_detail b,t_sporter c,t_team d,t_model e " +
                "where a.chenji_no = b.chenji_no " +
                "and a.sporter_no = c.sporter_no " +
                "and c.sporter_team_no = d.team_no " +
                "and a.model_no = e.model_no " +
                "order by a.model_no,c.sporter_team_no,a.sporter_no,a.chenji_day,b.model_sub_no;";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<CsvData> list_csvData = new ArrayList<CsvData>();
        while (cursor.moveToNext()) {
            list_csvData.add(new CsvData(cursor.getString(cursor.getColumnIndex("model_name")),
                    cursor.getString(cursor.getColumnIndex("team_name")),
                    cursor.getString(cursor.getColumnIndex("sporter_name")),
                    cursor.getInt(cursor.getColumnIndex("chenji_no")),
                    cursor.getString(cursor.getColumnIndex("chenji_day")),
                    cursor.getDouble(cursor.getColumnIndex("model_total_speed")) + "",
                    cursor.getDouble(cursor.getColumnIndex("model_sub_speed")) + "",
                    cursor.getInt(cursor.getColumnIndex("model_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("model_sub_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("sporter_team_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("sporter_no")) + ""));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_csvData;
    }

    /**
     * 查找成绩详细信息
     *
     * @param
     * @return ArrayList<CsvData>
     */
    public ArrayList<CsvData> find(int[] sporter_no, int[] model_no, String day_str, String day_end) {

        String str_sporter_no = "";
        String[] str_para = new String[sporter_no.length + model_no.length + 2];
        for (int i = 0; i < sporter_no.length; i++) {
            if (i == 0) {
                str_sporter_no = "(c.sporter_no = ? ";
            } else {
                str_sporter_no = str_sporter_no + "or c.sporter_no = ? ";
            }
        }
        str_sporter_no = str_sporter_no + ")";

        day_str = day_str + " 00:00:00";
        day_end = day_end + " 24:00:00";

        String str_model_no = "";
        for (int i = 0; i < model_no.length; i++) {
            if (i == 0) {
                str_model_no = "(e.model_no = ? ";
            } else {
                str_model_no = str_model_no + "or e.model_no = ? ";
            }
        }
        str_model_no = str_model_no + ")";

        String strSQL = "select e.model_name model_name,d.team_name team_name,c.sporter_name sporter_name,a.chenji_no chenji_no," +
                "a.chenji_day chenji_day,a.model_total_speed model_total_speed,b.model_sub_speed model_sub_speed,a.model_no model_no," +
                "b.model_sub_no model_sub_no,c.sporter_team_no sporter_team_no,a.sporter_no sporter_no " +
                "from t_chenji a,t_chenji_detail b,t_sporter c,t_team d,t_model e " +
                "where a.chenji_no = b.chenji_no " +
                "and a.sporter_no = c.sporter_no " +
                "and c.sporter_team_no = d.team_no " +
                "and a.model_no = e.model_no and " +
                str_sporter_no + " and " + str_model_no + " and (a.chenji_day >= ? and a.chenji_day <= ?) " +
                "order by a.model_no,c.sporter_team_no,a.sporter_no,a.chenji_day,b.model_sub_no;";

        for (int i = 0; i < sporter_no.length; i++) {
            str_para[i] = String.valueOf(sporter_no[i]);
        }
        for (int i = 0; i < model_no.length; i++) {
            str_para[sporter_no.length + i] = String.valueOf(model_no[i]);
        }
        str_para[sporter_no.length + model_no.length] = day_str;
        str_para[sporter_no.length + model_no.length + 1] = day_end;

        Log.i(TAG, "strSQL:" + strSQL);
        for (int i = 0; i < str_para.length; i++) {
            Log.i(TAG, "str_para:" + str_para[i]);
        }

        Cursor cursor = db.rawQuery(strSQL, str_para);


        ArrayList<CsvData> list_csvData = new ArrayList<CsvData>();
        while (cursor.moveToNext()) {
            list_csvData.add(new CsvData(cursor.getString(cursor.getColumnIndex("model_name")),
                    cursor.getString(cursor.getColumnIndex("team_name")),
                    cursor.getString(cursor.getColumnIndex("sporter_name")),
                    cursor.getInt(cursor.getColumnIndex("chenji_no")),
                    cursor.getString(cursor.getColumnIndex("chenji_day")),
                    cursor.getDouble(cursor.getColumnIndex("model_total_speed")) + "",
                    cursor.getDouble(cursor.getColumnIndex("model_sub_speed")) + "",
                    cursor.getInt(cursor.getColumnIndex("model_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("model_sub_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("sporter_team_no")) + "",
                    cursor.getInt(cursor.getColumnIndex("sporter_no")) + ""));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_csvData;
    }

    /**
     * 成绩输出完成后，删除成绩
     *
     * @param chenji_no
     * @return void
     */
    public void delete(String[] chenji_no) {

        db.beginTransaction();
        //先删除模式信息
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chenji_no.length; i++) {
            sb.append('?').append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        //删除成绩信息
        db.execSQL("delete from t_chenji where model_no in (" + sb + ")", chenji_no);

        //删除成绩详细信息
        db.execSQL("delete from t_chenji_detail where chenji_no in (" + sb + ")", chenji_no);

        db.setTransactionSuccessful();
        db.endTransaction();

    }
}
