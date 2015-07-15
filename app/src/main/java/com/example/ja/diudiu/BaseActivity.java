package com.example.ja.diudiu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.example.ja.diudiu.config.Constants;

import cn.bmob.v3.Bmob;

/**
 * Created by JA on 2015/7/14.
 */
public abstract class BaseActivity extends Activity {
    protected int mScreenWidth;
    protected int mScreenHeight;
    public static final String TAG = "bmob";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, Constants.Bmob_APPID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //获取当前屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        setContentView();
        initViews();
        initListeners();
        initData();
    }

    public abstract void setContentView();

    public abstract void initViews();

    public abstract void initListeners();

    public abstract void initData();

    Toast mToast;
    public void showToast (String text) {
        if(!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public int getStateBar() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale*dipValue + 0.5f);
    }
}
