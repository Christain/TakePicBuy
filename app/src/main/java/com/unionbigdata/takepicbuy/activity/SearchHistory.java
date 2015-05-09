package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.adapter.SearchPicAdapter;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.model.SearchPicModel;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 搜索历史
 * Created by Christain on 2015/4/20.
 */
public class SearchHistory extends BaseActivity {

    @InjectView(R.id.gridView)
    GridView gridView;

    private boolean isCancelStatus = false;
    private SearchPicAdapter adapter;
    private ArrayList<SearchPicModel> list;

    @Override
    protected int layoutResId() {
        return R.layout.search_history;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("LIST")) {
            this.list = (ArrayList<SearchPicModel>) intent.getSerializableExtra("LIST");
            getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
            getToolbar().setTitle("历史搜索");
            getToolbar().setTitleTextColor(0xFFFFFFFF);
            setSupportActionBar(getToolbar());
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCancelStatus) {
                        CancelDismiss();
                    } else {
                        if (adapter != null && adapter.isHasChange()) {
                            Intent intent = new Intent();
                            intent.putExtra("LIST", list);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }
                }
            });
            this.adapter = new SearchPicAdapter(SearchHistory.this, "ALL");
            this.gridView.setAdapter(adapter);
            this.adapter.setSearchList(list);
            this.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (!isCancelStatus) {
                        isCancelStatus = true;
                        for (int j = 0; j < list.size(); j++) {
                            SearchPicModel model = adapter.getItem(j);
                            model.setStatus(1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
            this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (!ClickUtil.isFastClick()) {
                        if (isCancelStatus) {
                            CancelDismiss();
                        } else {
                            if (!ClickUtil.isFastClick()) {
                                Intent intent = new Intent(SearchHistory.this, SearchResult.class);
                                intent.putExtra("IMGURL", getImageUrl(position));
                                intent.putExtra("FROM", "SEARCH");
                                startActivity(intent);
                            }
                        }
                    }
                }
            });
        } else {
            toast("获取搜索历史错误");
            finish();
        }
    }

    /**
     * 根据文件名，获得图片在服务器的url
     */
    private String getImageUrl(int position) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.paitogo.com:80/upload/");
        String filePath = adapter.getItem(position).getImg();
        sb.append(filePath.substring(filePath.lastIndexOf("/"), filePath.lastIndexOf("_")));
        sb.append(".jpg");
        return sb.toString();
    }

    private void CancelDismiss() {
        if (list != null) {
            for (int j = 0; j < list.size(); j++) {
                SearchPicModel model = adapter.getItem(j);
                model.setStatus(0);
            }
            adapter.notifyDataSetChanged();
            isCancelStatus = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCancelStatus) {
                CancelDismiss();
            } else {
                if (adapter != null && adapter.isHasChange()) {
                    Intent intent = new Intent();
                    intent.putExtra("LIST", list);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        }
        return true;
    }
}
