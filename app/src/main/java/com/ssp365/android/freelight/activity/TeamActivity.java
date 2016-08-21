package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.db.DBTeamDAO;
import com.ssp365.android.freelight.dialog.SimpleDialog;
import com.ssp365.android.freelight.inf.SimpleDialogListener;
import com.ssp365.android.freelight.model.Pinyin4jUtil;
import com.ssp365.android.freelight.model.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TeamActivity extends Activity {

    protected static final String TAG = "TeamActivity";

    //队伍对话框标示
    public static final int DIALOG_TEAM_NAME_ERROR = 1;
    public static final int DIALOG_TEAM_DELETE_CONFIRM = 2;

    //队伍按钮
    private TextView bt_team_add;
    private ImageButton bt_team_back;
    //队伍列表
    private ListView team_list;

    //明细内情报：队伍编号、队伍名称
    private ArrayList<Team> teams;
    TeamListAdapter list_view_adapter = null;

    //对话框中队伍编号
    public int item_selected_team_no = -1;
    //对话框中队伍名称
    public String item_selected_team_name = "";

    public SmartSportApplication mApplication = null;
    //DB关联
    DBTeamDAO teamDAO = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_activity);
        team_list = (ListView) findViewById(R.id.list_team);

        mApplication = (SmartSportApplication) getApplication();
        teamDAO = new DBTeamDAO(mApplication.getDb());
        teams = teamDAO.find();

        list_view_adapter = new TeamListAdapter(teams, this);
        team_list.setAdapter(list_view_adapter);

        //添加队伍按钮的处理
        bt_team_add = (TextView) findViewById(R.id.button_team_add);
        bt_team_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialog dialog = new SimpleDialog(TeamActivity.this, new SimpleDialogListener() {
                    @Override
                    public void onOkClick(String name) {
                        //队名输入检查
                        if (!checkTeam(name)) {
                            return;
                        }
                        //队名重复的检查
                        Team team = new Team(0, name, Pinyin4jUtil.getPinYinHeadChar(name));
                        if (!teamDAO.checkTeamName(team)) {
                            showErrorMessage("错误提示", "队名重复，请重新输入队名");
                            return;
                        }
                        team = teamDAO.addTeam(team);
                        addArrayList(team);
                        Toast.makeText(TeamActivity.this, "队伍添加成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelClick() {
                    }
                }, R.style.dialog, "请输入添加的队名");
                dialog.show();
            }
        });
        //设定完了按钮的处理
        bt_team_back = (ImageButton) findViewById(R.id.button_team_back);
        bt_team_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

	/*
    @Override
	protected Dialog onCreateDialog(int id){
		Log.i(TAG,"onCreateDialog");
		AlertDialog.Builder builder = null;
		builder = new AlertDialog.Builder(this);
		switch (id){
			case DIALOG_TEAM_NAME_ERROR:
				builder.setMessage("队名重复，请重新输入队名！");
				// 返回键是否可以关闭对话框
				builder.setCancelable(true);
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				});
				return builder.create();
			case DIALOG_TEAM_DELETE_CONFIRM:
				builder.setMessage("");
				// 返回键是否可以关闭对话框
				builder.setCancelable(false);
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						teamDAO.detele(item_selected_team_no);
						Team team = new Team(item_selected_team_no, null,null);
						deleteArrayList(team);
						Toast.makeText(TeamActivity.this, "队伍删除成功！" , Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				});
				return builder.create();
			default:
				break;
			}
			return null;
	}
	*/

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        Log.i(TAG, "onPrepareDialog");
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DIALOG_TEAM_DELETE_CONFIRM:
                Log.i(TAG, "DIALOG_TEAM_DELETE_CONFIRM");
                ((AlertDialog) dialog).setMessage("确认删除队伍：" + item_selected_team_name + "？");
        }
    }


    class TeamListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Team> teamListObjects;

        public TeamListAdapter(ArrayList<Team> teams, Context context) {
            super();
            teamListObjects = teams;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (null != teamListObjects) {
                return teamListObjects.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return teamListObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TeamListItem teamListItem;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.team_activity_item, null);
                teamListItem = new TeamListItem();
                teamListItem.team_no = (TextView) convertView.findViewById(R.id.team_no);
                teamListItem.team_name = (TextView) convertView.findViewById(R.id.team_name);
                teamListItem.team_name_py = (TextView) convertView.findViewById(R.id.team_name_py);
                teamListItem.bt_delete = (ImageButton) convertView.findViewById(R.id.button_team_delete);
                teamListItem.bt_delete.setFocusable(false);
                teamListItem.bt_update = (ImageButton) convertView.findViewById(R.id.button_team_update);
                teamListItem.bt_update.setFocusable(false);
                convertView.setTag(teamListItem);
            } else {
                teamListItem = (TeamListItem) convertView.getTag();
            }

            Log.i(TAG, "" + teamListObjects.get(position).getTeam_no());
            Log.i(TAG, "" + teamListObjects.get(position).getTeam_name());
            Log.i(TAG, "" + teamListObjects.get(position).getTeam_name_py());
            Log.i(TAG, teamListItem.toString());
            Log.i(TAG, teamListItem.team_no.toString());

            teamListItem.team_no.setText(teamListObjects.get(position).getTeam_no() + "");
            teamListItem.team_name.setText(teamListObjects.get(position).getTeam_name());
            teamListItem.team_name_py.setText(teamListObjects.get(position).getTeam_name_py());
            teamListItem.bt_delete.setTag(R.id.DIALOG_TAT_TEAM_NO, teamListObjects.get(position).getTeam_no());
            teamListItem.bt_delete.setTag(R.id.DIALOG_TAT_TEAM_NAME, teamListObjects.get(position).getTeam_name());
            teamListItem.bt_update.setTag(R.id.DIALOG_TAT_TEAM_NO, teamListObjects.get(position).getTeam_no());
            teamListItem.bt_update.setTag(R.id.DIALOG_TAT_TEAM_NAME, teamListObjects.get(position).getTeam_name());
            teamListItem.bt_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_selected_team_no = ((Integer) v.getTag(R.id.DIALOG_TAT_TEAM_NO)).intValue();
                    item_selected_team_name = (String) v.getTag(R.id.DIALOG_TAT_TEAM_NAME);
                    //确认该队伍下是否还有队员
                    if (!teamDAO.checkDeleteTeam(new Team(item_selected_team_no,
                            item_selected_team_name,
                            Pinyin4jUtil.getPinYinHeadChar(item_selected_team_name)))) {
                        showErrorMessage("错误提示", "该队伍下还有队员，不能删除");
                        return;
                    }

                    new AlertDialog.Builder(TeamActivity.this)
                            .setTitle("删除确认")
                            .setMessage("确实要删除队伍：" + item_selected_team_name + "？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    teamDAO.detele(item_selected_team_no);
                                    Team team = new Team(item_selected_team_no, null, null);
                                    deleteArrayList(team);
                                    Toast.makeText(TeamActivity.this, "队伍删除成功！", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create().show();

                    //showDialog(DIALOG_TEAM_DELETE_CONFIRM);
                }
            });
            teamListItem.bt_update.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_selected_team_no = ((Integer) v.getTag(R.id.DIALOG_TAT_TEAM_NO)).intValue();
                    item_selected_team_name = (String) v.getTag(R.id.DIALOG_TAT_TEAM_NAME);
                    SimpleDialog dialog = new SimpleDialog(inflater.getContext(), new SimpleDialogListener() {
                        @Override
                        public void onOkClick(String name) {
                            //队名输入检查
                            if (!checkTeam(name)) {
                                return;
                            }
                            //队名重复的检查
                            Team team = new Team(item_selected_team_no, name, Pinyin4jUtil.getPinYinHeadChar(name));
                            if (!teamDAO.checkTeamName(team)) {
                                //showDialog(DIALOG_TEAM_NAME_ERROR);
                                showErrorMessage("错误提示", "队名重复，请重新输入队名");
                                return;
                            }
                            teamDAO.update(team);
                            changArrayList(team);
                            Toast.makeText(TeamActivity.this, "队伍修改成功！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelClick() {
                        }
                    }, R.style.dialog, "请输入修改后的队名", item_selected_team_name);
                    dialog.show();
                }
            });
            Log.i("MainActivity", "<<<<<<<" + position);
            return convertView;
        }

        public void flush(ArrayList<Team> teams) {
            teamListObjects = teams;
            notifyDataSetChanged();
        }

    }

    public void changArrayList(Team team) {
        for (int i = 0; i < teams.size(); i++) {
            if (team.getTeam_no() == teams.get(i).getTeam_no()) {
                teams.set(i, team);
                break;
            }
        }
        teams = arraySort(teams, new TeamListItemComparator());
        list_view_adapter.flush(teams);

        team_list.setSelection(getPosition(teams, team.getTeam_no()));
    }

    public void addArrayList(Team team) {
        teams.add(team);
        teams = arraySort(teams, new TeamListItemComparator());
        list_view_adapter.flush(teams);

        team_list.setSelection(getPosition(teams, team.getTeam_no()));
    }

    public void deleteArrayList(Team team) {
        int position = getPosition(teams, team.getTeam_no());
        position = position - 1;
        if (position < 0) {
            position = 0;
        }

        for (int i = 0; i < teams.size(); i++) {
            if (team.getTeam_no() == teams.get(i).getTeam_no()) {
                teams.remove(i);
                break;
            }
        }
        if (teams.size() > 0) {
            teams = arraySort(teams, new TeamListItemComparator());
        }
        list_view_adapter.flush(teams);

        team_list.setSelection(position);
    }

    class TeamListItem {
        public TextView team_no;
        public TextView team_name;
        public TextView team_name_py;
        public ImageButton bt_delete;
        public ImageButton bt_update;
    }

    private ArrayList<Team> arraySort(ArrayList<Team> arrayList_team, TeamListItemComparator teamListItemComparator) {
        Log.i(TAG, "排序前:");
        for (int i = 0; i < arrayList_team.size(); i++) {
            Log.i(TAG, arrayList_team.get(i).getTeam_name() + ":" + arrayList_team.get(i).getTeam_name_py());
        }

        Team[] team_list = arrayList_team.toArray(new Team[1]);
        Arrays.sort(team_list, teamListItemComparator);
        arrayList_team = new ArrayList<Team>();
        for (int i = 0; i < team_list.length; i++) {
            arrayList_team.add(team_list[i]);
        }

        Log.i(TAG, "排序后:");
        for (int i = 0; i < arrayList_team.size(); i++) {
            Log.i(TAG, arrayList_team.get(i).getTeam_name() + ":" + arrayList_team.get(i).getTeam_name_py());
        }

        return arrayList_team;
    }

    private int getPosition(ArrayList<Team> arrayList_team, int no) {
        for (int i = 0; i < arrayList_team.size(); i++) {
            if (no == arrayList_team.get(i).getTeam_no()) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkTeam(String team_name) {
        boolean reFlag = true;

        if (team_name == null || team_name.trim().length() == 0) {
            showErrorMessage("错误提示", "队伍名字没有输入");
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


}

class TeamListItemComparator implements Comparator<Object> {
    public final int compare(Object pFirst, Object pSecond) {
        String firstPY = ((Team) pFirst).getTeam_name_py();
        String secondPY = ((Team) pSecond).getTeam_name_py();
        if (firstPY.compareTo(secondPY) > 0) {
            return 1;
        } else if (firstPY.compareTo(secondPY) < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}