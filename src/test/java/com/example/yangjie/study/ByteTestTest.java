package com.example.yangjie.study; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* ByteTest Tester. 
* 
* @author <Authors name> 
* @since <pre>9月 2, 2021</pre> 
* @version 1.0 
*/ 
public class ByteTestTest {
    ByteTest x = new ByteTest();

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: sum(int i, int j) 
* 
*/ 
@Test
public void testSum() throws Exception { 
//TODO: Test goes here...
    int b = x.sum(1,2);
    System.out.println(b);
    assert b==3;
} 


} 
