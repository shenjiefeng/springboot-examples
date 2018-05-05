package com.fsj.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.fsj.util.ExcelUtils.writeToExcel;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping(value = "/excel")
    public Object excel(){
        List<LinkedHashMap<String, String>> mapList = new ArrayList<>();
        LinkedHashMap<String, String> m1 = new LinkedHashMap<>();
        m1.put("k1", "v1");
        m1.put("k2", "v2");
        m1.put("k3", "v1");
        m1.put("k4", "v2");
        m1.put("k5", "v1");
        m1.put("k6", "v2");
        m1.put("k7", "v1");
        m1.put("k8", "v2");
        m1.put("k9", "v1");
        m1.put("k10", "v2");
        mapList.add(m1);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("t");
        arrayList.add("t");
        arrayList.add("t");
        arrayList.add("t");
        arrayList.add("5:1:1");
        arrayList.add("5:2:1");
        arrayList.add("6:1:1");
        arrayList.add("6:1:2");
        arrayList.add("7:1:1");
        arrayList.add("7:1:2");
        arrayList.add("|:|:|");

        try {
            writeToExcel(mapList, arrayList, "/Users/fsj/tmp/test.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(mapList);
        return mapList;
    }
}
