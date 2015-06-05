package com.unionbigdata.takepicbuy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.utils.ExceptionManage;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

/**
 * 启动页面
 * Created by Christain on 15/4/19.
 */
public class StartActivity extends BaseActivity implements Runnable {

    private static final long START_DURATION = 2000;// 启动页持续时间
    private static final int START_PROGRESS_OVER = 12;
    private boolean needLogin = false, needGuide;
    private static final String LOGIN_TIMES = "LOGIN_TIMES";


    @Override
    protected int layoutResId() {
        return R.layout.start_activity;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        ExceptionManage.startInstance();// 启动错误管理
        new Thread(this).start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        // 初始化操作----------------------------------------------
        if (PhoneManager.isSdCardExit()) {
            Constant.checkSDPath();
        } else {
            handler.sendEmptyMessage(0);
        }

//        // 登录判断
//        int sid = AppPreference.getUserPersistentInt(this, AppPreference.LOGIN_TYPE);
//        if (sid != 0) {
//            needLogin = true;
//        } else {
//            needLogin = false;
//        }

        // 登录次数判断，用于是否显示导航页
        SharedPreferences timePreferences = StartActivity.this.getSharedPreferences(LOGIN_TIMES, Context.MODE_PRIVATE);
        int times = timePreferences.getInt("TIMES", 0);
        if (times == 0) {
            needGuide = true;
            SharedPreferences.Editor editor = timePreferences.edit();
            editor.putInt("TIMES", 1);
            editor.commit();
        } else {
            needGuide = false;
        }

        // 初始化结束-----------------------------------------------
        long endTime = System.currentTimeMillis();
        long diffTime = endTime - startTime;
        while (diffTime <= START_DURATION) {
            diffTime = System.currentTimeMillis() - startTime;
            /** 线程等待 **/
            Thread.yield();
        }
        handler.sendEmptyMessage(START_PROGRESS_OVER);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_PROGRESS_OVER:
                    Intent intent = null;
                    if (needGuide) {
                        intent = new Intent(StartActivity.this, GuideActivity.class);
                    } else {
                        intent = new Intent(StartActivity.this, IndexHome.class);
                    }
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                case 0:
                    Toast.makeText(StartActivity.this, "没有插入SD卡", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
