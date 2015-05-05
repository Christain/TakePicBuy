package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.AppPreference;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.TakePicBuyApplication;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.dialog.DialogVersionUpdate;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.fragment.HomeFragment;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.VersionModel;
import com.unionbigdata.takepicbuy.params.HomeParams;
import com.unionbigdata.takepicbuy.params.UpdateVersionParam;
import com.unionbigdata.takepicbuy.utils.DoubleClickExitHelper;
import com.unionbigdata.takepicbuy.utils.PhoneManager;
import com.unionbigdata.takepicbuy.widget.ComposerLayout;
import com.unionbigdata.takepicbuy.widget.PullToRefreshViewPager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;
import christain.refreshlibrary.library.PullToRefreshBase;

/**
 * 首页
 * Created by Christain on 15/4/19.
 */
public class IndexHome extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ViewPager> {

    private DoubleClickExitHelper doubleClickExitHelper;

    @InjectView(R.id.path)
    ComposerLayout pathButton;
    @InjectView(R.id.view_pager)
    PullToRefreshViewPager refreshViewPager;
    private ViewPagerAdapter adapter;

    private MenuItem menuItemSet, menuItemUser;

    private int page = 0, size = 18;

    @Override
    protected int layoutResId() {
        this.doubleClickExitHelper = new DoubleClickExitHelper(this);
        return R.layout.index_home;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getToolbar().setTitle("");
        setSupportActionBar(getToolbar());
        pathButton.init(new int[]{R.mipmap.icon_select_pic_canmar, R.mipmap.icon_select_pic_album},
                R.mipmap.icon_path_red_bg,
                R.mipmap.icon_path_cross,
                ComposerLayout.LEFTBOTTOM,
                getResources().getDimensionPixelOffset(R.dimen.path_radius), 300);
        pathButton.setButtonsOnClickListener(new PathOnClickListener());

        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
        refreshViewPager.setOnRefreshListener(this);

        ViewPager viewPager = refreshViewPager.getRefreshableView();
        viewPager.setAdapter(adapter);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setCurrentItem(0);

        getVersionInfo();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ViewPager> refreshView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                   mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshViewPager.onRefreshComplete();
        }
    };

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ViewPager> refreshView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class PathOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == 100 + 0) {
                Intent intent = new Intent(IndexHome.this, CropImageActivity.class);
                intent.putExtra("TYPE","CAMERA");
                startActivity(intent);
            } else if (v.getId() == 100 + 1) {
                Intent intent = new Intent(IndexHome.this, CropImageActivity.class);
                intent.putExtra("TYPE","ALBUM");
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menuItemSet = menu.findItem(R.id.set);
        menuItemUser = menu.findItem(R.id.user);
        menuItemSet.setActionView(R.layout.menu_item_view);
        menuItemUser.setActionView(R.layout.menu_item_view);
        LinearLayout layout = (LinearLayout) menuItemSet.getActionView();
        ImageView btn = (ImageView) layout.findViewById(R.id.ivItem);
        btn.setImageResource(R.mipmap.icon_toolbar_set);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexHome.this, SetActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout userlayout = (LinearLayout) menuItemUser.getActionView();
        ImageView user = (ImageView) userlayout.findViewById(R.id.ivItem);
        user.setImageResource(R.mipmap.icon_toolbar_user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexHome.this, UserCenter.class);
                startActivity(intent);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            HomeFragment fragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE", arg0 + 1);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }

    /**
     * 获取首页图片
     */
    private void getHomeImage() {
        HomeParams params = new HomeParams(page, size);
        AsyncHttpTask.post(params.getUrl(), params, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {

            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {

            }
        });
    }

    /**
     * 获取版本信息
     */
    private void getVersionInfo() {
        UpdateVersionParam param = new UpdateVersionParam();
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("app").length() > 2) {
                        Gson gson = new Gson();
                        VersionModel versionModel = gson.fromJson(object.getString("app"), VersionModel.class);
                        if (versionModel.getCode() > PhoneManager.getVersionInfo().versionCode) {
                            AppPreference.setVersionInfo(IndexHome.this, versionModel);
                            updateVersionDialog(versionModel);
                        }
                        TakePicBuyApplication.getInstance().setCheckViersion(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        DialogVersionUpdate versionUpdate = new DialogVersionUpdate(IndexHome.this, R.style.dialog_untran);
        versionUpdate.withDuration(300).withEffect(Effectstype.Fadein).setCancel("以后再说").setSure("立即更新").setVersionName("最新版本：" + versionModel.getName()).setVersionSize("新版本大小：" + versionModel.getSize()).setVersionContent(versionModel.getDescri()).setOnSureClick(new DialogVersionUpdate.OnUpdateClickListener() {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
