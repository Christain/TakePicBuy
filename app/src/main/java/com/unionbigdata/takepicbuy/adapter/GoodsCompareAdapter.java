package com.unionbigdata.takepicbuy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.WebViewActivity;
import com.unionbigdata.takepicbuy.model.SearchResultModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 商品对比
 * Created by Christain on 15/4/21.
 */
public class GoodsCompareAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SearchResultModel> list;

    public GoodsCompareAdapter(Context context, ArrayList<SearchResultModel> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SearchResultModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_goods_compare, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchResultModel model = getItem(position);
        if (position == 0) {
            holder.tvTipNum.setVisibility(View.VISIBLE);
            holder.tvTipNum.setText("为您找到" + list.size() + "件商品");
        } else {
            holder.tvTipNum.setVisibility(View.GONE);
        }
        holder.tvPrice.setText("￥" + model.getPrice());
        holder.ivPic.setImageURI(Uri.parse(model.getImg_url()));
        holder.tvTitle.setText(model.getTitle());
        switch (model.getOrig_id()) {
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
        if (model.getSalesnum() > 999) {
            holder.tvSaleNum.setText("销量：" + "999+");
        } else {
            holder.tvSaleNum.setText("销量：" + model.getSalesnum());
        }
        holder.llItemAll.setOnClickListener(new OnItemAllClickListener(model));
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.llItemAll)
        LinearLayout llItemAll;
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
        @InjectView(R.id.tvTipNum)
        TextView tvTipNum;

        public ViewHolder(View convertView) {
            ButterKnife.inject(this, convertView);
        }
    }

    private class OnItemAllClickListener implements View.OnClickListener {

        private SearchResultModel model;

        public OnItemAllClickListener(SearchResultModel model) {
            this.model = model;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("URL", model.getUrl());
            intent.putExtra("TITLE", "商品详情");
            mContext.startActivity(intent);
        }
    }

}
