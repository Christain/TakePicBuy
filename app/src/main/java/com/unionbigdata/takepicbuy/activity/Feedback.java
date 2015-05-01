package com.unionbigdata.takepicbuy.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.dialog.DialogTipsBuilder;
import com.unionbigdata.takepicbuy.dialog.Effectstype;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 用户反馈
 * Created by Christain on 15/4/19.
 */
public class Feedback extends BaseActivity {

    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    @InjectView(R.id.etContent)
    EditText etContent;

    private DialogTipsBuilder dialog;

    @Override
    protected int layoutResId() {
        return R.layout.feedback;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getToolbar().setTitle("意见反馈");
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            toast("提交");
        }
    }
}
