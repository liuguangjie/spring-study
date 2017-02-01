package com.springframework.core.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;

/**
 * Created by free on 16-12-16.
 */
public class TestProperty {
    protected final Log logger = LogFactory.getLog(getClass());
    private MutablePropertySources propertySources = new MutablePropertySources(null);
    @Test
    public void testGet(){
        PropertySourcesPropertyResolver propertyResolver=new PropertySourcesPropertyResolver(propertySources);
        String value=propertyResolver.getProperty("user.dir");
        System.out.println(value);
    }
}
