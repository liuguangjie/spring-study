package com.spring.study.collection;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by free on 16-12-30.
 */
public class InjectionCollections {
    private List<String> list=null;

    private Map<String,String> map =null;

    private Properties properties=null;

    private List<Properties> propertiesList=null;


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<Properties> getPropertiesList() {
        return propertiesList;
    }

    public void setPropertiesList(List<Properties> propertiesList) {
        this.propertiesList = propertiesList;
    }

}
