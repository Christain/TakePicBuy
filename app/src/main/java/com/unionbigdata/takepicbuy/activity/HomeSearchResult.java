package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.adapter.HomeSearchResultAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.SearchResultListModel;
import com.unionbigdata.takepicbuy.params.HomePicSearchParam;

import org.apache.http.Header;

import butterknife.InjectView;

/**
 * 首页搜索结果
 * Created by Christain on 15/5/9.
 */
public class HomeSearchResult extends BaseActivity {

    @InjectView(R.id.gridView)
    GridView gridView;
    @InjectView(R.id.llLoading)
    LinearLayout llLoading;
    @InjectView(R.id.llNoResult)
    LinearLayout llNoResult;
    @InjectView(R.id.tvNoResult)
    TextView tvNoResult;
    @InjectView(R.id.llBack)
    LinearLayout llBack;
    @InjectView(R.id.tvBack)
    TextView tvBack;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;

    private Animation alphaIn;
    private String imageId;
    private HomeSearchResultAdapter adapter;

    @Override
    protected int layoutResId() {
        return R.layout.home_search_result;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("IAMGEID")) {
            this.imageId = intent.getStringExtra("IAMGEID");
            this.alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
            getToolbar().setTitle("");
//            getToolbar().setTitleTextColor(0xFFFFFFFF);
//            getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
            llBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            tvTitle.setText("搜索结果");
            tvBack.setText("返回");

            setSupportActionBar(getToolbar());
//            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    finish();
//                }
//            });
            this.adapter = new HomeSearchResultAdapter(HomeSearchResult.this);
            this.gridView.setAdapter(adapter);
            this.initView();
        } else {
            toast("搜索图片信息错误");
            finish();
        }
    }

    private void initView() {
        dialogVisible();
        getHomeSearchList();
    }

    /**
     * 获取首页图片搜索结果
     */
    private void getHomeSearchList() {
        HomePicSearchParam param = new HomePicSearchParam(imageId);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                SearchResultListModel list = gson.fromJson(result, SearchResultListModel.class);
                if (list != null && list.getSearchresult() != null) {
                    if (list.getSearchresult().size() != 0) {
                        adapter.setHomeSearchList(list.getSearchresult());
                        rightVisible();
                    } else {
                        failVisible("没有搜索到类似商品");
                    }
                } else {
                    failVisible("没有搜索到类似商品，请重试");
                }
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                failVisible("搜索失败，请重试");
            }
        });
    }

    /**
     * 正确显示
     */
    private void rightVisible() {
        if (llLoading.isShown()) {
            llLoading.setVisibility(View.GONE);
        }
        if (llNoResult.isShown()) {
            llNoResult.setVisibility(View.GONE);
        }
        if (!gridView.isShown()) {
            gridView.setVisibility(View.VISIBLE);
            gridView.startAnimation(alphaIn);
        }
    }

    /**
     * 错误显示
     */
    private void failVisible(String msg) {
        if (llLoading.isShown()) {
            llLoading.setVisibility(View.GONE);
        }
        if (!llNoResult.isShown()) {
            llNoResult.setVisibility(View.VISIBLE);
            tvNoResult.setText(msg);
            llNoResult.startAnimation(alphaIn);
        }
        if (gridView.isShown()) {
            gridView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载中显示
     */
    private void dialogVisible() {
        if (llNoResult.isShown()) {
            llNoResult.setVisibility(View.GONE);
        }
        if (!llLoading.isShown()) {
            llLoading.setVisibility(View.VISIBLE);
        }
        if (gridView.isShown()) {
            gridView.setVisibility(View.GONE);
        }
    }
}
