package com.example.parseallmethodandgetdex;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void filterIllegalString() {
//        ArrayList<String> classNameList, String className
        ArrayList<String> classNameList = new ArrayList<>();
        String[] filterArray = {"android", "java", "com.google", "org", "com.android.dex", "dalvik", "javax", "libcore", "c.h.b.a.a.a", "com.alipay", "com.amap.api", "com.alibaba.fastjson", "com.example", "com.facebook"};
        classNameList.add("org.c");
        classNameList.add("org.add");
        String className = "test.c";
        String more = "a.b,c.d, e.f,";
        String[] strArr = more.split(",");
        int filterArrayLength = filterArray.length;
        int strArrLength = strArr.length;

        System.out.print(filterArrayLength);
        filterArray = Arrays.copyOf(filterArray, filterArrayLength + strArrLength);
        System.out.print(filterArrayLength);
        System.arraycopy(strArr, 0, filterArray, filterArrayLength, strArrLength);
    }
}