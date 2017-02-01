package com.springframework.core.test.javaconfig;

import com.spring.study.beans.Happy;
import com.spring.study.javaconfig.AppConfig;
import com.spring.study.javaconfig.HelloConfig;
import com.spring.study.javaconfig.MainConfig;
import com.springframework.core.test.LoadClassFilterApplicationContext;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by free on 17-1-25.
 */
public class JavaConfigTest {
    private LoadClassFilterApplicationContext context = null;

    @Before
    public void testBefore() {
        context = new LoadClassFilterApplicationContext();
    }

    @Test
    public void testConfig() {
        HelloConfig helloConfig = context.getBean(HelloConfig.class, AppConfig.class);
        helloConfig.sayJavaConfig();

        System.out.println(helloConfig + " == " + context.getBean(HelloConfig.class));
    }

    @Test
    public void testImportAnnotation() {
        HelloConfig helloConfig = context.getBean(HelloConfig.class, MainConfig.class);
        helloConfig.sayJavaConfig();

        Happy happy = context.getBean(Happy.class);
        happy.destroy();
    }


}
