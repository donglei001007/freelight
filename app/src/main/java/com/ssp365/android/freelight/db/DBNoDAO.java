package com.ssp365.android.freelight.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 *
 */
public class DBNoDAO {
    private SQLiteDatabase db;

    private static final String TAG = "DBNoDAO";

    public DBNoDAO(SQLiteDatabase db) {
        this.db = db;
    }

    private int no;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    /**
     * 获得番号
     *
     * @param
     */
    public void getNewNo(String noType) {

        int no = 1;
        Cursor cursor = db.rawQuery("select no from t_no where no_type = ? ;",
                new String[]{noType});
        if (cursor.moveToNext()) {
            Log.i(TAG, "有数据！");
            no = cursor.getInt(cursor.getColumnIndex("no"));
            no++;
            db.execSQL("update t_no set no = ? where no_type = ? ;",
                    new Object[]{no, noType});
        } else {
            //初期值为1
            Log.i(TAG, "没有数据！");
            db.execSQL("insert into t_no(no_type,no) values(?,?);",
                    new Object[]{noType, no});
        }
        cursor.close();

        this.no = no;
    }

    /**
     * 获得番号
     *
     * @param
     */
    public int getNo(String noType) {
        int no = -1;
        Cursor cursor = db.rawQuery("select no from t_no where no_type = ? ;",
                new String[]{noType});
        if (cursor.moveToNext()) {
            no = cursor.getInt(cursor.getColumnIndex("no"));
        } else {
        }
        cursor.close();
        return no;
    }


}
