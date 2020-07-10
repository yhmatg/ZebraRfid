package com.zebra.rfidreader.demo.nongshanghang;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;


import java.util.List;
import java.util.Set;

@Dao
public interface FileBeanDao extends BaseDao<FileBean> {

    @Query("DELETE FROM FileBean")
    public void deleteAllData();

    @Query("SELECT * FROM FileBean where boxCode in (:boxCodes)")
    public List<FileBean> getFileBeansByBoxCode(Set<String> boxCodes);

    @Query("SELECT * FROM FileBean ")
    public List<FileBean> getAllFileBeans();

    @Query("SELECT * FROM FileBean where boxCode = :mBoxCode")
    public List<FileBean> getFileBeanByBoxCode(String mBoxCode);
}
