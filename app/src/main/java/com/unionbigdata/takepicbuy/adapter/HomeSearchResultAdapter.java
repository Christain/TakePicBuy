package com.unionbigdata.takepicbuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.GoodsCompare;
import com.unionbigdata.takepicbuy.activity.WebViewActivity;
import com.unionbigdata.takepicbuy.model.SearchResultModel;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 首页图片搜索Adapter
 * Created by Christain on 15/5/9.
 */
public class HomeSearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ArrayList<SearchResultModel>> list;

    public HomeSearchResultAdapter(Context context) {
        this.mContext = context;
        this.list = new ArrayList<ArrayList<SearchResultModel>> ();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ArrayList<SearchResultModel> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_search_result, null);
            holder = new ViewHolder(convertView);

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
            params.width = (metrics.widthPixels - mContext.getResources().getDimensionPixelOffset(R.dimen.search_result_padding) * 3) / 2;
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
                    Intent intent = new Intent(mContext, GoodsCompare.class);
                    intent.putExtra("LIST", list);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("URL", list.get(0).getUrl());
                    intent.putExtra("TITLE", "商品详情");
                    mContext.startActivity(intent);
                }
            }
        }
    }

    public void setHomeSearchList(ArrayList<ArrayList<SearchResultModel>> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}
