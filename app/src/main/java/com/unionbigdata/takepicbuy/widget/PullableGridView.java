package com.unionbigdata.takepicbuy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Christain on 2015/4/1.
 */
public class PullableGridView extends GridView implements Pullable {

    private boolean isOver;

    public PullableGridView(Context context) {
        super(context);
    }

    public PullableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return false;
        } else if (getFirstVisiblePosition() == 0
                && getChildAt(0).getTop() >= 0) {
            // 滑到顶部了
            return false;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return false;
        } else if (getLastVisiblePosition() == (getCount() - 1) && !isOver) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

    public void setContentOver(boolean isover) {
        this.isOver = isover;
    }

}