package com.unionbigdata.takepicbuy.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Christain on 2015/4/27.
 */
public class SearchResultModel implements Serializable {

    private String img_url;//图片地址
    private String id;//商品Id
    private String title;//AMII极简2015新款飘逸显瘦长裙雪纺半身裙长裙11480107 绿色 L
    private double distance;//9836.7099609375
    private double price;//	239
    private int cate_id;//01010302
    private int salesnum;
    private String url;//商品网址
    private String key_id;//jd_1171357130
    private int orig_id;//3

    public String getImg_url() {
        return (img_url != null ? img_url : "");
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getId() {
        return (id != null ? id : "");
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return (title != null ? title : "");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        BigDecimal b = new BigDecimal(price);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public int getSalesnum() {
        return salesnum;
    }

    public void setSalesnum(int salesnum) {
        this.salesnum = salesnum;
    }

    public String getUrl() {
        return (url != null ? url : "");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey_id() {
        return (key_id != null ? key_id : "");
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public int getOrig_id() {
        return orig_id;
    }

    public void setOrig_id(int orig_id) {
        this.orig_id = orig_id;
    }
}
