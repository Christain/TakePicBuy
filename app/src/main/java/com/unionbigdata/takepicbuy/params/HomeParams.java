package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * 首页图片
 * Created by Christain on 2015/4/20.
 */
public class HomeParams extends BaseParams {

    public HomeParams(int page, int size) {
        super("getHomePageData.action");
        put("page", page);
        put("size", size);
    }
}
