package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

/**
 * 登录
 * Created by Christain on 2015/4/22.
 */
public class LoginParam extends BaseParams {

    /**
     * @param type 1新浪登录，2QQ登录
     * @param openId
     * @param access_token
     */
    public LoginParam(int type, String openId, String access_token) {
        super("");
        put("type", type);
        put("openId", openId);
        put("access_token", access_token);
    }
}
