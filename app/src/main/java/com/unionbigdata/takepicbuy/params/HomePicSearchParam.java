package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * 首页点击图片搜索
 * Created by Christain on 2015/5/5.
 */
public class HomePicSearchParam extends BaseParams{

    public HomePicSearchParam(String imageId) {
        super("getSearchResultByID.action");
        put("imageId", imageId);
    }
}
