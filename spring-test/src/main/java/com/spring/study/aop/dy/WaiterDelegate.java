package com.spring.study.aop.dy;

/**
 * Created by free on 17-3-6.
 */
public class WaiterDelegate {

    private Waiter waiter ;


    public void service(String clentName){
        if (waiter!=null){
            waiter.greetTo(clentName);
            waiter.serveTo(clentName);
        }
    }

    public synchronized void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }
}
