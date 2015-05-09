package com.unionbigdata.takepicbuy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.GoodsCompare;
import com.unionbigdata.takepicbuy.activity.WebViewActivity;
import com.unionbigdata.takepicbuy.baseclass.SuperAdapter;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.model.SearchResultListModel;
import com.unionbigdata.takepicbuy.model.SearchResultModel;
import com.unionbigdata.takepicbuy.params.HomePicSearchParam;
import com.unionbigdata.takepicbuy.params.SearchResultParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import org.apache.http.Header;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索结果
 * Created by Christain on 2015/4/20.
 */
public class SearchResultAdapter extends SuperAdapter {

    private int page = 0, isOver = 0, maxPage, fromType = 0;
    public int limit = 20;
    private String imgUrl = "", filterString = "all";
    private ResponseHandler responseHandler;
    private Context mContext;
    private boolean isRequest = false;

    public SearchResultAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.initListener();
    }

    private void initListener() {
        responseHandler = new ResponseHandler() {
            @Override
            public void onResponseSuccess(int code, Header[] headers, String result) {
                Gson gson = new Gson();
                SearchResultListModel list = gson.fromJson(result, SearchResultListModel.class);
                if (page == 0) {
                    limit = list.getPage_listsize();
                    imgUrl = list.getSrcimageurl();
                    maxPage = list.getPage_sum();
                }
                if (list != null && list.getSearchresult() != null) {
                    switch (loadType) {
                        case REFRESH:
                            refreshItems(list.getSearchresult());
                            if (list.getSearchresult().size() == 0) {
                                refreshOver(code, ISNULL);
                            } else if (page == maxPage - 1) {
                                isOver = 1;
                                refreshOver(code, ISOVER);
                            } else {
                                page++;
                                refreshOver(code, CLICK_MORE);
                            }
                            break;
                        case LOADMORE:
                            if (page != maxPage - 1) {
                                page++;
                                loadMoreOver(code, CLICK_MORE);
                            } else {
                                isOver = 1;
                                loadMoreOver(code, ISOVER);
                            }
                            addItems(list.getSearchresult());
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_result, null);
            holder = new ViewHolder(convertView);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
            params.width = (metrics.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.search_result_padding) * 3) / 2;
            params.height = params.width;
            holder.ivPic.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArrayList<SearchResultModel> list = (ArrayList<SearchResultModel>) getItem(position);

        if (position == 0 || position == 1) {
            holder.view.setVisibility(View.VISIBLE);
        } else {
            holder.view.setVisibility(View.GONE);
        }
        holder.ivPic.setImageURI(Uri.parse(list.get(0).getImg_url()));
        holder.tvTitle.setText(list.get(0).getTitle());
        holder.tvPrice.setText("￥" + list.get(0).getPrice());
        switch (list.get(0).getOrig_id()) {
            case 1:
                holder.ivType.setImageResource(R.mipmap.icon_type_taobao);
                break;
            case 2:
                holder.ivType.setImageResource(R.mipmap.icon_type_tmall);
                break;
            case 3:
                holder.ivType.setImageResource(R.mipmap.icon_type_jd);
                break;
            default:
                holder.ivType.setImageResource(R.mipmap.icon_type_taobao);
                break;
        }
        if (list.get(0).getSalesnum() > 999) {
            holder.tvSaleNum.setText("销量：" + "999+");
            if (list.get(0).getSalesnum() > 2000) {
                holder.tvSaleNum.setText("销量：" + "2000+");
            } else if (list.get(0).getSalesnum() > 5000) {
                holder.tvSaleNum.setText("销量：" + "5000+");
            } else if (list.get(0).getSalesnum() > 10000) {
                holder.tvSaleNum.setText("销量：" + "10000+");
            }
        } else {
            holder.tvSaleNum.setText("销量：" + list.get(0).getSalesnum());
        }
        if (list.size() > 1) {
            holder.tvSameNum.setVisibility(View.VISIBLE);
            holder.tvSameNum.setText((list.size()) + "件同款");
        } else {
            holder.tvSameNum.setVisibility(View.GONE);
        }

        holder.llItemAll.setOnClickListener(new OnItemAllClickListener(list));
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.llItemAll)
        LinearLayout llItemAll;
        @InjectView(R.id.view)
        View view;
        @InjectView(R.id.ivPic)
        SimpleDraweeView ivPic;
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvPrice)
        TextView tvPrice;
        @InjectView(R.id.ivType)
        ImageView ivType;
        @InjectView(R.id.tvSaleNum)
        TextView tvSaleNum;
        @InjectView(R.id.tvSameNum)
        TextView tvSameNum;

        public ViewHolder(View convertView) {
            ButterKnife.inject(this, convertView);
        }
    }

    private class OnItemAllClickListener implements View.OnClickListener {

        private ArrayList<SearchResultModel> list;

        public OnItemAllClickListener(ArrayList<SearchResultModel> list) {
            this.list = list;
        }

        @Override
        public void onClick(View view) {
            if (!ClickUtil.isFastClick()) {
                if (list.size() > 1) {
                    Intent intent = new Intent(context, GoodsCompare.class);
                    intent.putExtra("LIST", list);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("URL", list.get(0).getUrl());
                    intent.putExtra("TITLE", "商品详情");
                    context.startActivity(intent);
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
                if (fromType == 1) {
                    HomePicSearchParam param = new HomePicSearchParam(imgUrl, filterString, page, limit);
                    AsyncHttpTask.post(param.getUrl(), param, responseHandler);
                } else {
                    SearchResultParam param = new SearchResultParam(imgUrl, filterString, page, limit);
                    AsyncHttpTask.post(param.getUrl(), param, responseHandler);
                }
            }
        }
    }

    public boolean getIsOver() {
        return (isOver == 1) ? true : false;
    }

    /**
     * 搜索第一次加载分类
     */
    public void searchResultList(String imgUrl, String filterString, int fromType) {
        if (!isRequest) {
            this.isRequest = true;
            this.loadType = REFRESH;
            this.page = 0;
            isOver = 0;
            this.imgUrl = imgUrl;
            this.filterString = filterString;
            this.fromType = fromType;
            if (fromType == 1) {
                HomePicSearchParam param = new HomePicSearchParam(imgUrl, filterString, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
            } else {
                SearchResultParam param = new SearchResultParam(imgUrl, filterString, page, limit);
                AsyncHttpTask.post(param.getUrl(), param, responseHandler);
            }
        }
    }
}