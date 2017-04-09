package com.spring.study.event;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by free on 17-4-9.
 */
@Component
public class Alistener implements ApplicationListener<ContentEvent> {
    @Async
    public void onApplicationEvent(ContentEvent event) {

        System.out.println(" pppppppppppppppppp ----  "+event);
    }
}
