package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.unionbigdata.takepicbuy.AppPreference;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.adapter.SearchPicAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.dialog.LoadingDialog;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.SearchPicModel;
import com.unionbigdata.takepicbuy.model.UserInfoModel;
import com.unionbigdata.takepicbuy.params.LoginParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 个人中心
 * Created by Christain on 2015/4/20.
 */
public class UserCenter extends BaseActivity {

    @InjectView(R.id.ivUserIcon)
    SimpleDraweeView ivUserIcon;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.rlHeader)
    RelativeLayout rlHeader;
    @InjectView(R.id.gridView)
    GridView gridView;
    @InjectView(R.id.llNoSearch)
    LinearLayout llNosearch;
    @InjectView(R.id.llSearchBar)
    LinearLayout llSearchBar;
    @InjectView(R.id.llNoLogin)
    LinearLayout llNoLogin;
    @InjectView(R.id.llHasLogin)
    LinearLayout llHasLogin;

    private SearchPicAdapter adapter;
    private boolean isCancelStatus = false, hasLogin = false;
    private ArrayList<SearchPicModel> list;

    /**
     * 第三方登录
     */
    private Tencent mTencent;
    private IUiListener listener;
    private WeiboAuth mWeiboAuth;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;

    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.user_center;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.mLoadingDialog = LoadingDialog.createDialog(UserCenter.this, true);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlHeader.getLayoutParams();
        params.width = PhoneManager.getDisplayMetrics(UserCenter.this).widthPixels;
        params.height = PhoneManager.getDisplayMetrics(UserCenter.this).heightPixels - PhoneManager.getStatusBarHigh() - params.width - getResources().getDimensionPixelOffset(R.dimen.user_center_search_item_high) + getResources().getDimensionPixelOffset(R.dimen.user_center_gridview_padding);
        rlHeader.setLayoutParams(params);

        this.hasLogin = AppPreference.hasLogin(UserCenter.this);
        if (hasLogin) {
            isLoginView();
        } else {
            noLoginView();
        }

    }

    /**
     * 已登录显示
     */
    private void isLoginView() {
        llHasLogin.setVisibility(View.VISIBLE);
        llNoLogin.setVisibility(View.GONE);
        ivUserIcon.setImageURI(Uri.parse(AppPreference.getUserPersistent(UserCenter.this, AppPreference.USER_PHOTO)));
        tvUserName.setText(AppPreference.getUserPersistent(UserCenter.this, AppPreference.NICK_NAME));
        this.adapter = new SearchPicAdapter(UserCenter.this, "9");
        this.gridView.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                list = searchList(new File(Constant.UPLOAD_FILES_DIR_PATH));
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 未登录显示
     */
    private void noLoginView() {
        llHasLogin.setVisibility(View.GONE);
        llNoLogin.setVisibility(View.VISIBLE);
        ivUserIcon.setImageURI(Uri.parse(""));
        tvUserName.setText("");
        if (mTencent == null) {
            this.mTencent = Tencent.createInstance(Constant.TENCENT_APPID, this.getApplicationContext());
            this.mWeiboAuth = new WeiboAuth(this, Constant.SINA_APPID, Constant.SINA_CALLBACK_URL, Constant.SINA_SCOPE);
        }
    }

    @OnClick({R.id.ibtBack, R.id.ibtSet, R.id.tvSearch, R.id.rlHeader, R.id.rlSina, R.id.rlTencent})
    void OnItemClick(View view) {
        if (!ClickUtil.isFastClick()) {
            if (isCancelStatus) {
                CancelDismiss();
                return;
            }
            switch (view.getId()) {
                case R.id.ibtBack:
                    finish();
                    break;
                case R.id.ibtSet:
                    Intent intent = new Intent(UserCenter.this, SetActivity.class);
                    startActivityForResult(intent, 500);
                    break;
                case R.id.tvSearch:
                    if (list != null && list.size() > 0) {
                        Intent search = new Intent(UserCenter.this, SearchHistory.class);
                        search.putExtra("LIST", list);
                        startActivityForResult(search, 100);
                    } else {
                        toast("您还没有搜索过图片");
                    }
                    break;
                case R.id.rlSina:
                    SinaOauth();
                    break;
                case R.id.rlTencent:
                    TencentOauth();
                    break;
            }
        }
    }

    /**
     * 搜索历史的显示
     */
    private void searchHistory() {
        if (list != null && list.size() > 0) {
            llNosearch.setVisibility(View.GONE);
            llSearchBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.VISIBLE);
            adapter.setSearchList(list);

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (!isCancelStatus) {
                        isCancelStatus = true;
                        for (int j = 0; j < list.size(); j++) {
                            SearchPicModel model = adapter.getItem(j);
                            model.setStatus(1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (isCancelStatus) {
                        CancelDismiss();
                    } else {
                        if (!ClickUtil.isFastClick()) {
                            Intent intent = new Intent(UserCenter.this, SearchResult.class);
                            intent.putExtra("IMGURL", getImageUrl(position));
                            startActivity(intent);
                        }
                    }
                }
            });
        } else {
            llNosearch.setVisibility(View.VISIBLE);
            llSearchBar.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
        }
    }

    /**
     * 根据文件名，获得图片在服务器的url
     */
    private String getImageUrl(int position) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.paitogo.com:80/upload/");
        String filePath = adapter.getItem(position).getImg();
        sb.append(filePath.substring(filePath.lastIndexOf("/"), filePath.lastIndexOf("_")));
        sb.append(".jpg");
        return sb.toString();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    searchHistory();
                    break;
            }
        }
    };

    /**
     * 获取SD卡图片搜索历史
     *
     * @param file
     * @return
     */
    private ArrayList<SearchPicModel> searchList(File file) {
        ArrayList<SearchPicModel> list = new ArrayList<SearchPicModel>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getAbsolutePath().endsWith("_SEARCH.jpg")) {
                    SearchPicModel model = new SearchPicModel();
                    model.setImg(f.getAbsolutePath());
                    model.setStatus(0);
                    model.setLastModified(f.lastModified());
                    list.add(model);
                }
            }
        }
        Collections.sort(list, new FileComparator());
        return list;
    }

    /**
     * 图片文件按时间排序的比较
     */
    public class FileComparator implements Comparator<SearchPicModel> {
        public int compare(SearchPicModel file1, SearchPicModel file2) {
            if (file1.getLastModified() < file2.getLastModified()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 登录
     */
    private void Login(final int type, final String opendId, final String access_token) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("登录中...");
            mLoadingDialog.show();
        }
        LoginParam param = new LoginParam(type, opendId, access_token);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                if (type == 0) {
                    try {
                        JSONObject object = new JSONObject(result);
                        UserInfoModel model = new UserInfoModel();
                        model.setName(object.getString("screen_name"));
                        model.setUser_photo(object.getString("avatar_large"));
                        AppPreference.saveUserInfo(UserCenter.this, model);
                        AppPreference.saveThirdLoginInfo(UserCenter.this, AppPreference.TYPE_SINA, mAccessToken.getUid(), mAccessToken.getToken(), mAccessToken.getExpiresTime());
                        isLoginView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast("登录失败，请重试");
                    }
                } else if (type == 1) {
                    try {
                        JSONObject object = new JSONObject(result);
                        UserInfoModel model = new UserInfoModel();
                        model.setName(object.getString("nickname"));
                        model.setUser_photo(object.getString("figureurl_qq_2"));
                        toast("object.getString(\"figureurl_qq_2\")");
                        AppPreference.saveUserInfo(UserCenter.this, model);
                        AppPreference.saveThirdLoginInfo(UserCenter.this, AppPreference.TYPE_QQ, opendId, access_token, Long.parseLong(AppPreference.getUserPersistent(UserCenter.this, AppPreference.QQ_EXPIRES)));
                        isLoginView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toast("登录失败，请重试");
                    }
                }
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("登录失败，请重试");
            }
        });
    }

    /**
     * 清除图片的删除按钮
     */
    private void CancelDismiss() {
        if (list != null) {
            for (int j = 0; j < list.size(); j++) {
                SearchPicModel model = adapter.getItem(j);
                model.setStatus(0);
            }
            adapter.notifyDataSetChanged();
            isCancelStatus = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCancelStatus) {
                CancelDismiss();
            } else {
                finish();
            }
        }
        return true;
    }

    /**
     * QQ登录验证
     */
    private void TencentOauth() {
        listener = new IUiListener() {
            @Override
            public void onCancel() {
                toast("QQ登录取消");
            }

            @Override
            public void onComplete(Object arg0) {
                try {
                    JSONObject object = (JSONObject) arg0;
                    String openid = object.getString("openid");
                    String token = object.getString("access_token");
                    String expires_in = object.getString("expires_in");
                    AppPreference.save(UserCenter.this, AppPreference.QQ_EXPIRES, expires_in);
                    Login(1, openid, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError arg0) {
                toast("QQ登录错误");
            }
        };
        mTencent.login(UserCenter.this, "get_simple_userinfo", listener);
    }

    /**
     * 新浪微博登录验证
     */
    private void SinaOauth() {
        if (checkSinaPackage()) {
            mSsoHandler = new SsoHandler(UserCenter.this, mWeiboAuth);
            mSsoHandler.authorize(new AuthListener());
        } else {
            mWeiboAuth.anthorize(new AuthListener());
        }
    }

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (Constant.SHOW_LOG) {
                Log.e("微博验证", "uid: " + mAccessToken.getUid() + "\n" + "token:" + mAccessToken.getToken() + "\n" + "expirestime: " + mAccessToken.getExpiresTime());
            }
            Login(0, mAccessToken.getUid(), mAccessToken.getToken());
        }

        @Override
        public void onCancel() {
            toast("微博登录取消");
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    }

    /**
     * 检测新浪微博是否安装
     */
    private boolean checkSinaPackage() {
        PackageManager manager = getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase("com.sina.weibo")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                mTencent.handleLoginData(data, listener);
            }
        }
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100://历史搜索页有删除操作
                    if (data.hasExtra("LIST")) {
                        list = (ArrayList<SearchPicModel>) data.getSerializableExtra("LIST");
                        adapter.setSearchList(list);
                    }
                    break;
                case 500://设置页有退出操作
                    if (data != null) {
                        if (data.hasExtra("LOGIN")) {
                            noLoginView();
                        }
                        if (data.hasExtra("HISTORY")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    list = searchList(new File(Constant.UPLOAD_FILES_DIR_PATH));
                                    handler.sendEmptyMessage(0);
                                }
                            }).start();
                        }
                    }
                    break;
            }
        }
    }
}
