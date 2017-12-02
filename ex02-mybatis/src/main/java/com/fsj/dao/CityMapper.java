package com.fsj.dao;

import com.fsj.entity.City;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CityMapper {

    @Select("SELECT id, name, state, country FROM city WHERE state = #{state}")
    City queryByState(String state);

    @Select("select * from city")
    List<City> queryAll();

    //xml方式
    List<City> fuzzyQuery(@Param("name") String name);
}