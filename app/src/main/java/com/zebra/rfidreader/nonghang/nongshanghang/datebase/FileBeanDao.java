package com.zebra.rfidreader.nonghang.nongshanghang.datebase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;


import com.zebra.rfidreader.nonghang.nongshanghang.shinv.FileBean;

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

    //精确查询
    @Query("SELECT * FROM FileBean where boxCode = :mBoxCode")
    public List<FileBean> getFileBeanByBoxCode(String mBoxCode);

    //模糊查询
    @Query("SELECT * FROM FileBean where boxCode LIKE '%' || :mBoxCode || '%'")
    public List<FileBean> SearchFileBeanByBoxCode(String mBoxCode);
}
