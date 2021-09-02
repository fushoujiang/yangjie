package com.example.yangjie.jmeter;

import com.example.yangjie.util.HttpRequestUtils;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Map;


/**
 * jmeter自动化test
 */
public class HttpJmeterTest extends AbstractJavaSamplerClient {

    private String httpUrl = null;

    private Map<String,String> requestParams;

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();//计时开始
        try {
           String  resultData =  HttpRequestUtils.sendGet(httpUrl,requestParams,null);
           sampleResult.setResponseMessage(resultData);
        } catch (Exception e) {
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(false);
            e.printStackTrace();
        }
        sampleResult.sampleEnd();
        sampleResult.setSuccessful(true);
        return sampleResult;
    }


    @Override
    public void setupTest(JavaSamplerContext context) {
        httpUrl = context.getParameter("httpUrl");
        requestParams.put("key",context.getParameter("key"));
        requestParams.put("address",context.getParameter("address"));
        super.setupTest(context);
    }

}
