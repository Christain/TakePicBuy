package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.AppPreference;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.TakePicBuyApplication;
import com.unionbigdata.takepicbuy.adapter.HomeAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.baseclass.SuperAdapter;
import com.unionbigdata.takepicbuy.dialog.DialogVersionUpdate;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.OnAdapterLoadMoreOverListener;
import com.unionbigdata.takepicbuy.http.OnAdapterRefreshOverListener;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.VersionModel;
import com.unionbigdata.takepicbuy.params.UpdateVersionParam;
import com.unionbigdata.takepicbuy.utils.DoubleClickExitHelper;
import com.unionbigdata.takepicbuy.utils.PhoneManager;
import com.unionbigdata.takepicbuy.widget.ComposerLayout;
import com.unionbigdata.takepicbuy.widget.PullToRefreshLayout;
import com.unionbigdata.takepicbuy.widget.PullableListView;

import org.apache.http.Header;

import butterknife.InjectView;

/**
 * 首页
 * Created by Christain on 15/4/19.
 */
public class IndexHome extends BaseActivity {

    private DoubleClickExitHelper doubleClickExitHelper;

    @InjectView(R.id.path)
    ComposerLayout pathButton;
    @InjectView(R.id.listView)
    PullableListView listView;
    @InjectView(R.id.head_view)
    RelativeLayout refreshHeader;
    @InjectView(R.id.loadmore_view)
    RelativeLayout refreshFooter;
    @InjectView(R.id.refreshView)
    PullToRefreshLayout refreshLayout;

    private LinearLayout llFooter;
    private LinearLayout llEmpty;
    private LinearLayout llLoading;
    private TextView tvEmpty;
    private boolean isFooterVisible = false;

    private View footerView;
    private HomeAdapter adapter;
    private MenuItem menuItemSet, menuItemUser;
    private int toolbarHigh;


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

        this.footerView = LayoutInflater.from(IndexHome.this).inflate(R.layout.empty_view, null);
        this.llFooter = (LinearLayout) footerView.findViewById(R.id.llFooter);
        this.llEmpty = (LinearLayout) footerView.findViewById(R.id.llEmpty);
        this.llLoading = (LinearLayout) footerView.findViewById(R.id.llLoading);
        this.tvEmpty = (TextView) footerView.findViewById(R.id.empty_text);

        this.refreshHeader = (RelativeLayout) findViewById(R.id.head_view);
        this.refreshFooter = (RelativeLayout) findViewById(R.id.loadmore_view);
        this.refreshHeader.setBackgroundColor(0xFFF1F1F1);
        this.refreshFooter.setBackgroundColor(0xFFF1F1F1);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            toolbarHigh = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        this.adapter = new HomeAdapter(IndexHome.this, toolbarHigh);
        this.listView.addFooterView(footerView);
        this.listView.setAdapter(adapter);

        this.refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter.getHomeList(1);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                adapter.loadMore();
            }
        });
        this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
            @Override
            public void refreshOver(int code, String msg) {
                listView.setContentOver(adapter.getIsOver());
                refreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                if (code == -1) {
                    if (!isFooterVisible) {
                        EmptyViewVisible();
                    }
                    tvEmpty.setText("获取出错啦~~");
                } else {
                    if (msg.equals(SuperAdapter.ISNULL)) {
                        if (!isFooterVisible) {
                            EmptyViewVisible();
                        }
                        tvEmpty.setText("没有获取到数据");
                    } else {
                        if (isFooterVisible) {
                            EmptyViewGone();
                        }
                    }
                }
            }
        });
        this.adapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
            @Override
            public void loadMoreOver(int code, String msg) {
                listView.setContentOver(adapter.getIsOver());
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                if (code == -1) {
                    toast("加载失败，请重试");
                } else {
                    if (msg.equals(SuperAdapter.ISNULL)) {
                        if (!isFooterVisible) {
                            EmptyViewVisible();
                        }
                        tvEmpty.setText("没有获取到数据");
                    } else {
                        if (isFooterVisible) {
                            EmptyViewGone();
                        }
                    }
                }
            }
        });
        LoadingVisible();
        adapter.getHomeList(1);
        getVersionInfo();
    }

    private void LoadingVisible() {
        listView.setContentOver(true);
        isFooterVisible = true;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.footerView.setLayoutParams(new AbsListView.LayoutParams(metrics.widthPixels, metrics.heightPixels - PhoneManager.getStatusBarHigh() - toolbarHigh));
        llLoading.setVisibility(View.VISIBLE);
        llEmpty.setVisibility(View.GONE);
        llFooter.setVisibility(View.VISIBLE);
    }

    private void EmptyViewVisible() {
        listView.setContentOver(true);
        isFooterVisible = true;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.footerView.setLayoutParams(new AbsListView.LayoutParams(metrics.widthPixels, metrics.heightPixels - PhoneManager.getStatusBarHigh() - toolbarHigh));
        llLoading.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
        llFooter.setVisibility(View.VISIBLE);
    }

    private void EmptyViewGone() {
        isFooterVisible = false;
        this.footerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1));
        llFooter.setVisibility(View.GONE);
    }

    private class PathOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == 100 + 0) {
                Intent intent = new Intent(IndexHome.this, CropImage.class);
                intent.putExtra("TYPE", "CAMERA");
                startActivity(intent);
            } else if (v.getId() == 100 + 1) {
                Intent intent = new Intent(IndexHome.this, CropImage.class);
                intent.putExtra("TYPE", "ALBUM");
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
                        AppPreference.setVersionInfo(IndexHome.this, versionModel);
                        updateVersionDialog(versionModel);
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
        DialogVersionUpdate versionUpdate = new DialogVersionUpdate(IndexHome.this, R.style.dialog_untran);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
