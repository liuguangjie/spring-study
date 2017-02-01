package com.springframework.core.test;

import org.junit.Test;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Created by free on 16-12-30.
 */
public class TestParameterNameDiscoverer {

    @Test
    public void testParams() throws NoSuchMethodException {
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
       // String[] args=parameterNameDiscoverer.getParameterNames(this.getClass().getConstructor(new Class[0]));
        String[] args =parameterNameDiscoverer.getParameterNames(this.getClass().getMethod("testParams",null));
        System.out.println(args[0]);
    }
}
