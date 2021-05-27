package com.zebra.rfidreader.nonghang.zhongyou.model;

import java.util.ArrayList;
import java.util.List;

public class CityModel {
    private String City;
    private String CityID;
    private List<DistrictModel> districtList = new ArrayList<>();

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public CityModel() {
        super();
    }

    public CityModel(String City, List<DistrictModel> districtList) {
        super();
        this.City = City;
        this.districtList = districtList;
    }

    public List<DistrictModel> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<DistrictModel> districtList) {
        this.districtList = districtList;
    }

    @Override
    public String toString() {
        return "CityModel [City=" + City + ", districtList=" + districtList
                + "]";
    }

}
