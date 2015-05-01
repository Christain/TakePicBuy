package com.unionbigdata.takepicbuy.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.model.SearchPicModel;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索历史
 * Created by Christain on 2015/4/20.
 */
public class SearchPicAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchPicModel> list;
    private String type;
    private boolean hasChange = false;

    public SearchPicAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
        this.list = new ArrayList<SearchPicModel>();
    }

    @Override
    public int getCount() {
        if (type.equals("9")) {
            return (list.size() > 9) ? 9 : list.size();
        } else {
            return list.size();
        }
    }

    @Override
    public SearchPicModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_history, null);
            holder = new ViewHolder(convertView);

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.flItemAll.getLayoutParams();
            params.width = (int) (metrics.widthPixels -
                    context.getResources().getDimensionPixelOffset(R.dimen.user_center_gridview_padding) *2) / 3;
            params.height = params.width;
            holder.flItemAll.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchPicModel model = getItem(position);
        holder.imageView.setImageURI(Uri.parse("file://"+model.getImg()));
        if (model.getStatus() == 0) {
            holder.ivCancel.setVisibility(View.GONE);
        } else {
            holder.ivCancel.setVisibility(View.VISIBLE);
            holder.ivCancel.setOnClickListener(new OnCancelClickListener(model, position));
        }
        return convertView;
    }

    static class  ViewHolder {
        @InjectView(R.id.imageView)
        SimpleDraweeView imageView;
        @InjectView(R.id.ivCancel)
        ImageView ivCancel;
        @InjectView(R.id.flItemAll)
        FrameLayout flItemAll;

        public ViewHolder(View convertView) {
            ButterKnife.inject(this, convertView);
        }
    }

    private class OnCancelClickListener implements View.OnClickListener {

        private SearchPicModel model;
        private int position;

        public OnCancelClickListener(SearchPicModel model, int position) {
            this.model = model;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (!ClickUtil.isFastClick()) {
                File file = new File(model.getImg());
                if (file != null && file.exists()) {
                    file.delete();
                }
                list.remove(position);
                notifyDataSetChanged();
                hasChange = true;
            }
        }
    }

    public void setSearchList(ArrayList<SearchPicModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public boolean isHasChange() {
        return hasChange;
    }
}
