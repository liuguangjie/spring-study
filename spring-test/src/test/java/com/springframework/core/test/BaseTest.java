package com.springframework.core.test;

import org.junit.Before;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Created by free on 17-1-6.
 */
public abstract class BaseTest {


    protected ResourceLoader resourceLoader=null;
    protected Resource resource=null;
    protected XmlBeanFactory beanFactory =null;

    @Before
    public void loadSource(){
        resourceLoader=new DefaultResourceLoader();
        resource=resourceLoader.getResource(loadConfig());
        beanFactory=new XmlBeanFactory(resource);
    }

    protected abstract String loadConfig();

    protected Resource getResource(){
        if(this.resource==null){
             throw new RuntimeException("文件为空");
        }
        return this.resource;
    }
}
