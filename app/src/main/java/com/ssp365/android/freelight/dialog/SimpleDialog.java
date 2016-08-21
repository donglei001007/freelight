package com.ssp365.android.freelight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.inf.SimpleDialogListener;

public class SimpleDialog extends Dialog implements Button.OnClickListener {
    private Button bt_ok, bt_cancel;
    private EditText et_team_name;
    private TextView tv_team_name;
    private String team_name, team_message;

    private SimpleDialogListener listener;

    public SimpleDialog(Context context, SimpleDialogListener listener, int theme, String team_message, String team_name) {
        super(context, theme);
        this.listener = listener;
        this.team_message = team_message;
        this.team_name = team_name;
    }

    public SimpleDialog(Context context, SimpleDialogListener listener, int theme, String team_message) {
        super(context, theme);
        this.listener = listener;
        this.team_message = team_message;
    }

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.simple_dialog);
        tv_team_name = (TextView) findViewById(R.id.message);
        tv_team_name.setText(team_message);
        et_team_name = (EditText) findViewById(R.id.name);
        et_team_name.setText(team_name);
        bt_ok = (Button) findViewById(R.id.button_ok);
        bt_cancel = (Button) findViewById(R.id.button_cancel);
        bt_ok.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                listener.onOkClick(et_team_name.getText().toString());
                // 关闭Dialog
                dismiss();
                break;
            case R.id.button_cancel:
                // 取消Dialog, "取消"的含义是指不再需要执行对话框上的任何功能和动作, 取消对话框会自动调用dismiss()方法
                cancel();
                break;
        }
    }
}
