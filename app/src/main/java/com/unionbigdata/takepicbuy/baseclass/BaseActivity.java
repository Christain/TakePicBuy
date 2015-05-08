package com.unionbigdata.takepicbuy.baseclass;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.utils.AppManage;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


public abstract class BaseActivity extends ActionBarActivity {

	private Context context;

    @Optional
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResId());
        ButterKnife.inject(this);
        this.context = this;
        AppManage appManage = AppManage.getInstance();
        appManage.addActivity(this);
        onCreateActivity(savedInstanceState);
	}


    public Toolbar getToolbar() {
        return toolbar;
    }

	@Override
	public void finish() {
		AppManage appManage = AppManage.getInstance();
		appManage.finishActivity(this);
	}

	public void finishActivity() {
		super.finish();
	}

	/**
	 * 退出程序
	 */
	public void exitApp() {
		AppManage appManage = AppManage.getInstance();
		appManage.exit(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
        AsyncHttpTask.getClient().cancelRequests(context, true);
	}

	/**
	 * toast message
	 * @param text
	 */
	protected void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	protected abstract int layoutResId();


	protected abstract void onCreateActivity(Bundle savedInstanceState);

}
