package com.unionbigdata.takepicbuy.constant;

import com.unionbigdata.takepicbuy.BuildConfig;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import java.io.File;

/**
 * APP常量
 * Created by Christain on 15/4/18.
 */
public final class Constant {

    public static final String DEFOULT_REQUEST_URL = "http://www.paitogo.com/";// 外网数据访问地址
    public static final boolean SHOW_LOG = BuildConfig.DEBUG;// 显示log信息

    public static final String TENCENT_APPID = "101135845"; //QQ登录ID
    public static final String SINA_APPID = "2279287286";	//新浪登录ID
    public static final String SINA_CALLBACK_URL = "http://www.paitogo.com"; //新浪回调地址
    public static final String SINA_SCOPE = "all"; //新浪授权的种类

    public static final String IMAGE_CACHE_DIR_PATH = PhoneManager.getSdCardRootPath() + "/TakePicBuy/cache/";// 图片SD卡缓存路径
    public static final String UPLOAD_FILES_DIR_PATH = PhoneManager.getSdCardRootPath() + "/TakePicBuy/upload/";// 上传图片SD卡缓存路径

    public static void checkSDPath() {
        File dir = new File(IMAGE_CACHE_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(UPLOAD_FILES_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
