package com.spring.study.listener;

import java.util.EventObject;

/**
 * Created by free on 16-12-8.
 */
public class DoorEvent extends EventObject {

    private String doorState = "";

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DoorEvent(Object source,String doorState) {
        super(source);
        this.doorState=doorState;
    }

    public String getDoorState() {
        return doorState;
    }

    public void setDoorState(String doorState) {
        this.doorState = doorState;
    }


}
