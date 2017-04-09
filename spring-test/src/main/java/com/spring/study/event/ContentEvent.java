package com.spring.study.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by free on 17-4-9.
 *  定义事件
 *
 */
public class ContentEvent extends ApplicationEvent {


    public ContentEvent(String content) {
        super(content);
    }
}
