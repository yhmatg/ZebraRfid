package com.zebra.rfidreader.nonghang.zhongyou.model;

import java.util.ArrayList;
import java.util.List;

public class ProvinceModel {
    private String provinceID;
    private String province;
    private List<CityModel> cityList = new ArrayList<>();

    public String getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(String provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public ProvinceModel() {
        super();
    }

    public ProvinceModel(String province, List<CityModel> cityList) {
        super();
        this.province = province;
        this.cityList = cityList;
    }

    public List<CityModel> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityModel> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return "ProvinceModel [province=" + province + ", cityList=" + cityList + "]";
    }

}
