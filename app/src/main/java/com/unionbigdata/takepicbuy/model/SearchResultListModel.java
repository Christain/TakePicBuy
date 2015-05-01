package com.unionbigdata.takepicbuy.model;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/4/27.
 */
public class SearchResultListModel {

    private int page_sum;

    private String srcimageurl;

    private int page_listsize;

    private int page;

    private ArrayList<ArrayList<SearchResultModel>> searchresult;

    public ArrayList<ArrayList<SearchResultModel>> getSearchresult() {
        return searchresult;
    }

    public void setSearchresult(ArrayList<ArrayList<SearchResultModel>> searchresult) {
        this.searchresult = searchresult;
    }

    public int getPage_sum() {
        return page_sum;
    }

    public void setPage_sum(int page_sum) {
        this.page_sum = page_sum;
    }

    public String getSrcimageurl() {
        return (srcimageurl != null ? srcimageurl : "");
    }

    public void setSrcimageurl(String srcimageurl) {
        this.srcimageurl = srcimageurl;
    }

    public int getPage_listsize() {
        return page_listsize;
    }

    public void setPage_listsize(int page_listsize) {
        this.page_listsize = page_listsize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
