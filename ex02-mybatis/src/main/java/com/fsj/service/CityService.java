package com.fsj.service;

import com.fsj.dao.CityMapper;
import com.fsj.entity.City;
import com.fsj.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityMapper cityMapper;

    public City queryByState(String state) {
        return cityMapper.queryByState(state);
    }

    public List<City> queryAll() {
        return cityMapper.queryAll();
    }

    public List<City> fuzzyQuery(String name) {
        assert name != null;
        List<City> res = cityMapper.fuzzyQuery(name.toUpperCase());
        return res == null ? new ArrayList<City>() : res;
    }

}
