package com.spring.study.event;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by free on 17-4-9.
 * 定义无序监听器
 */
@Component
public class LisiListener implements ApplicationListener<ContentEvent> {


    /** 异步执行 提高效率 */
    @Async
    public void onApplicationEvent(ContentEvent event) {
        System.out.println(" 收到的内容是 ...  "+event);
    }
}
