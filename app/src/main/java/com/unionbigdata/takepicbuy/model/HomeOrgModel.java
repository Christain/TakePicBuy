package com.unionbigdata.takepicbuy.model;

import java.util.ArrayList;

/**
 * Created by Christain on 15/5/10.
 */
public class HomeOrgModel {

    private ArrayList<HomeModel> groupData;

    public ArrayList<HomeModel> getGroupData() {
        return (groupData != null ? groupData : new ArrayList<HomeModel>());
    }

    public void setGroupData(ArrayList<HomeModel> groupData) {
        this.groupData = groupData;
    }
}