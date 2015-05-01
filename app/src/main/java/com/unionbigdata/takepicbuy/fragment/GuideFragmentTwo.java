package com.unionbigdata.takepicbuy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.IndexHome;
import com.unionbigdata.takepicbuy.baseclass.BaseFragment;
import com.unionbigdata.takepicbuy.utils.ClickUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Christain on 15/4/21.
 */
public class GuideFragmentTwo extends BaseFragment {
    private View convertView;

    @InjectView(R.id.rlHome)
    RelativeLayout rlHome;

    @Override
    protected void OnCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        this.convertView = inflater.inflate(R.layout.fragment_guide_two, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
        ButterKnife.inject(this, convertView);
    }

    @Override
    protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) convertView.getParent();
        if (p != null) {
            p.removeAllViews();
        }
        return convertView;
    }

    @Override
    protected void OnSaveInstanceState(Bundle outState) {

    }

    @Override
    protected void OnActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.rlHome)
    void OnClick() {
        if (!ClickUtil.isFastClick()) {
            Intent intent = new Intent(getActivity(), IndexHome.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
