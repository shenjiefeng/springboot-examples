package com.fsj.util;

public class CommonUtil {
    public static void print(Object obj){
        if (obj != null) System.out.println(obj.toString());
        else  print("Obj is null");
    }
}
