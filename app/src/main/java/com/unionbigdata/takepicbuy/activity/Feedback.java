package com.unionbigdata.takepicbuy.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.dialog.DialogTipsBuilder;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.dialog.LoadingDialog;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.params.FeedBackParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import org.apache.http.Header;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 用户反馈
 * Created by Christain on 15/4/19.
 */
public class Feedback extends BaseActivity {

    @InjectView(R.id.etContent)
    EditText etContent;
    @InjectView(R.id.llBack)
    LinearLayout llBack;
    @InjectView(R.id.tvBack)
    TextView tvBack;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;

    private DialogTipsBuilder dialog;
    private LoadingDialog mLoadingDialog;

    @Override
    protected int layoutResId() {
        return R.layout.feedback;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        this.mLoadingDialog = LoadingDialog.createDialog(Feedback.this, true);
        getToolbar().setTitle("");
//        getToolbar().setTitleTextColor(0xFFFFFFFF);
//        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText("意见反馈");
        tvBack.setText("设置");
        setSupportActionBar(getToolbar());
//        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        this.dialog = DialogTipsBuilder.getInstance(Feedback.this);
    }

    @OnClick(R.id.tvSubmit)
    void OnSumbit() {
        if (!ClickUtil.isFastClick()) {
            String content = etContent.getText().toString();
            if (TextUtils.isEmpty(content)) {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.setMessage("请输入您的意见及建议").withEffect(Effectstype.Shake).show();
                }
                return;
            }
            Submit(content);
        }
    }

    /**
     * 提交意见反馈
     */
    private void Submit(String content) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setMessage("提交中...");
            mLoadingDialog.show();
        }
        FeedBackParam param = new FeedBackParam(content);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("提交成功");
                finish();
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("提交失败，请重试");
            }
        });
    }
}
