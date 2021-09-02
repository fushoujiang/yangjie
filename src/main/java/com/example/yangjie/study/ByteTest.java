package com.example.yangjie.study;

import java.util.HashMap;
import java.util.Map;

/**
 * Byte使用场景
 */
public class ByteTest {

    public int sum (int i,int j){
        int sum=i+j;
        return sum;
    }


    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("yangjie","nan");
        map.put("fushoujiang","nan");
        String s = (String) map.get("yangjie");
        String s1 = (String) map.get("fushoujiang");
        System.out.printf(s+"/"+s1);
    }

}
