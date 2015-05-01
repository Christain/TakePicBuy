package com.unionbigdata.takepicbuy;

import android.content.Context;
import android.content.SharedPreferences;

import com.unionbigdata.takepicbuy.model.UserInfoModel;

/**
 * Created by Christain on 15/4/19.
 */
public class AppPreference {

    private final static String NAME = "APP_PREFERENCE";
    private static SharedPreferences preferences;

    /******************* 用户信息 **************************/
    public final static String USER_ID = "USER_ID"; // 用户ID
    public static final String USER_PHOTO = "USER_PHOTO"; // 头像
    public static final String USER_SEX = "USER_SEX"; // 性别
    public static final String NICK_NAME = "NICK_NAME"; // 昵称
    public static final String USER_PWD = "USER_PWD"; // 密码
    public static final String LOGIN_TYPE = "LOGIN_TYPE"; // 登录方式
    public static final String SINA_ID = "SINA_ID"; // 新浪openid
    public static final String SINA_TOKEN = "SINA_TOKEN"; // 新浪access_token
    public static final String SINA_EXPIRES = "SINA_EXPIRES"; // 新浪expires_in过期时间
    public static final String QQ_ID = "QQ_ID"; // 腾讯openid
    public static final String QQ_TOKEN = "QQ_TOKEN"; // 腾讯access_token
    public static final String QQ_EXPIRES = "QQ_EXPIRES"; // 腾讯expires_in过期时间

    /******************* 登录平台TYPE **************************/
    public static final int TYPE_SINA = 101; // 新浪登录
    public static final int TYPE_QQ = 102; // QQ登录

    private static void getInstance(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
    }

    public static String getUserPersistent(Context context, String key) {
        getInstance(context);
        return preferences.getString(key, "");
    }

    public static int getUserPersistentInt(Context context, String key) {
        getInstance(context);
        return preferences.getInt(key, 0);
    }

    public static long getUserPersistentLong(Context context, String key) {
        getInstance(context);
        return preferences.getLong(key, 0);
    }

    public static void save(Context context, String key, String value) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static void save(Context context, String key, int value) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static void save(Context context, String key, long value) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public static void clearInfo(Context context) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    /**
     * 更新第三方登录TYPE, openid, access_token
     */

    public static void saveThirdLoginInfo(Context context, int login_type, String open_id, String access_token, long expires_in) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        if (login_type == TYPE_QQ) {
            mEditor.putInt(LOGIN_TYPE, TYPE_QQ);
            mEditor.putString(QQ_ID, open_id);
            mEditor.putString(QQ_TOKEN, access_token);
            mEditor.putLong(QQ_EXPIRES, expires_in);
        } else if (login_type == TYPE_SINA) {
            mEditor.putInt(LOGIN_TYPE, TYPE_SINA);
            mEditor.putString(SINA_ID, open_id);
            mEditor.putString(SINA_TOKEN, access_token);
            mEditor.putLong(SINA_EXPIRES, expires_in);
        }
        mEditor.commit();
    }

    /**
     * 保存个人信息
     *
     * @param context
     * @param model
     */
    public static void saveUserInfo(Context context, UserInfoModel model) {
        getInstance(context);
        SharedPreferences.Editor mEditor = preferences.edit();
        mEditor.putString(USER_ID, model.getUserId());
        mEditor.putInt(USER_SEX, model.getSex());
        mEditor.putString(NICK_NAME, model.getName());
        mEditor.putString(USER_PHOTO, model.getUser_photo());
        mEditor.commit();
    }

    /**
     * 获取个人信息
     */
    public static UserInfoModel getUserInfo(Context context) {
        UserInfoModel model = new UserInfoModel();
        getInstance(context);
        model.setUserId(preferences.getString(USER_ID, ""));
        model.setSex(preferences.getInt(USER_SEX, 0));
        model.setName(preferences.getString(NICK_NAME, ""));
        model.setUser_photo(preferences.getString(USER_PHOTO, ""));
//        model.setSina_expires_in(preferences.getLong(SINA_EXPIRES, 0));
//        model.setSina_token(preferences.getString(SINA_TOKEN, ""));
//        model.setSina_openid(preferences.getString(SINA_ID, ""));
//        model.setQq_expires_in(preferences.getLong(QQ_EXPIRES, 0));
//        model.setQq_token(preferences.getString(QQ_TOKEN, ""));
//        model.setQq_openid(preferences.getString(QQ_ID, ""));
        return model;
    }

    /**
     * 判断是否登录
     */
    public static boolean hasLogin(Context context) {
        getInstance(context);
        if (preferences.getInt(LOGIN_TYPE, 0) == 0) {
            return false;
        } else {
            return true;
        }
    }
}
