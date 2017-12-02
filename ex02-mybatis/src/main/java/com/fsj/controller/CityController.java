package com.fsj.controller;


import com.fsj.dao.CityMapper;
import com.fsj.entity.City;
import com.fsj.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mybatis")
public class CityController {

    @Autowired
    private CityService cityService;

    @RequestMapping("/queryByState")
    public City queryByState(@RequestParam(value = "state", defaultValue = "CA") String state){
        return cityService.queryByState(state);
    }

    @RequestMapping("/queryAll")
    public List<City> queryAll(){
        return cityService.queryAll();
    }

    @RequestMapping("/fuzzyQuery")
    public List<City> fuzzyQuery(@RequestParam(value = "name") String name){
        return cityService.fuzzyQuery(name);
    }
}
