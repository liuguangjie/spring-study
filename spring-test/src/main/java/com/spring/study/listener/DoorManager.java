package com.spring.study.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by free on 16-12-8.
 */
public class DoorManager {

    private Collection linsteners;

    public void addDoorListener(DoorLinstener doorLinstener){
        if(linsteners==null){
            linsteners=new HashSet();
        }
        linsteners.add(doorLinstener);

    }

    public void removeDoorLinstener(DoorLinstener doorLinstener){
        if (doorLinstener==null){
            return;
        }
        linsteners.remove(doorLinstener);
    }


    /**
     * 触发开门事件
     */
    public void fireWorkspaceOpened() {
        if (linsteners == null)
            return;
        DoorEvent event = new DoorEvent(this, "open");
        notifyListeners(event);
    }

    /**
     * 触发关门事件
     */
    public void fireWorkspaceClosed() {
        if (linsteners == null)
            return;
        DoorEvent event = new DoorEvent(this, "close");
        notifyListeners(event);
    }

    /**
     * 通知所有的DoorListener
     */
    private void notifyListeners(DoorEvent event) {
        Iterator iter = linsteners.iterator();
        while (iter.hasNext()) {
            DoorLinstener listener = (DoorLinstener) iter.next();
            listener.doorEvent(event);
        }
    }

}
