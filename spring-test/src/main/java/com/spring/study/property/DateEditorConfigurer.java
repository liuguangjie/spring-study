package com.spring.study.property;

import org.apache.log4j.helpers.DateTimeDateFormat;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.Ordered;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by free on 17-3-8.
 */
public class DateEditorConfigurer implements BeanFactoryPostProcessor,Ordered {

    private Integer order = 20000;

    private Set<String> pattens;

    private Map <String,DateFormat> patten2DateFormat=null;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (pattens!=null && pattens.size() !=0){
            if (patten2DateFormat==null){
                patten2DateFormat=new HashMap<String, DateFormat>(pattens.size());
            }
            for ( String patten : pattens){
                patten2DateFormat.put(patten,new SimpleDateFormat(patten));
            }

            beanFactory.addPropertyEditorRegistrar(new DatePropertyEditorRegistrar(Date.class,new DateEditor(patten2DateFormat)));
        }else {
            defaultRegister(beanFactory);
        }

    }

    private void defaultRegister(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addPropertyEditorRegistrar(new DatePropertyEditorRegistrar(Date.class,new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),true)));
    }


    public void setPattens(Set<String> pattens) {
        this.pattens = pattens;
    }

    public Set<String> getPattens() {
        return pattens;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    private static class DatePropertyEditorRegistrar implements PropertyEditorRegistrar {

        private final Class requiredType;

        private final PropertyEditor editor;

        private DatePropertyEditorRegistrar(Class requiredType, PropertyEditor editor) {
            this.requiredType = requiredType;
            this.editor = editor;
        }

        @Override
        public void registerCustomEditors(PropertyEditorRegistry registry) {
            registry.registerCustomEditor(requiredType,editor);
        }


    }
}
