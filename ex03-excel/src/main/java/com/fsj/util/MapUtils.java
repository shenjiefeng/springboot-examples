package com.fsj.util;

import java.util.*;

public class MapUtils {
    public static <K,V> Map<K, V> sortMapByValue(Map<K, V> oriMap,Comparator<Map.Entry<K, V>> comparator) {
        Map<K, V> sortedMap = new LinkedHashMap<>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<K, V>> entryList = new LinkedList<>(oriMap.entrySet());
            Collections.sort(entryList,comparator
                    );
            Iterator<Map.Entry<K, V>> iter = entryList.iterator();
            Map.Entry<K, V> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }
}
