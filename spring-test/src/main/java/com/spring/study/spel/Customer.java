package com.spring.study.spel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by free on 17-1-27.
 */
@Component("customerBean")
public class Customer {

    @Value("#{itemBean}")
    private Item item;
    @Value("#{itemBean.name}")
    private String itemName;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
