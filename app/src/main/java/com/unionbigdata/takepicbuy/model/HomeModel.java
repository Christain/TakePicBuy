package com.unionbigdata.takepicbuy.model;

/**
 * 首页数据解析
 * Created by Christain on 2015/5/8.
 */
public class HomeModel {

    private int plate;
    private int position;
    private String id;
    private String searchUrl;
    private String url;

    public int getPlate() {
        return plate;
    }

    public void setPlate(int plate) {
        this.plate = plate;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return (id != null ? id : "");
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchUrl() {
        return (searchUrl != null ? searchUrl : "");
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getUrl() {
        return (url != null ? url : "");
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
