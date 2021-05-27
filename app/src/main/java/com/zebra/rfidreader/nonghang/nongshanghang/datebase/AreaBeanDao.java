package com.zebra.rfidreader.nonghang.nongshanghang.datebase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.zebra.rfidreader.nonghang.zhongyou.model.AreaBean;

import java.util.List;

@Dao
public interface AreaBeanDao extends BaseDao<AreaBean> {

    @Query("DELETE FROM AreaBean")
    public void deleteAllData();

    @Query("SELECT * FROM AreaBean ")
    public List<AreaBean> getAllAreaBeans();

}
