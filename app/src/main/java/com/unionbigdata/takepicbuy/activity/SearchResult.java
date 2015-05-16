package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.adapter.SearchResultAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.baseclass.SuperAdapter;
import com.unionbigdata.takepicbuy.dialog.LoadingDialog;
import com.unionbigdata.takepicbuy.http.OnAdapterLoadMoreOverListener;
import com.unionbigdata.takepicbuy.http.OnAdapterRefreshOverListener;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.widget.PullToRefreshLayout;
import com.unionbigdata.takepicbuy.widget.PullableGridView;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 搜索结果
 * Created by Christain on 2015/4/20.
 */
public class SearchResult extends BaseActivity {

//    private MenuItem menuItemSet, menuItemUser;

    @InjectView(R.id.tvType)
    TextView tvType;
    @InjectView(R.id.gridView)
    PullableGridView gridView;
    @InjectView(R.id.head_view)
    RelativeLayout refreshHeader;
    @InjectView(R.id.loadmore_view)
    RelativeLayout refreshFooter;
    @InjectView(R.id.refreshView)
    PullToRefreshLayout refreshLayout;

    @InjectView(R.id.llLoading)
    LinearLayout llLoading;
    @InjectView(R.id.llNoResult)
    LinearLayout llNoResult;
    @InjectView(R.id.tvNoResult)
    TextView tvNoResult;

