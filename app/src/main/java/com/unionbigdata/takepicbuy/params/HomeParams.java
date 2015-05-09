package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * 首页图片
 * Created by Christain on 2015/4/20.
 */
public class HomeParams extends BaseParams {

    public HomeParams(int pageno, int pagesize, int plate) {
        super("getHomePageData.action");
        put("pageno", pageno);
        put("pagesize", pagesize);
        put("plate", plate);
    }
}
