package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * Òâ¼û·´À¡
 * Created by Christain on 2015/5/5.
 */
public class FeedBackParam extends BaseParams {

    public FeedBackParam(String content) {
        super("feedBack.action");
        put("content", content);
    }
}
