package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * 搜索结果
 * Created by Christain on 2015/4/20.
 */
public class SearchResultParam extends BaseParams {

    public SearchResultParam(String imgUrl, String filterString, int page, int size) {
        super("searchByImageUrl2.action");
        put("imageUrl", imgUrl);
        put("filterString", filterString);
        put("page", page);
        put("size", size);
    }
}
