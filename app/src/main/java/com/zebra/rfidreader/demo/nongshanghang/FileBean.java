package com.zebra.rfidreader.demo.nongshanghang;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.w3c.dom.TypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//资产详情
@Entity
public class FileBean {

    //批次号
    private String batchCode;
    //业务开始日期
    private String startDate;
    //业务结束日期
    private String endDate;
    //EPC编码
    @PrimaryKey
    @NonNull
    private String epcCode;
    //封袋编号
    private String bagCode;
    //登记机构号
    private String registerCode;
    //档案所属机构名称
    private String orgName;
    //档案种类
    private String fileType;
    //档案名称
    private String fileName;
    //档案本数
    private String fileNumber;
    //档案箱编号
    private String boxCode;
    //盘点状态 0未盘点 1已经盘点
    @Ignore
    private Boolean invStatus = false;
    @Ignore
    private List<EpcBean> epcs = new ArrayList<>();

    public FileBean() {
    }

    public FileBean(String epcCode, String bagCode, String boxCode) {
        this.epcCode = epcCode;
        this.bagCode = bagCode;
        this.boxCode = boxCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @NonNull
    public String getEpcCode() {
        return epcCode;
    }

    public void setEpcCode(@NonNull String epcCode) {
        this.epcCode = epcCode;
    }

    public String getBagCode() {
        return bagCode;
    }

    public void setBagCode(String bagCode) {
        this.bagCode = bagCode;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Boolean getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Boolean invStatus) {
        this.invStatus = invStatus;
    }

    public List<EpcBean> getEpcs() {
        return epcs;
    }

    public void setEpcs(List<EpcBean> epcs) {
        this.epcs = epcs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileBean)) return false;
        FileBean fileBean = (FileBean) o;
        return getEpcCode().equals(fileBean.getEpcCode()) &&
                getBagCode().equals(fileBean.getBagCode()) &&
                getFileName().equals(fileBean.getFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpcCode(), getBagCode(), getFileName());
    }
}
