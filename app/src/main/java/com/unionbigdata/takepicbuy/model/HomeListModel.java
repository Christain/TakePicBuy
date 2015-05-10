package com.unionbigdata.takepicbuy.model;

import java.util.ArrayList;

/**
 * Created by Christain on 15/5/9.
 */
public class HomeListModel {

    private int totalrecord;
    private int totalpage;
    private int pageno;
    private int pagesize;
    private ArrayList<HomeOrgModel> obj;

    public int getTotalrecord() {
        return totalrecord;
    }

    public void setTotalrecord(int totalrecord) {
        this.totalrecord = totalrecord;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public ArrayList<HomeOrgModel> getObj() {
        return (obj != null ? obj : new ArrayList<HomeOrgModel>());
    }

    public void setObj(ArrayList<HomeOrgModel> obj) {
        this.obj = obj;
    }
}
