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

    private int page = 1, isOver = 0, maxPage, plate;
    public int limit = 18;
    private ResponseHandler responseHandler;
    private Context mContext;
    private int toolbarHigh;

    public HomeAdapter(Context context, int toolbarHigh) {
        super(context);
        this.mContext = context;
        this.toolbarHigh = toolbarHigh;
        this.initListener();
    }

    private void initListener() {
        responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int code, Header[] headers, String result) {
                result = "{\n" +
                        "    \"homeresult\": [\n" +
                        "      [\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"1\",\n" +
                        "          \"id\": \"jRwMG7bc1YEi5Z3zHbT\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/7ce39244aeb243789cdf2ad3e4709e91.jpg\",\n" +
                        "          \"url\": \"file/pictures/0dade70a804b4ad699985538623732fa.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"2\",\n" +
                        "          \"id\": \"ymyCsnoGek9dcNEjCGb\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/c956a15b2dc7456ba26aa5b098c15116.jpg\",\n" +
                        "          \"url\": \"file/pictures/9bdca5c8dd744588a928faef1e399bdd.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"3\",\n" +
                        "          \"id\": \"bjvWpdphcAniJTbcxW3\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/pictures/94bd4420692d43c1ab320fb8f23ca24b.jpg\",\n" +
                        "          \"url\": \"file/pictures/94bd4420692d43c1ab320fb8f23ca24b.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"4\",\n" +
                        "          \"id\": \"OJ6v2F9MN4M0FQ5BMlG\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/4b105f8e014846109e4521d6eff787f9.jpg\",\n" +
                        "          \"url\": \"file/pictures/953e4bf357594187b3d18b0b69355463.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"5\",\n" +
                        "          \"id\": \"J25HKelRJeR13Sj0NKJ\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/962a5d6b95a646b5a6ba7145a4d3854f.jpg\",\n" +
                        "          \"url\": \"file/pictures/33b5559c54e44b0980ac8744f329ceb3.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"6\",\n" +
                        "          \"id\": \"b8VK349u5ljLMxo8VWo\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/0bb441f5fe634dc3975b7d3a03da967f.jpg\",\n" +
                        "          \"url\": \"file/pictures/cb526cd80395447a8d3bd38aa91c4143.jpg\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      [\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"1\",\n" +
                        "          \"id\": \"jRwMG7bc1YEi5Z3zHbT\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/7ce39244aeb243789cdf2ad3e4709e91.jpg\",\n" +
                        "          \"url\": \"file/pictures/0dade70a804b4ad699985538623732fa.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"2\",\n" +
                        "          \"id\": \"ymyCsnoGek9dcNEjCGb\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/c956a15b2dc7456ba26aa5b098c15116.jpg\",\n" +
                        "          \"url\": \"file/pictures/9bdca5c8dd744588a928faef1e399bdd.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"3\",\n" +
                        "          \"id\": \"bjvWpdphcAniJTbcxW3\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/pictures/94bd4420692d43c1ab320fb8f23ca24b.jpg\",\n" +
                        "          \"url\": \"file/pictures/94bd4420692d43c1ab320fb8f23ca24b.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"4\",\n" +
                        "          \"id\": \"OJ6v2F9MN4M0FQ5BMlG\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/4b105f8e014846109e4521d6eff787f9.jpg\",\n" +
                        "          \"url\": \"file/pictures/953e4bf357594187b3d18b0b69355463.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"5\",\n" +
                        "          \"id\": \"J25HKelRJeR13Sj0NKJ\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/962a5d6b95a646b5a6ba7145a4d3854f.jpg\",\n" +
                        "          \"url\": \"file/pictures/33b5559c54e44b0980ac8744f329ceb3.jpg\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"plate\": \"1\",\n" +
                        "          \"position\": \"6\",\n" +
                        "          \"id\": \"b8VK349u5ljLMxo8VWo\",\n" +
                        "          \"groupId\": \"1\",\n" +
                        "          \"searchUrl\": \"file/searchPictures/0bb441f5fe634dc3975b7d3a03da967f.jpg\",\n" +
                        "          \"url\": \"file/pictures/cb526cd80395447a8d3bd38aa91c4143.jpg\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    ]\n" +
                        "  }";
                Gson gson = new Gson();
                HomeListModel list = gson.fromJson(result, HomeListModel.class);
                if (list != null && list.getHomeresult() != null) {
                    switch (loadType) {
                        case REFRESH:
                            if (list.getHomeresult().size() == 0) {
                                refreshOver(code, ISNULL);
                            } else if (page == maxPage) {
                                isOver = 1;
                                refreshOver(code, ISOVER);
                            } else {
                                page++;
                                refreshOver(code, CLICK_MORE);
                            }
                            refreshItems(list.getHomeresult());
                            break;
                        case LOADMORE:
                            if (page != maxPage) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getHomeresult());
                            break;
                    }
                } else {
                    switch (loadType) {
                        case REFRESH:
                            refreshOver(code, ISNULL);
                            break;
                        case LOADMORE:
                            loadMoreOver(code, ISOVER);
                            break;
                    }
                }
                isRequest = false;
            }

            @Override
            public void onResponseFailed(int code, String msg) {
                switch (loadType) {
                    case REFRESH:
                        refreshOver(code, msg);
                        break;
                    case LOADMORE:
                        loadMoreOver(code, msg);
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

        ArrayList<HomeModel> model = (ArrayList<HomeModel>)getItem(position);
        for (int i = 0; i < model.size(); i++) {
            switch (model.get(i).getPosition()) {
                case 1:
                    holder.ivOne.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivOne.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
                    break;
                case 2:
                    holder.ivTwo.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivTwo.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
                    break;
                case 3:
                    holder.ivThree.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivThree.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
                    break;
                case 4:
                    holder.ivFour.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivFour.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
                    break;
                case 5:
                    holder.ivFive.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivFive.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
                    break;
                case 6:
                    holder.ivSix.setImageURI(Uri.parse(model.get(i).getUrl()));
                    holder.ivSix.setOnClickListener(new OnPicClickListener(model.get(i).getId()));
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
        ArrayList<HomeModel> list = (ArrayList<HomeModel>) getItem(position);
        return list.get(0).getPlate();
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
        if (isOver == 1) {
            loadMoreOver(0, ISOVER);
        } else {
            if (!isRequest) {
                this.isRequest = true;
                this.loadType = LOADMORE;
                if (plate == 6) {
                    plate = 1;
                } else {
                    plate++;
                }
                HomeParams params = new HomeParams(page, limit, plate);
                AsyncHttpTask.post(params.getUrl(), params, responseHandler);
            }
        }
    }

    public boolean getIsOver() {
        return (isOver == 1) ? true : false;
    }

    /**
     * 首页刷新
     */
    public void getHomeList(int plate) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 1;
            isOver = 0;
            this.plate = plate;
            HomeParams params = new HomeParams(page, limit, plate);
            AsyncHttpTask.post(params.getUrl(), params, responseHandler);
        }
    }
}
