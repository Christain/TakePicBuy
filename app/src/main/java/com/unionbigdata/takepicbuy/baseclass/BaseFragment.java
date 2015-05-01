package com.unionbigdata.takepicbuy.baseclass;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

	protected Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.context = getActivity();
		return OnCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		OnActivityCreated(savedInstanceState);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		OnSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * toast message
	 * 
	 * @param text
	 */
	protected void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create页面内容
	 */
	protected abstract void OnCreate(Bundle savedInstanceState);

	/**
	 * createView
	 */
	protected abstract View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	/**
	 * 保存页面数据
	 */
	protected abstract void OnSaveInstanceState(Bundle outState);

	/**
	 * activityCreated
	 */
	protected abstract void OnActivityCreated(Bundle savedInstanceState);

}
