package com.example.yangjie.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1、生成对应的类
 * 2、
 */
public class ExcelUtil {


    public  static <T> List<T> readFile(String filePath ,T t,int sheetNo){
        List<T> result = new ArrayList<>();
        try {
            EasyExcel.read(new FileInputStream(filePath), t.getClass(), new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    result.add(data);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    System.out.println("excel文件读取完毕");
                }
            }).sheet(sheetNo).doRead();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public  static <T> List<T> inputFile(String filePath , T t,int sheetNo,List<T> list){
        List<T> result = new ArrayList<>();

        return result;
    }
}
