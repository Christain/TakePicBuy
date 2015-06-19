package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.adapter.GoodsCompareAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.model.SearchResultModel;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 商品对比
 * Created by Christain on 15/4/21.
 */
public class GoodsCompare extends BaseActivity {

    @InjectView(R.id.listView)
    ListView listView;

    private GoodsCompareAdapter adapter;
    private ArrayList<SearchResultModel> list;

    @Override
    protected int layoutResId() {
        return R.layout.goods_compare;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("LIST")) {
            this.list = (ArrayList<SearchResultModel>) intent.getSerializableExtra("LIST");
            getToolbar().setTitle("商品对比");
            getToolbar().setTitleTextColor(0xFFFFFFFF);
            getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
            getToolbar().setTitleTextAppearance(GoodsCompare.this, R.style.AppTheme_ActionBar_TitleText);
            setSupportActionBar(getToolbar());
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            this.adapter = new GoodsCompareAdapter(GoodsCompare.this, list);
            this.listView.setAdapter(adapter);
        } else {
            toast("商品对比页面错误");
            finish();
        }
    }
}
