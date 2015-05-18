package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.AppPreference;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.TakePicBuyApplication;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.dialog.DialogChoiceBuilder;
import com.unionbigdata.takepicbuy.dialog.DialogTipsBuilder;
import com.unionbigdata.takepicbuy.dialog.DialogVersionUpdate;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.dialog.LoadingDialog;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.VersionModel;
import com.unionbigdata.takepicbuy.params.UpdateVersionParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import org.apache.http.Header;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 设置
 * Created by Christain on 15/4/19.
 */
public class SetActivity extends BaseActivity {

    @InjectView(R.id.ivVersion)
    ImageView ivVersion;
    @InjectView(R.id.tvExit)
    TextView tvExit;
    @InjectView(R.id.tvHistory)
    TextView tvHistory;
    @InjectView(R.id.historyLine)
    View historyLine;

    private LoadingDialog mLoadingDialog;
    private DialogTipsBuilder dialog;
    private boolean hasLogin = true, loginStatusChanged = false, historyClear = false;

    @Override
    protected int layoutResId() {
        return R.layout.set_activity;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.mLoadingDialog = LoadingDialog.createDialog(SetActivity.this, true);
        this.dialog = DialogTipsBuilder.getInstance(SetActivity.this);
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        getToolbar().setTitle(R.string.set);
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginStatusChanged || historyClear) {
                    Intent intent = new Intent();
                    if (loginStatusChanged) {
                        intent.putExtra("LOGIN", "");
                    }
                    if (historyClear) {
                        intent.putExtra("HISTORY", "");
                    }
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
        this.initView();
    }

    private void initView() {
        hasLogin = AppPreference.hasLogin(SetActivity.this);
        if (hasLogin) {
            tvExit.setVisibility(View.VISIBLE);
            tvHistory.setVisibility(View.VISIBLE);
            historyLine.setVisibility(View.VISIBLE);
        } else {
            tvExit.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            historyLine.setVisibility(View.GONE);
        }
        if (!TakePicBuyApplication.getInstance().isCheckViersion()) {
            getVersionInfo();
        } else {
            if (AppPreference.getUserPersistentInt(SetActivity.this, AppPreference.VERSION_CODE) > PhoneManager.getVersionInfo().versionCode) {
                ivVersion.setVisibility(View.VISIBLE);
            } else {
                if (ivVersion.isShown()) {
                    ivVersion.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.tvClean, R.id.tvHistory, R.id.llVersion, R.id.llFeedback, R.id.llWeibo, R.id.llWechat, R.id.tvExit})
    void OnItemClick(View v) {
        if (!ClickUtil.isFastClick()) {
            switch (v.getId()) {
                case R.id.tvClean:
                    final DialogChoiceBuilder clear = DialogChoiceBuilder.getInstance(SetActivity.this);
                    clear.setMessage("您确定要清除缓存？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            clear.dismiss();
                            new ProgressClear().execute();
                        }
                    }).show();
                    break;
                case R.id.tvHistory:
                    final DialogChoiceBuilder history = DialogChoiceBuilder.getInstance(SetActivity.this);
                    history.setMessage("您确定要清除搜索记录？").withDuration(300).withEffect(Effectstype.Fadein).setOnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            history.dismiss();
                            new ProgressClearHistory().execute();
                        }
                    }).show();
                    break;
                case R.id.llVersion:
                    if (ivVersion.isShown() && TakePicBuyApplication.getInstance().isCheckViersion()) {
                        updateVersionDialog(AppPreference.getVersionInfo(SetActivity.this));
                    } else {
                        toast("当前已是最新版本");
                    }
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
                            hasLogin = false;
                            loginStatusChanged = true;
                            tvExit.setVisibility(View.GONE);
                            tvHistory.setVisibility(View.GONE);
                            historyLine.setVisibility(View.GONE);
                        }
                    }).show();
                    break;
            }
        }
    }

    /**
     * 获取版本信息
     */
    private void getVersionInfo() {
        UpdateVersionParam param = new UpdateVersionParam();
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                VersionModel versionModel = gson.fromJson(result, VersionModel.class);
                if (versionModel != null) {
                    if (versionModel.getCode() > PhoneManager.getVersionInfo().versionCode) {
                        AppPreference.setVersionInfo(SetActivity.this, versionModel);
                        ivVersion.setVisibility(View.VISIBLE);
                    } else {
                        if (ivVersion.isShown()) {
                            ivVersion.setVisibility(View.GONE);
                        }
                    }
                    TakePicBuyApplication.getInstance().setCheckViersion(true);
                } else {
                    TakePicBuyApplication.getInstance().setCheckViersion(false);
                }
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                TakePicBuyApplication.getInstance().setCheckViersion(false);
            }
        });
    }

    /**
     * 版本更新
     */
    private void updateVersionDialog(final VersionModel versionModel) {
        DialogVersionUpdate versionUpdate = new DialogVersionUpdate(SetActivity.this, R.style.dialog_untran);
        versionUpdate.withDuration(300).withEffect(Effectstype.Fadein).setCancel("以后再说").setSure("立即更新").setVersionName("最新版本：" + versionModel.getName()).setVersionSize("新版本大小：" + versionModel.getSize() + "M").setVersionContent(versionModel.getDescri()).setOnSureClick(new DialogVersionUpdate.OnUpdateClickListener() {
            @Override
            public void onUpdateListener() {
                if (versionModel.getVer_url().startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionModel.getVer_url()));
                    startActivity(intent);
                } else {
                    toast("更新地址错误");
                }
            }
        }).show();
    }

    /**
     * 清理缓存
     */
    private class ProgressClear extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.setMessage("正在清除");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File file = new File(PhoneManager.getAppRootPath());
            if (file.exists()) {
                deleteFile(file);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            if (result) {
                toast("清除完成");
            } else {
//                toast("清理缓存失败，请重试");
            }
        }
    }

    /**
     * 清除搜索记录
     */
    private class ProgressClearHistory extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.setMessage("正在清除");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            File file = new File(Constant.UPLOAD_FILES_DIR_PATH);
            if (file.exists()) {
                deleteFile(file);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            if (result) {
                historyClear = true;
                toast("清除完成");
            } else {
//                toast("清理缓存失败，请重试");
            }
        }
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
//            file.delete();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (loginStatusChanged || historyClear) {
                Intent intent = new Intent();
                if (loginStatusChanged) {
                    intent.putExtra("LOGIN", "");
                }
                if (historyClear) {
                    intent.putExtra("HISTORY", "");
                }
                setResult(RESULT_OK, intent);
            }
            finish();
        }
        return true;
    }
}
