package com.zebra.rfidreader.nonghang.zhongyou.model;

import java.util.Objects;

public class InvBean {
    private String areaName;
    private String epcCode;

    public InvBean(String areaName, String epcCode) {
        this.areaName = areaName;
        this.epcCode = epcCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getEpcCode() {
        return epcCode;
    }

    public void setEpcCode(String epcCode) {
        this.epcCode = epcCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvBean)) return false;
        InvBean invBean = (InvBean) o;
        return getAreaName().equals(invBean.getAreaName()) &&
                getEpcCode().equals(invBean.getEpcCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAreaName(), getEpcCode());
    }
}
