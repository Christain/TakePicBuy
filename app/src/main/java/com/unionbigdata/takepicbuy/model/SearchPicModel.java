package com.unionbigdata.takepicbuy.model;

import java.io.Serializable;

/**
 * Created by Christain on 2015/4/20.
 */
public class SearchPicModel implements Serializable {

    private String img;
    private int status;//0不显示删除，1显示删除
    private long lastModified;//文件最后修改时间

    public String getImg() {
        return (img != null ? img : "");
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
