package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.Bundle;
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

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.fragment.HomeFragment;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.params.HomeParams;
import com.unionbigdata.takepicbuy.utils.DoubleClickExitHelper;
import com.unionbigdata.takepicbuy.widget.ComposerLayout;
import com.unionbigdata.takepicbuy.widget.PullToRefreshViewPager;

import org.apache.http.Header;

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
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ViewPager> refreshView) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refreshViewPager.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ViewPager> refreshView) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refreshViewPager.onRefreshComplete();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
