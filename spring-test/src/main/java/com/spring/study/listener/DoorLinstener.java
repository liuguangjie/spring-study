package com.spring.study.listener;

import java.util.EventListener;

/**
 * Created by free on 16-12-8.
 */
public interface DoorLinstener extends EventListener {

    public void doorEvent(DoorEvent doorEvent);

}
