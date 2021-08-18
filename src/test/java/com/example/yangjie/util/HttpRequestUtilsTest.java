package com.example.yangjie.util; 

import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

/** 
* HttpRequestUtils Tester. 
* 
* @author <Authors name> 
* @since <pre>8月 18, 2021</pre> 
* @version 1.0 
*/ 
public class HttpRequestUtilsTest { 

    String url ;
    Map<String, String>params;

@Before
public void before() throws Exception {
    url ="https://restapi.amap.com/v3/geocode/geo";
    params = new HashMap<>();
    params.put("key","null");
    params.put("address","北京市朝阳区阜通东大街6号");

} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: sendGet(String url, Map<String, String> params, Map<String, String> header) 
* 
*/ 
@Test
public void testSendGet() throws Exception {
    String sendGet = HttpRequestUtils.sendGet(url, params, null);
    System.out.println("result:"+sendGet);
}

/** 
* 
* Method: sendPostJson(String url, String json, Map<String, String> header) 
* 
*/ 
@Test
public void testSendPostJson() throws Exception {
    String postJson = HttpRequestUtils.sendPostJson(url, JSONObject.toJSONString(params), null);
    System.out.println("result:"+postJson);
}

/** 
* 
* Method: sendPostForm(String url, Map<String, String> params, Map<String, String> header) 
* 
*/ 
@Test
public void testSendPostForm() throws Exception {
    String postJson = HttpRequestUtils.sendPostForm(url, params, null);
    System.out.println("result:"+postJson);
}


} 
