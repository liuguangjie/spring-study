package com.springframework.core.test;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by free on 16-12-9.
 */
public class TestClassLoader {

    @Before
    public void Before(){
    }


    @Test
    public void testCLass() throws IOException {

        ClassLoader classLoader=TestClassLoader.class.getClassLoader();
        Enumeration enumeration=classLoader.getResources("META-INF/spring.handlers");
        Properties properties=new Properties();
        while (enumeration.hasMoreElements()){

            URL url = (URL) enumeration.nextElement();
            System.out.println(url);
            InputStream is = null;
            try {
                URLConnection con = url.openConnection();
                con.setUseCaches(false);
                is = con.getInputStream();
                properties.load(is);
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        Enumeration keys=properties.keys();
        while (keys.hasMoreElements()){
            Object key=keys.nextElement();
            System.out.println(key + "\t" + properties.get(key) );
        }
    }

    @Test
    public void testKey(){
        Properties properties=new Properties();
        properties.put("key1","v1");
        properties.put("key1","v2");
        Enumeration<Object> enumeration=properties.keys();
        while (enumeration.hasMoreElements()){
            Object key=enumeration.nextElement();
            System.out.println(key+"   "+properties.get(key));
        }

    }

}
