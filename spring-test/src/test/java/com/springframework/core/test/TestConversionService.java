package com.springframework.core.test;

import org.junit.Test;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.lang.reflect.Type;

/**
 * Created by free on 16-12-21.
 */
public class TestConversionService {

    @Test
    public void testGet() {
        ConfigurableConversionService conversionService = new DefaultConversionService();

        boolean b=conversionService.convert("true",Boolean.class);
        System.out.println(b);
        /*clazz=clazz.getSuperclass();
        System.out.println(clazz.getGenericInterfaces()[0]);*/

    }
}
