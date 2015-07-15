package com.example.ja.diudiu;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ja.diudiu.bean.Found;
import com.example.ja.diudiu.bean.Lost;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by JA on 2015/7/14.
 */
public class AddActivity extends BaseActivity implements View.OnClickListener {
    EditText edit_title, edit_photo, edit_describe;
    Button btn_back, btn_true;
    TextView tvAdd;
    String from = "";
    String old_title = "";
    String oldDescribe = "";
    String old_phone = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.aty_add);
    }

    @Override
    public void initViews() {
        tvAdd = (TextView) findViewById(R.id.tv_add);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_true = (Button) findViewById(R.id.btn_true);
        edit_photo = (EditText) findViewById(R.id.edit_photo);
        edit_describe = (EditText) findViewById(R.id.edit_describe);
        edit_title = (EditText) findViewById(R.id.edit_title);

    }

    @Override
    public void initListeners() {
        btn_back.setOnClickListener(this);
        btn_true.setOnClickListener(this);

    }

    @Override
    public void initData() {
        from = getIntent().getStringExtra("from");
        old_title = getIntent().getStringExtra("title");
        old_phone = getIntent().getStringExtra("phone");
        oldDescribe = getIntent().getStringExtra("describe");
        edit_title.setText(old_title);
        edit_describe.setText(oldDescribe);
        edit_photo.setText(old_phone);
        if (from.equals("Lost")) {
            tvAdd.setText("添加丢失的物品信息");
        } else {
            tvAdd.setText("添加捡到的物品信息");
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btn_true) {
            addByType();
        } else if (v == btn_back) {
            finish();
        }
    }

    String title = "";
    String describe = "";
    String photo = "";

    private void addByType() {
        title = edit_title.getText().toString();
        describe = edit_describe.getText().toString();
        photo = edit_photo.getText().toString();
        if (TextUtils.isEmpty(title)) {
            showToast("请填写标题");
            return;
        }
        if (TextUtils.isEmpty(describe)) {
            showToast("请填写描述");
            return;
        }
        if (TextUtils.isEmpty(photo)) {
            showToast("请填写手机号码");
            return;
        }
        if (from.equals("Lost")) {
            addLost();
        } else {
            addFound();
        }
    }

    private void addLost() {
        Lost lost = new Lost();
        lost.setDescribe(describe);
        lost.setPhone(photo);
        lost.setTitle(title);
        lost.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("丢失的物品信息添加完成！");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("添加失败:" + s);
            }
        });
    }

    private void addFound() {
        Found found = new Found();
        found.setDescribe(describe);
        found.setPhone(photo);
        found.setTitle(title);
        found.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("招领的物品信息添加完成！");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("添加失败:" + s);
            }
        });
    }


}
