package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * Ê×Ò³Í¼Æ¬µã»÷ËÑË÷
 * Created by Christain on 2015/5/5.
 */
public class HomePicSearchParam extends BaseParams{

    public HomePicSearchParam(String imageId, String filterString, int page, int size) {
        super("getSearchResultByID.action");
        put("imageId", imageId);
        put("filterString", filterString);
        put("page", page);
        put("size", size);
    }
}
