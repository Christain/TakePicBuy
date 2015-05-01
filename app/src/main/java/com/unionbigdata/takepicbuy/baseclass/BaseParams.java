package com.unionbigdata.takepicbuy.baseclass;

import com.loopj.android.http.RequestParams;
import com.unionbigdata.takepicbuy.constant.Constant;

/**
 * http请求参数基类
 * Created by Christain on 2015/4/8.
 */
public class BaseParams extends RequestParams {

    private String url;

    public BaseParams(String url) {
        this.url = (url != null) ? url : "";
    }

    public String getUrl() {
        if (url.startsWith("http")) {
            return url;
        }
        return new StringBuffer().append(Constant.DEFOULT_REQUEST_URL).append(url).toString();
    }

}
