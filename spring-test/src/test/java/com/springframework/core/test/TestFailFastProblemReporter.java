package com.springframework.core.test;

import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Map;

/**
 * Created by free on 16-12-8.
 *
 * org.springframework.beans.factory.parsing.FailFastProblemReporter 类的测试单元
 *
 */
public class TestFailFastProblemReporter {
    private String config= "spel/spring-spel.xml";
    private ResourceLoader resourceLoader=null;
    private Resource resource=null;

    /*@Before
    public void Before(){
        resourceLoader=new DefaultResourceLoader();
        resource=resourceLoader.getResource(config);
    }*/


    @Test
    public void testProblem(){
        /*Location location=new Location(resource);
        Problem problem=new Problem("may error .... ",location);

        *//** 处理bean定义 错误和警告 *//*
        ProblemReporter problemReporter = new FailFastProblemReporter();
        System.out.println(problem.getRootCause());
        problemReporter.fatal(problem);*/
        System.out.println(System.currentTimeMillis()/1000);
        String appId="SV-ABC-0000";
        String appKey="e8a1e19058c0928d7690cfd59c6b062d";
        String timestamp="1234567890";
        String body="{\"userId\":\"u12\",\"deviceType\":\"101c120024000810e2010540000044\"}";

        String s=getSign(appId,appKey,timestamp,body);
        System.out.println(s);
        // bd4495183b97e8133aeab2f1916fed41

    }




    public static KeyStore getHttpsKeyStore()
    {
        InputStream ins = null;
        try {

            ins = new FileInputStream("srca.cer");
            //读取证书
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");  //问1
            Certificate cer = cerFactory.generateCertificate(ins);
            //创建一个证书库，并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());   //问2
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(ins!=null)
            {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public void initSSLContext(Map<String,String> headers,URL url,String body) throws Exception{
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(getHttpsKeyStore());
        sslContext.init( null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        HttpsURLConnection httpsURLConnection=(HttpsURLConnection)url.openConnection();
        /** 添加headers  */
        if (headers!=null){
            for (String key : headers.keySet() ){
                httpsURLConnection.setRequestProperty(key,headers.get(key));
            }
        }


        /**添加 body */
        OutputStream out=httpsURLConnection.getOutputStream();

        out.write(body.getBytes());

        httpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        httpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession sslsession) {

                if("localhost".equals(hostname)){
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Test
    public void testLogin(){


    }

    String BinaryToHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        String hexStr = "0123456789abcdef";
        for (int i = 0; i < bytes.length; i++) {
            hex.append(String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4)));
            hex.append(String.valueOf(hexStr.charAt(bytes[i] & 0x0F)));
        }
        return hex.toString();
    }

    String getSign(String appId, String appKey, String timestamp, String body){
        appKey = appKey.trim();
        appKey = appKey.replaceAll("\"", "");

        if (body != null) {
            body = body.trim();
        }
        if (!body.equals("")) {
            body = body.replaceAll("", "");
            body = body.replaceAll("\t", "");
            body = body.replaceAll("\r", "");
            body = body.replaceAll("\n", "");
        }
        System.out.println("body:"+body);
        StringBuffer sb = new StringBuffer();
        sb.append(body).append(appId).append(appKey).append(timestamp);

        MessageDigest md = null;
        byte[] bytes = null;
        try {
            md = MessageDigest.getInstance("MD5");
            bytes = md.digest(sb.toString().getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BinaryToHexString(bytes);
    }

}
