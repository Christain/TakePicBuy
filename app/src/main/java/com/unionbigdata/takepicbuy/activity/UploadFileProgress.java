package com.unionbigdata.takepicbuy.activity;

import android.os.Bundle;
import android.view.View;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.widget.CircleProgress;

import butterknife.InjectView;

/**
 * 上传图片搜索等待
 * Created by Christain on 15/4/22.
 */
public class UploadFileProgress extends BaseActivity {

    @InjectView(R.id.progress)
    CircleProgress progress;

    @Override
    protected int layoutResId() {
        return R.layout.upload_fle_progress;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getToolbar().setTitle("搜索中...");
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progress.setRadius(1f);
        progress.startAnim();
    }
}