    private SearchResultAdapter adapter;
    private PopupWindow popWind;
    private String filterString = "all";//平台分类
    private int type = 0;
    private String imgUrl = "";//搜索图片的url
    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.search_result;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("IMGURL")) {
            imgUrl = intent.getStringExtra("IMGURL");
            this.mLoadingDialog = LoadingDialog.createDialog(SearchResult.this, true);
            getToolbar().setTitle("");
            getToolbar().setTitleTextColor(0xFFFFFFFF);
            getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
            setSupportActionBar(getToolbar());
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            this.refreshHeader = (RelativeLayout) findViewById(R.id.head_view);
            this.refreshFooter = (RelativeLayout) findViewById(R.id.loadmore_view);
            this.refreshHeader.setBackgroundColor(0xFFF1F1F1);
            this.refreshFooter.setBackgroundColor(0xFFF1F1F1);

            this.adapter = new SearchResultAdapter(SearchResult.this);
            this.gridView.setAdapter(adapter);

            this.refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

                }

                @Override
                public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                    adapter.loadMore();
                }
            });
            this.adapter.setRefreshOverListener(new OnAdapterRefreshOverListener() {
                @Override
                public void refreshOver(int code, String msg) {
                    switch (type) {
                        case 0:
                            tvType.setText("全部结果");
                            break;
                        case 1:
                            tvType.setText("淘宝");
                            break;
                        case 2:
                            tvType.setText("天猫");
                            break;
                        case 3:
                            tvType.setText("京东");
                            break;
                    }
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    if (llLoading.isShown()) {
                        llLoading.setVisibility(View.GONE);
                    }
                    gridView.setContentOver(adapter.getIsOver());
                    gridView.smoothScrollToPosition(0);
                    if (code == -1) {
                        if (!llNoResult.isShown()) {
                            llNoResult.setVisibility(View.VISIBLE);
                            tvNoResult.setText("  搜索商品失败");
                        }
                        if (refreshLayout.isShown()) {
                            refreshLayout.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (msg.equals(SuperAdapter.ISNULL)) {
                            if (!llNoResult.isShown()) {
                                llNoResult.setVisibility(View.VISIBLE);
                                tvNoResult.setText("没有搜索到商品");
                            }
                            if (refreshLayout.isShown()) {
                                refreshLayout.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (llNoResult.isShown()) {
                                llNoResult.setVisibility(View.GONE);
                            }
                            if (!refreshLayout.isShown()) {
                                refreshLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
            this.adapter.setLoadMoreOverListener(new OnAdapterLoadMoreOverListener() {
                @Override
                public void loadMoreOver(int code, String msg) {
                    gridView.setContentOver(adapter.getIsOver());
                    refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    if (code == -1) {
                        toast("加载失败，请重试");
                    } else {
                        if (msg.equals(SuperAdapter.ISNULL)) {
                            if (!llNoResult.isShown()) {
                                llNoResult.setVisibility(View.VISIBLE);
                                tvNoResult.setText("没有搜索到商品");
                            }
                            if (refreshLayout.isShown()) {
                                refreshLayout.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (llNoResult.isShown()) {
                                llNoResult.setVisibility(View.GONE);
                            }
                            if (!refreshLayout.isShown()) {
                                refreshLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
            llLoading.setVisibility(View.VISIBLE);
            adapter.searchResultList(imgUrl, filterString);
        } else {
            toast("无效的搜索");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search_result, menu);
//        menuItemSet = menu.findItem(R.id.set);
//        menuItemUser = menu.findItem(R.id.user);
//        menuItemSet.setActionView(R.layout.menu_item_view);
//        menuItemUser.setActionView(R.layout.menu_item_view);
//        LinearLayout setLayout = (LinearLayout) menuItemSet.getActionView();
//        ImageView set = (ImageView) setLayout.findViewById(R.id.ivItem);
//        set.setImageResource(R.mipmap.icon_toolbar_set);
//        set.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SearchResult.this, SetActivity.class);
//                startActivity(intent);
//            }
//        });
//        LinearLayout userlayout = (LinearLayout) menuItemUser.getActionView();
//        ImageView user = (ImageView) userlayout.findViewById(R.id.ivItem);
//        user.setImageResource(R.mipmap.icon_toolbar_user);
//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SearchResult.this, UserCenter.class);
//                startActivity(intent);
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.llType)
    void OnTypeClick() {
        if (!ClickUtil.isFastClick()) {
            popUpMyOverflow();
        }
    }

    private void popUpMyOverflow() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int xOffset = frame.top + getToolbar().getHeight() - getToolbar().getHeight() / 2 - 10;
        int yOffset = -20;
        View parentView = getLayoutInflater().inflate(R.layout.toolbar_search_result, null);
        View popView = getLayoutInflater().inflate(R.layout.dialog_select_type, null);

        TextView mTextViews[] = new TextView[4];
        mTextViews[0] = (TextView) popView.findViewById(R.id.tvAll);
        mTextViews[1] = (TextView) popView.findViewById(R.id.tvTaobao);
        mTextViews[2] = (TextView) popView.findViewById(R.id.tvTMall);
        mTextViews[3] = (TextView) popView.findViewById(R.id.tvJD);
//        mTextViews[4] = (TextView) popView.findViewById(R.id.tvMoGu);
//        mTextViews[5] = (TextView) popView.findViewById(R.id.tvBeautiful);

        for (int i = 0; i < 4; i++) {
            if (type == i) {
                mTextViews[i].setTextColor(0xFFFE5C71);
            } else {
                mTextViews[i].setTextColor(0xFF919191);
            }
            mTextViews[i].setOnClickListener(new TabOnClickListener(i));
        }
        popWind = new PopupWindow(popView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popWind.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        popWind.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popWind.setOutsideTouchable(true);
        popWind.setAnimationStyle(android.R.style.Animation_Dialog);
        popWind.showAtLocation(parentView, Gravity.TOP, yOffset, xOffset);
        backgroundAlpha(0.4f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 平台点击
     */
    private class TabOnClickListener implements View.OnClickListener {
        private int index = 0;

        public TabOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            if (!ClickUtil.isFastClick()) {
                if (type != index) {
                    switch (index) {
                        case 0:
                            type = index;
                            filterString = "all";
                            clickPupWindow();
                            break;
                        case 1:
                            type = index;
                            filterString = "taobao";
                            clickPupWindow();
                            break;
                        case 2:
                            type = index;
                            filterString = "tmall";
                            clickPupWindow();
                            break;
                        case 3:
                            type = index;
                            filterString = "jd";
                            clickPupWindow();
                            break;
//                        case 4:
//                            type = index;
//                            filterString = "";
//                            break;
//                        case 5:
//                            type = index;
//                            filterString = "";
//                            break;
                    }
                }
                if (popWind != null && popWind.isShowing()) {
                    popWind.dismiss();
                }
            }
        }

        private void clickPupWindow() {
            if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                mLoadingDialog.setMessage("加载中...");
                mLoadingDialog.show();
            }
            adapter.searchResultList(imgUrl, filterString);
        }
    }
}
