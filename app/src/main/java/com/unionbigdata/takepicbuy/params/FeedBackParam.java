package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * �������
 * Created by Christain on 2015/5/5.
 */
public class FeedBackParam extends BaseParams {

    public FeedBackParam(String content) {
        super("UserFeedbackAction.action");
        put("content", content);
    }
}
