package com.ssp365.android.freelight.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.Sporter;

import java.util.ArrayList;

/**
 *
 */
public class DBSporterDAO {
    private static final String TAG = "DBSporterDAO";
    private SQLiteDatabase db;

    public DBSporterDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 添加新的运动员信息
     *
     * @param sporter
     */
    public Sporter add(Sporter sporter) {

        //取得新的运动员编号
        DBNoDAO dbNoDAO = new DBNoDAO(db);
        dbNoDAO.getNewNo(Parameter.NO_TYPE_SPORTER);
        int newSporterNo = dbNoDAO.getNo();

        sporter.setSporter_no(newSporterNo);

        String str = "insert into t_sporter (sporter_no,sporter_name," +
                "					sporter_name_py," +
                "					sporter_xingbie," +
                "					sporter_birthday," +
                "					sporter_team_no," +
                "					sporter_shengao," +
                "					sporter_tizhong) values (?,?,?,?,?,?,?,?)" +
                newSporterNo + ":" +
                sporter.getSporter_name() + ":" +
                sporter.getSporter_name_py() + ":" +
                sporter.getSporter_xingbie() + ":" +
                sporter.getSporter_birthday() + ":" +
                sporter.getSporter_team_no() + ":" +
                sporter.getSporter_shengao() + ":" +
                sporter.getSporter_tizhong();
        Log.i(TAG, str);
        db.execSQL("insert into t_sporter (sporter_no," +
                        "					sporter_name," +
                        "					sporter_name_py," +
                        "					sporter_xingbie," +
                        "					sporter_birthday," +
                        "					sporter_team_no," +
                        "					sporter_shengao," +
                        "					sporter_tizhong) values (?,?,?,?,?,?,?,?)",
                new Object[]{sporter.getSporter_no(),
                        sporter.getSporter_name(),
                        sporter.getSporter_name_py(),
                        sporter.getSporter_xingbie(),
                        sporter.getSporter_birthday(),
                        sporter.getSporter_team_no(),
                        sporter.getSporter_shengao(),
                        sporter.getSporter_tizhong()});

        return sporter;
    }

    /**
     * 更新运动员信息
     *
     * @param sporter
     */
    public void update(Sporter sporter) {
        try {
            String str = "update t_sporter set sporter_name = ?," +
                    "				sporter_name_py = ?, " +
                    "				sporter_xingbie = ?, " +
                    "				sporter_birthday = ?, " +
                    "				sporter_team_no = ?, " +
                    "				sporter_shengao = ?, " +
                    "				sporter_tizhong = ? " +
                    "	where sporter_no = ?" +
                    sporter.getSporter_name() + ":" +
                    sporter.getSporter_name_py() + ":" +
                    sporter.getSporter_xingbie() + ":" +
                    sporter.getSporter_birthday() + ":" +
                    sporter.getSporter_team_no() + ":" +
                    sporter.getSporter_shengao() + ":" +
                    sporter.getSporter_tizhong() + ":" +
                    sporter.getSporter_no();
            Log.i(TAG, str);

            db.execSQL("update t_sporter set sporter_name = ?," +
                            "				sporter_name_py = ?, " +
                            "				sporter_xingbie = ?, " +
                            "				sporter_birthday = ?, " +
                            "				sporter_team_no = ?, " +
                            "				sporter_shengao = ?, " +
                            "				sporter_tizhong = ? " +
                            "	where sporter_no = ?",
                    new Object[]{sporter.getSporter_name(),
                            sporter.getSporter_name_py(),
                            sporter.getSporter_xingbie(),
                            sporter.getSporter_birthday(),
                            sporter.getSporter_team_no(),
                            sporter.getSporter_shengao(),
                            sporter.getSporter_tizhong(),
                            sporter.getSporter_no()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找运动员信息
     *
     * @param sporter_no
     * @return Sporter
     */
    public Sporter find(int sporter_no) {
        Cursor cursor = db.rawQuery("select sporter_no," +
                        "							sporter_name," +
                        "							sporter_name_py," +
                        "							sporter_xingbie," +
                        "							sporter_birthday," +
                        "							sporter_team_no," +
                        "							sporter_shengao," +
                        "							sporter_tizhong " +
                        "					from t_sporter where sporter_no = ?",
                new String[]{String.valueOf(sporter_no)});
        if (cursor.moveToNext()) {
            Sporter sporter = new Sporter(cursor.getInt(cursor.getColumnIndex("sporter_no")),
                    cursor.getString(cursor.getColumnIndex("sporter_name")),
                    cursor.getString(cursor.getColumnIndex("sporter_name_py")),
                    cursor.getString(cursor.getColumnIndex("sporter_xingbie")),
                    cursor.getString(cursor.getColumnIndex("sporter_birthday")),
                    cursor.getInt(cursor.getColumnIndex("sporter_team_no")),
                    cursor.getString(cursor.getColumnIndex("sporter_shengao")),
                    cursor.getString(cursor.getColumnIndex("sporter_tizhong")));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return sporter;
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

    /**
     * 查找运动员信息
     *
     * @return ArrayList<Sporter>
     */
    public ArrayList<Sporter> find() {

        ArrayList<Sporter> list_team = new ArrayList<Sporter>();
        Cursor cursor = db.rawQuery("select sporter_no," +
                "							sporter_name," +
                "							sporter_name_py," +
                "							sporter_xingbie," +
                "							sporter_birthday," +
                "							sporter_team_no," +
                "							sporter_shengao," +
                "							sporter_tizhong from t_sporter;"
                , null);
        while (cursor.moveToNext()) {
            list_team.add(new Sporter(cursor.getInt(cursor.getColumnIndex("sporter_no")),
                    cursor.getString(cursor.getColumnIndex("sporter_name")),
                    cursor.getString(cursor.getColumnIndex("sporter_name_py")),
                    cursor.getString(cursor.getColumnIndex("sporter_xingbie")),
                    cursor.getString(cursor.getColumnIndex("sporter_birthday")),
                    cursor.getInt(cursor.getColumnIndex("sporter_team_no")),
                    cursor.getString(cursor.getColumnIndex("sporter_shengao")),
                    cursor.getString(cursor.getColumnIndex("sporter_tizhong"))));
            Log.i(TAG, "id:" + cursor.getInt(cursor.getColumnIndex("sporter_no")) +
                    " sporter_name:" + cursor.getString(cursor.getColumnIndex("sporter_name")) +
                    " sporter_name_py:" + cursor.getString(cursor.getColumnIndex("sporter_name_py")));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return list_team;
    }

    /**
     * 查找运动员编号信息
     *
     * @return int[]
     */
    public int[] findSporterNo() {
        ArrayList<Sporter> list_team = find();
        int[] sporter_nos = new int[list_team.size()];
        for (int i = 0; i < list_team.size(); i++) {
            sporter_nos[i] = list_team.get(i).getSporter_no();
        }
        return sporter_nos;
    }

    /**
     * 删除运动员信息
     *
     * @param
     */
    public void detele(Integer... sporter_nos) {
        if (sporter_nos.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < sporter_nos.length; i++) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            db.execSQL("delete from t_sporter where sporter_no in (" + sb + ")", sporter_nos);
        }
    }

    /**
     * 获取运动员数量
     *
     * @return int
     */
    public int getCount() {
        Cursor cursor = db.rawQuery("select count(sporter_no) from t_sporter", null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return count;
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return 0;
    }
}
