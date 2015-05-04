package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

/**
 * 版本更新
 * Created by Christain on 15/5/4.
 */
public class UpdateVersionParam extends BaseParams{

    public UpdateVersionParam() {
        super("");
        put("versionCode", PhoneManager.getVersionInfo().versionCode);
    }
}
