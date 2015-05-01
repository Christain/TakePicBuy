package com.unionbigdata.takepicbuy.http;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.unionbigdata.takepicbuy.constant.Constant;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ResponseHandler extends JsonHttpResponseHandler {

	private static final String TAG = "Response";

	public abstract void onResponseSuccess(int returnCode, Header[] headers, String result);

	public abstract void onResponseFailed(int returnCode, String errorMsg);

	@Override
	public final void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		Log.e(TAG, "onSuccess(..., JSONArray)" + "We don't support this method, {\n" + ((response != null) ? response.toString() : "null") + "\n} should be a JSONObject");
		onFailure(statusCode, headers, new IllegalStateException("onSuccess(..., JSONArray) not supported!"), (JSONObject) null);
	}

	@Override
	public final void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		try {
			int returnCode = response.getInt("errno");
			if (returnCode == 0) {
                if (Constant.SHOW_LOG) {
                    Log.i(TAG, "正确：" + response.toString());
                }
                onResponseSuccess(returnCode, headers, response.getString("data"));
			} else {
                if (Constant.SHOW_LOG) {
                    Log.e(TAG, "错误：" + response.toString());
                }
                onResponseFailed(-1, response.getString("errmsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
            if (Constant.SHOW_LOG) {
                Log.e(TAG, "数据解析错误：" + response.toString());
            }
            onResponseFailed(-1, "数据解析错误");
		}
	}

	@Override
	public final void onSuccess(int statusCode, Header[] headers, String responseString) {
		onFailure(statusCode, headers, new IllegalStateException("onSuccess(..., JSONArray) not supported!"), (JSONObject) null);
	}

	@Override
	public final void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        if (Constant.SHOW_LOG && responseString != null) {
            Log.e(TAG, "错误：" + responseString);
        }
        if (statusCode == 404) {
            onResponseFailed(-1, "访问失败，请检查网络连接状态");
        } else {
            onResponseFailed(-1, "访问失败");
        }
	}

	@Override
	public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        if (Constant.SHOW_LOG && errorResponse != null) {
            Log.e(TAG, "错误：" + errorResponse.toString());
        }
        onResponseFailed(-1, "fail");
	}

	@Override
	public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        if (Constant.SHOW_LOG && errorResponse != null) {
            Log.e(TAG, "错误：" + errorResponse.toString());
        }
        onResponseFailed(-1, "fail");
	}
}
