package com.example.ja.diudiu;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by JA on 2015/7/14.
 */
public class SplashActivity extends BaseActivity {
    private  static final int GO_HOME = 100;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_splash);

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        mHanderler.sendEmptyMessageDelayed(GO_HOME, 3000);
    }
    private Handler mHanderler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
            }
        }
    };

    public void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
