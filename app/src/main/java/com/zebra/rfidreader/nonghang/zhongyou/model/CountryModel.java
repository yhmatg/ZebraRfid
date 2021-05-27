package com.zebra.rfidreader.nonghang.zhongyou.model;

import java.util.ArrayList;
import java.util.List;

public class CountryModel {
    private String Country;
    private List<ProvinceModel> provinceList = new ArrayList<>();

    public List<ProvinceModel> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceModel> provinceList) {
        this.provinceList = provinceList;
    }

    public CountryModel() {
        super();
    }

    public CountryModel(String Country, List<ProvinceModel> provinceList) {
        super();
        this.Country = Country;
        this.provinceList = provinceList;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    @Override
    public String toString() {
        return "ProvinceModel [Country=" + Country + ", provinceList=" + provinceList + "]";
    }

}
