package com.unionbigdata.takepicbuy.params;

import com.unionbigdata.takepicbuy.baseclass.BaseParams;

import java.io.File;

/**
 * 上传图片搜索
 * Created by Christain on 2015/4/27.
 */
public class UploadFileParam extends BaseParams {

    public UploadFileParam(File file) {
        super("searchByUploadImage2.action");
        try {
            put("fileupload", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UploadFileParam(String imageUrl, int page, int size, int orig_id) {
        super("searchByUploadImage2.action");
        put("srcimageurl", imageUrl);
        put("page", page);
        put("page_listsize", size);
        if (orig_id != -1) {
            put("orig_id", orig_id);
        }
    }
}
