package com.unionbigdata.takepicbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import butterknife.InjectView;

/**
 * 商品详情网页
 * Created by Christain on 15/4/21.
 */
public class WebViewActivity extends BaseActivity {

    @InjectView(R.id.pbWebView)
    ProgressBar pbWebView;
    @InjectView(R.id.webView)
    WebView webView;

    private String url, title;

    @Override
    protected int layoutResId() {
        return R.layout.goods_detail_web;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("URL") && intent.hasExtra("TITLE")) {
            this.title = intent.getStringExtra("TITLE");
            this.url = intent.getStringExtra("URL");
            getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
            getToolbar().setTitleTextAppearance(WebViewActivity.this, R.style.AppTheme_ActionBar_TitleText);
            getToolbar().setTitle(title);
            getToolbar().setTitleTextColor(0xFFFFFFFF);
            setSupportActionBar(getToolbar());
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            pbWebView.setVisibility(View.VISIBLE);
            pbWebView.setMax(100);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        pbWebView.setProgress(100);
                        pbWebView.setVisibility(View.GONE);
                    } else {
                        pbWebView.setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    pbWebView.setProgress(2);
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.loadUrl(url);
        } else {
            toast("网页信息错误");
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ClickUtil.isFastClick()) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        }
        return true;
    }
}
