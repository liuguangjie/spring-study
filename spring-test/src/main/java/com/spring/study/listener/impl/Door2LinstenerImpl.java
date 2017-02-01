package com.spring.study.listener.impl;

import com.spring.study.listener.DoorEvent;
import com.spring.study.listener.DoorLinstener;

/**
 * Created by free on 16-12-8.
 */
public class Door2LinstenerImpl implements DoorLinstener {

    public void doorEvent(DoorEvent doorEvent) {
        if(doorEvent.getDoorState().equals("open")){
            System.out.println("门2打开，同时打开走廊的灯");
        }else{
            System.out.println("门2关闭，同时关闭走廊的灯");
        }
    }

}
