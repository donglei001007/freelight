package com.ssp365.android.freelight.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.Team;

import java.util.ArrayList;

/**
 * 队伍数据库句柄
 */
public class DBTeamDAO {
    private SQLiteDatabase db;

    public DBTeamDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 检查队名是否重复
     *
     * @param team
     */
    public boolean checkTeamName(Team team) {
        Cursor cursor = null;
        //更新处理的检查
        if (team.getTeam_no() != 0) {
            cursor = db.rawQuery("select team_no,team_name,team_name_py from t_team where team_no <> ? and team_name = ?",
                    new String[]{String.valueOf(team.getTeam_no()), team.getTeam_name()});
            if (cursor.moveToNext()) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                return false;
            }
            //添加处理的检查
        } else {
            cursor = db.rawQuery("select team_no,team_name,team_name_py from t_team where team_name = ?",
                    new String[]{team.getTeam_name()});
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
     * 检查队伍能否删除（如果该队伍下还有队员便不可删除）
     *
     * @param team
     */
    public boolean checkDeleteTeam(Team team) {
        Cursor cursor = null;
        cursor = db.rawQuery("select sporter_no from t_sporter where sporter_team_no = ?",
                new String[]{String.valueOf(team.getTeam_no())});
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
     * 添加新的队伍信息
     *
     * @param team
     */
    public Team addTeam(Team team) {

        //取得新的队伍编号
        DBNoDAO dbNoDAO = new DBNoDAO(db);
        dbNoDAO.getNewNo(Parameter.NO_TYPE_TEAM);
        int newTeamNo = dbNoDAO.getNo();

        team.setTeam_no(newTeamNo);

        db.execSQL("insert into t_team (team_no,team_name,team_name_py) values (?,?,?)",
                new Object[]{team.getTeam_no(),
                        team.getTeam_name(),
                        team.getTeam_name_py()});

        return team;
    }

    /**
     * 更新队伍信息
     *
     * @param team
     */
    public void update(Team team) {
        db.execSQL("update t_team set team_name = ?,team_name_py = ? where team_no = ?",
                new Object[]{team.getTeam_name(), team.getTeam_name_py(), team.getTeam_no()});
    }

    /**
     * 查找队伍信息
     *
     * @param team_no
     * @return Team
     */
    public Team find(int team_no) {
        Cursor cursor = db.rawQuery("select team_no,team_name,team_name_py from t_team where team_no = ?",
                new String[]{String.valueOf(team_no)});
        if (cursor.moveToNext()) {
            Team team = new Team(cursor.getInt(cursor.getColumnIndex("team_no")),
                    cursor.getString(cursor.getColumnIndex("team_name")),
                    cursor.getString(cursor.getColumnIndex("team_name_py")));
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return team;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

    /**
     * 查找队伍信息
     *
     * @return ArrayList<Team>
     */
    public ArrayList<Team> find() {
        Cursor cursor = db.rawQuery("select team_no,team_name,team_name_py from t_team order by team_name_py", null);
        ArrayList<Team> list_team = new ArrayList<Team>();
        while (cursor.moveToNext()) {
            list_team.add(new Team(cursor.getInt(cursor.getColumnIndex("team_no")),
                    cursor.getString(cursor.getColumnIndex("team_name")),
                    cursor.getString(cursor.getColumnIndex("team_name_py"))));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list_team;
    }

    /**
     * 删除队伍信息
     *
     * @param team_nos
     */
    public void detele(Integer... team_nos) {
        if (team_nos.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < team_nos.length; i++) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            db.execSQL("delete from t_team where team_no in (" + sb + ")", team_nos);
        }
    }

    /**
     * 获取队伍数量
     *
     * @return int
     */
    public int getCount() {
        Cursor cursor = db.rawQuery("select count(team_no) from t_team", null);
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
