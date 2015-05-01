package com.unionbigdata.takepicbuy.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import butterknife.OnClick;

/**
 * 官方微信二维码
 * Created by Christain on 15/4/21.
 */
public class QRCodeOfficial extends BaseActivity {

    private MediaScannerConnection msc = null;

    @Override
    protected int layoutResId() {
        return R.layout.qrcode_official;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        getToolbar().setTitle("拍图购微信");
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.ivQRCode)
    void OnImageClick() {
        if (!ClickUtil.isFastClick()) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_qrcode);
            if (bitmap != null) {
                final String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "拍图购微信二维码", "拍图购微信二维码");
                if (uri != null) {
                    toast("保存成功");
                    msc = new MediaScannerConnection(QRCodeOfficial.this, new MediaScannerConnection.MediaScannerConnectionClient() {
                        @Override
                        public void onMediaScannerConnected() {
                            msc.scanFile(uri, "image/jpeg");
                        }

                        @Override
                        public void onScanCompleted(String s, Uri uri) {
                            msc.disconnect();
                        }
                    });
                } else {
                    toast("保存失败");
                }
            } else {
                toast("保存失败");
            }
        }
    }
}
