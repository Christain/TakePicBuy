package com.unionbigdata.takepicbuy.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.activity.SearchResult;
import com.unionbigdata.takepicbuy.baseclass.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 首页内容
 * Created by Christain on 2015/4/20.
 */
public class HomeFragment extends BaseFragment {

    private View convertView;

    @InjectView(R.id.ivOne)
    SimpleDraweeView ivOne;
    @InjectView(R.id.ivTwo)
    SimpleDraweeView ivTwo;
    @InjectView(R.id.ivThree)
    SimpleDraweeView ivThree;
    @InjectView(R.id.ivFour)
    SimpleDraweeView ivFour;
    @InjectView(R.id.ivFive)
    SimpleDraweeView ivFive;
    @InjectView(R.id.ivSix)
    SimpleDraweeView ivSix;

    private int style = 0;

    @Override
    protected void OnCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        Bundle bundle = getArguments();
        style = bundle.getInt("TYPE");
        switch (style) {
            case 1:
                this.convertView = inflater.inflate(R.layout.fragment_home_one, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivSix.getLayoutParams();
                params.width = (metrics.widthPixels - getActivity().getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                ivSix.setLayoutParams(params);
                break;
            case 2:
                this.convertView = inflater.inflate(R.layout.fragment_home_two, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                LinearLayout.LayoutParams paramTwo = (LinearLayout.LayoutParams) ivOne.getLayoutParams();
                paramTwo.width = (metrics.widthPixels - getActivity().getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                ivOne.setLayoutParams(paramTwo);
                break;
            case 3:
                this.convertView = inflater.inflate(R.layout.fragment_home_three, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                break;
            case 4:
                this.convertView = inflater.inflate(R.layout.fragment_home_four, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                LinearLayout.LayoutParams paramFour = (LinearLayout.LayoutParams) ivThree.getLayoutParams();
                paramFour.width = (metrics.widthPixels - getActivity().getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                ivThree.setLayoutParams(paramFour);
                break;
            case 5:
                this.convertView = inflater.inflate(R.layout.fragment_home_five, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                FrameLayout.LayoutParams paramFive = (FrameLayout.LayoutParams) ivSix.getLayoutParams();
                paramFive.width = (metrics.widthPixels - getActivity().getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                ivSix.setLayoutParams(paramFive);
                break;
            case 6:
                this.convertView = inflater.inflate(R.layout.fragment_home_six, (ViewGroup) getActivity().findViewById(R.id.view_pager), false);
                ButterKnife.inject(this, convertView);
                LinearLayout.LayoutParams paramSix = (LinearLayout.LayoutParams) ivFour.getLayoutParams();
                paramSix.width = (metrics.widthPixels - getActivity().getResources().getDimensionPixelOffset(R.dimen.home_image_padding) * 2);
                ivFour.setLayoutParams(paramSix);
                break;
        }


        ivOne.setImageURI(Uri.parse("http://a.hiphotos.baidu.com/image/pic/item/09fa513d269759ee154e5cc6b0fb43166d22dfa4.jpg"));
        ivTwo.setImageURI(Uri.parse("http://h.hiphotos.baidu.com/image/pic/item/267f9e2f0708283830200feebc99a9014c08f11f.jpg"));
        ivThree.setImageURI(Uri.parse("http://h.hiphotos.baidu.com/image/pic/item/267f9e2f0708283830200feebc99a9014c08f11f.jpg"));
        ivFour.setImageURI(Uri.parse("http://a.hiphotos.baidu.com/image/pic/item/09fa513d269759ee154e5cc6b0fb43166d22dfa4.jpg"));
        ivFive.setImageURI(Uri.parse("http://a.hiphotos.baidu.com/image/pic/item/09fa513d269759ee154e5cc6b0fb43166d22dfa4.jpg"));
        ivSix.setImageURI(Uri.parse("http://h.hiphotos.baidu.com/image/pic/item/267f9e2f0708283830200feebc99a9014c08f11f.jpg"));
    }

    @Override
    protected View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) convertView.getParent();
        if (p != null) {
            p.removeAllViews();
        }
        return convertView;
    }

    @OnClick({R.id.ivOne, R.id.ivTwo, R.id.ivThree, R.id.ivFour, R.id.ivFive, R.id.ivSix})
    void onImageClick() {
        Intent intent = new Intent(getActivity(), SearchResult.class);
        getActivity().startActivity(intent);
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
}
