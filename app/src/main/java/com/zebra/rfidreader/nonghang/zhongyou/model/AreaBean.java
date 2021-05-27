package com.zebra.rfidreader.nonghang.zhongyou.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity
public class AreaBean {
    @PrimaryKey
    @NonNull
    private String areaId;
    private String areaName;
    private String superAreaId;

    public AreaBean(String areaId, String areaName, String superAreaId) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.superAreaId = superAreaId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSuperAreaId() {
        return superAreaId;
    }

    public void setSuperAreaId(String superAreaId) {
        this.superAreaId = superAreaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AreaBean)) return false;
        AreaBean areaBean = (AreaBean) o;
        return getAreaId().equals(areaBean.getAreaId()) &&
                getAreaName().equals(areaBean.getAreaName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAreaId(), getAreaName());
    }
}
