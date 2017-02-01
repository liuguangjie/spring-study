package com.spring.study.resolver;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * Created by free on 17-1-7.
 */
public class TestResolver implements ScopeMetadataResolver {


    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        return null;
    }



}
