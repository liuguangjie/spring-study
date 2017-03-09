package com.spring.study.property;

import com.spring.study.beans.Bananer;
import org.springframework.core.io.Resource;

import java.util.*;

/**
 * Created by free on 17-3-7.
 */
public class BeanWrapList {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    private Bananer bananer;

    private Resource resource;

    private int num[];

    private List<Bananer> bananerList=new ArrayList<Bananer>();

    private Map<String,Bananer> bananerMap=new HashMap<String, Bananer>();

    public Bananer getBananer() {
        return bananer;
    }

    public void setBananer(Bananer bananer) {
        this.bananer = bananer;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    private Date date;


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int[] getNum() {
        return num;
    }

    public void setNum(int[] num) {
        this.num = num;
    }

    public List<Bananer> getBananerList() {
        return bananerList;
    }

    public void setBananerList(List<Bananer> bananerList) {
        this.bananerList = bananerList;
    }


    public Map<String, Bananer> getBananerMap() {
        return bananerMap;
    }

    public void setBananerMap(Map<String, Bananer> bananerMap) {
        this.bananerMap = bananerMap;
    }
}
