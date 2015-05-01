package com.unionbigdata.takepicbuy.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.fragment.GuideFragmentOne;
import com.unionbigdata.takepicbuy.fragment.GuideFragmentTwo;

import butterknife.InjectView;

/**
 * 引导页
 * Created by Christain on 15/4/20.
 */
public class GuideActivity extends BaseActivity {

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    private ViewPagerAdapter adapter;

    @Override
    protected int layoutResId() {
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        return R.layout.guide_activity;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(adapter);
        this.viewPager.setOffscreenPageLimit(0);
        this.viewPager.setCurrentItem(0);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            if (arg0 == 0) {
                GuideFragmentOne fragment = new GuideFragmentOne();
                return fragment;
            } else {
                GuideFragmentTwo fragment = new GuideFragmentTwo();
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
