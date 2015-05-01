package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unionbigdata.takepicbuy.AppPreference;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.dialog.DialogChoiceBuilder;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.dialog.LoadingDialog;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 设置
 * Created by Christain on 15/4/19.
 */
public class SetActivity extends BaseActivity {

    @InjectView(R.id.tvClean)
    TextView tvClean;
    @InjectView(R.id.tvVersion)
    TextView tvVersion;
    @InjectView(R.id.llFeedback)
    LinearLayout llFeedback;
    @InjectView(R.id.llWeibo)
    LinearLayout llWeibo;
    @InjectView(R.id.llWechat)
    LinearLayout llWechat;
    @InjectView(R.id.tvExit)
    TextView tvExit;

    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.set_activity;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.mLoadingDialog = LoadingDialog.createDialog(SetActivity.this, true);
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        getToolbar().setTitle(R.string.set);
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick ({R.id.tvClean, R.id.tvVersion, R.id.llFeedback, R.id.llWeibo, R.id.llWechat, R.id.tvExit})
    void OnItemClick(View v) {
        if (!ClickUtil.isFastClick()) {
            switch (v.getId()) {
                case R.id.tvClean:
                    final DialogChoiceBuilder clear = DialogChoiceBuilder.getInstance(SetActivity.this);
                    clear.setMessage("您确定要清理缓存？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            new ProgressClear().execute();
                            clear.dismiss();
                        }
                    }).show();
                    break;
                case R.id.tvVersion:
                    toast("检测版本更新");
                    break;
                case R.id.llFeedback:
                    Intent intent = new Intent(SetActivity.this, Feedback.class);
                    startActivity(intent);
                    break;
                case R.id.llWeibo:
                    Intent weibo = new Intent(SetActivity.this, WebViewActivity.class);
                    weibo.putExtra("URL", "http://weibo.com/paitogo");
                    weibo.putExtra("TITLE", "拍图购微博");
                    startActivity(weibo);
                    break;
                case R.id.llWechat:
                    Intent qrcode = new Intent(SetActivity.this, QRCodeOfficial.class);
                    startActivity(qrcode);
                    break;
                case R.id.tvExit:
                    final DialogChoiceBuilder dialog = DialogChoiceBuilder.getInstance(SetActivity.this);
                    dialog.setMessage("您确定要退出登录？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            dialog.dismiss();
                            AppPreference.clearInfo(SetActivity.this);
                            exitApp();
                        }
                    }).show();
                    break;
            }
        }
    }

    /**
     * 清理缓存
     */
    private class ProgressClear extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.setMessage("正在清理缓存");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File file = new File(PhoneManager.getAppRootPath());
            if (file != null && file.exists()) {
                deleteFile(file);
            } else {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("缓存文件不存在");
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            if (result) {
                toast("清理完成");
            } else {
                toast("清理缓存失败，请重试");
            }
        }
    }

    private void deleteFile(File file) {
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                deleteFile(f);
            }
            file.delete();
        }
    }
}
