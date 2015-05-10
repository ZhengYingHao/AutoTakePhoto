package com.example.zyh.autotakephoto.dao;


import com.example.zyh.autotakephoto.dao.impl.DateDataDaoImpl;
import com.example.zyh.autotakephoto.model.DateData;

import java.util.List;

public interface DateDataDao {

    String DELETE_ALL = "ALL";
    String DELETE_SINGLE = "SINGLE";
    int DEFAULT_ID = 0;

    void downloadDateData(DateDataDaoImpl.OnFinishGetDateData listener);
    void deleteDateData(String deleteType, int dateId);
    List<DateData> getDateData();
}
