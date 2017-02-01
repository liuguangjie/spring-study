package com.springframework.core.test;

import com.spring.study.listener.DoorManager;
import com.spring.study.listener.impl.Door1LinstenerImpl;
import com.spring.study.listener.impl.Door2LinstenerImpl;
import org.junit.Test;

/**
 * Created by free on 16-12-8.
 */
public class TestEvent {


    @Test
    public void testlistener(){
        DoorManager manager = new DoorManager();
        manager.addDoorListener(new Door1LinstenerImpl());// 给门1增加监听器
        manager.addDoorListener(new Door2LinstenerImpl());// 给门2增加监听器
        // 开门
        manager.fireWorkspaceOpened();
        System.out.println("我已经进来了");
        // 关门
        manager.fireWorkspaceClosed();

    }
}
