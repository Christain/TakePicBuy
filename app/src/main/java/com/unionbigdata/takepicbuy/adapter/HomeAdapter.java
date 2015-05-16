package com.unionbigdata.takepicbuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.HomeSearchResult;
import com.unionbigdata.takepicbuy.baseclass.SuperAdapter;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.HomeListModel;
import com.unionbigdata.takepicbuy.model.HomeModel;
import com.unionbigdata.takepicbuy.model.HomeOrgModel;
import com.unionbigdata.takepicbuy.params.HomeParams;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import org.apache.http.Header;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 首页数据Adapter
 * Created by Christain on 15/5/9.
 */
public class HomeAdapter extends SuperAdapter {

    private int page = 1, maxPage, plate;
    public int limit = 18;
    private ResponseHandler responseHandler;
    private Context mContext;
    private int toolbarHigh;
    private ArrayList<Integer> overStyle;

    public HomeAdapter(Context context, int toolbarHigh) {
        super(context);
        this.mContext = context;
        this.toolbarHigh = toolbarHigh;
        this.initListener();
        this.overStyle = new ArrayList<Integer>();
    }

    private void initListener() {
        responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                Gson gson = new Gson();
                HomeListModel list = gson.fromJson(result, HomeListModel.class);
                int totalNum = list.getTotalrecord();
                if (totalNum % limit == 0) {
                    maxPage = totalNum / limit;
                } else {
                    maxPage = (totalNum / limit) + 1;
                }
                if (list != null && list.getObj() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (list.getObj().size() == 0) {
                                overStyle.add(plate);
                                refreshOver(returnCode, ISNULL);
                            } else if (page == maxPage) {
                                overStyle.add(plate);
                                refreshOver(returnCode, ISOVER);
                            } else {
                                refreshOver(returnCode, CLICK_MORE);
                            }
                            refreshItems(list.getObj());
                            break;
                        case LOADMORE:
                            if (list.getObj().size() == 0) {
                                overStyle.add(plate);
                                loadMoreOver(returnCode, ISNULL);
                            } else if (page == maxPage) {
                                overStyle.add(plate);
                                loadMoreOver(returnCode, ISOVER);
                            } else {
                                loadMoreOver(returnCode, CLICK_MORE);
                            }
                            addItems(list.getObj());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(-1, ISNULL);
                            break;
                        case LOADMORE:
                            loadMoreOver(-1, ISOVER);
                            break;
                    }
                }
                isRequest = false;
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(-1, errorMsg);
                        break;
                    case LOADMORE:
                        loadMoreOver(-1, errorMsg);
                        break;
                }
                isRequest = false;
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            switch (getItemViewType(position)) {
                case 1:
                    convertView = mInflater.inflate(R.layout.fragment_home_one, null);
                    holder = new ViewHolder(convertView);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.ivSix.getLayoutParams();
                    params.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                    holder.ivSix.setLayoutParams(params);
                    break;
                case 2:
                    convertView = mInflater.inflate(R.layout.fragment_home_two, null);
                    holder = new ViewHolder(convertView);
                    LinearLayout.LayoutParams paramTwo = (LinearLayout.LayoutParams) holder.ivOne.getLayoutParams();
                    paramTwo.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                    holder.ivOne.setLayoutParams(paramTwo);
                    break;
                case 3:
                    convertView = mInflater.inflate(R.layout.fragment_home_three, null);
                    holder = new ViewHolder(convertView);
                    break;
                case 4:
                    convertView = mInflater.inflate(R.layout.fragment_home_four, null);
                    holder = new ViewHolder(convertView);
                    LinearLayout.LayoutParams paramFour = (LinearLayout.LayoutParams) holder.ivThree.getLayoutParams();
                    paramFour.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                    holder.ivThree.setLayoutParams(paramFour);
                    break;
                case 5:
                    convertView = mInflater.inflate(R.layout.fragment_home_five, null);
                    holder = new ViewHolder(convertView);
                    FrameLayout.LayoutParams paramFive = (FrameLayout.LayoutParams) holder.ivSix.getLayoutParams();
                    paramFive.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                    holder.ivSix.setLayoutParams(paramFive);
                    break;
                case 6:
                    convertView = mInflater.inflate(R.layout.fragment_home_six, null);
                    holder = new ViewHolder(convertView);
                    LinearLayout.LayoutParams paramSix = (LinearLayout.LayoutParams) holder.ivFour.getLayoutParams();
                    paramSix.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                    holder.ivFour.setLayoutParams(paramSix);
                    break;
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.llItemAll.getLayoutParams();
            layoutParams.width = metrics.widthPixels;
            layoutParams.height = metrics.heightPixels - PhoneManager.getStatusBarHigh() - toolbarHigh;
            holder.llItemAll.setLayoutParams(layoutParams);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HomeOrgModel orgmodel = (HomeOrgModel) getItem(position);
        for (int i = 0; i < orgmodel.getGroupData().size(); i++) {
            HomeModel model = orgmodel.getGroupData().get(i);
            switch (model.getPosition()) {
                case 1:
                    holder.ivOne.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivOne.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
                case 2:
                    holder.ivTwo.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivTwo.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
                case 3:
                    holder.ivThree.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivThree.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
                case 4:
                    holder.ivFour.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivFour.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
                case 5:
                    holder.ivFive.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivFive.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
                case 6:
                    holder.ivSix.setImageURI(Uri.parse(model.getUrl()));
                    holder.ivSix.setOnClickListener(new OnPicClickListener(model.getId()));
                    break;
            }
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        HomeOrgModel model = (HomeOrgModel) getItem(position);
        return model.getGroupData().get(0).getPlate();
    }

    static class ViewHolder {
        @InjectView(R.id.llItemAll)
        LinearLayout llItemAll;
        @InjectView(R.id.ivOne)
        SimpleDraweeView ivOne;
        @InjectView(R.id.ivTwo)
        SimpleDraweeView ivTwo;
        @InjectView(R.id.ivThree)
        SimpleDraweeView ivThree;
        @InjectView(R.id.ivFour)
        SimpleDraweeView ivFour;
        @InjectView(R.id.ivFive)
        SimpleDraweeView ivFive;
        @InjectView(R.id.ivSix)
        SimpleDraweeView ivSix;

        public ViewHolder(View convertView) {
            ButterKnife.inject(this, convertView);
        }
    }

    private class OnPicClickListener implements View.OnClickListener {

        private String imageId;

        public OnPicClickListener(String imageId) {
            this.imageId = imageId;
        }

        @Override
        public void onClick(View view) {
            if (!ClickUtil.isFastClick()) {
                if (!imageId.equals("")) {
                    Intent intent = new Intent(mContext, HomeSearchResult.class);
                    intent.putExtra("IAMGEID", imageId);
                    mContext.startActivity(intent);
                } else {
                    toast("无效的搜索图片");
                }
            }
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void loadMore() {
        if (overStyle.size() == 6) {
            loadMoreOver(0, ISOVER);
        } else {
            if (!isRequest) {
                this.isRequest = true;
                this.loadType = LOADMORE;
                if (plate == 6) {
                    plate = 1;
                    page++;
                } else {
                    plate++;
                }
                getPlate();
                HomeParams params = new HomeParams(page, limit, plate);
                AsyncHttpTask.post(params.getUrl(), params, responseHandler);
            }
        }
    }

    private void getPlate() {
        for (int i = 0; i < overStyle.size(); i++) {
            if (plate == overStyle.get(i)) {
                if (plate == 6) {
                    plate = 1;
                    page++;
                } else {
                    plate++;
                }
                getPlate();
            }
        }
        return;
    }

    public boolean getIsOver() {
        return (overStyle.size() == 6);
    }

    /**
     * 首页刷新
     */
    public void getHomeList(int plate) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 1;
            this.plate = plate;
            if (overStyle != null) {
                overStyle.clear();
            }
            HomeParams params = new HomeParams(page, limit, plate);
            AsyncHttpTask.post(params.getUrl(), params, responseHandler);
        }
    }
}
