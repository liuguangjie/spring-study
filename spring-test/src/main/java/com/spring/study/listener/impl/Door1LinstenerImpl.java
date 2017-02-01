package com.spring.study.listener.impl;

import com.spring.study.listener.DoorEvent;
import com.spring.study.listener.DoorLinstener;

/**
 * Created by free on 16-12-8.
 */
public class Door1LinstenerImpl  implements DoorLinstener{


    public void doorEvent(DoorEvent doorEvent) {
        if(doorEvent.getDoorState().equals("open")){
            System.out.println("门1开");
        }else{
            System.out.println("门1关");
        }
    }

}
