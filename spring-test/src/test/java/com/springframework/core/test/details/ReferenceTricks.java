package com.springframework.core.test.details;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @Author ms.liu
 * ~~Email 18310693623@163.com
 * @Date 2018-06-02 上午11:00
 */
public class ReferenceTricks {

    /*public static void main(String[] args) {
        ReferenceTricks r = new ReferenceTricks();
        // reset integer
        r.i = 0;
        System.out.println("Before changeInteger:" + r.i);
        changeInteger(r);
        System.out.println("After changeInteger:" + r.i);

        // just for format
        System.out.println();

// reset integer
        r.i = 0;
        System.out.println("Before changeReference:" + r.i);
        changeReference(r);

        System.out.println("After changeReference:" + r.i);


        // 0 5 5
        // 0 5 0
    }*/


    /**
     * 强引用、软引用、弱引用、虚引用这四个概念
     * @param args
     */
    public static void main(String[] args) {
        Object obj = new Object();
        ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
        WeakReference<Object> weakRef = new WeakReference<Object>(obj, refQueue);
        System.out.println(weakRef.get());
        System.out.println(refQueue.poll());
        obj = null;
        System.gc();

        System.out.println(weakRef.get());
        System.out.println(refQueue.poll());



    }

    private static void changeReference(ReferenceTricks r) {
        r = new ReferenceTricks();
        r.i = 5;
        System.out.println("In changeReference: " + r.i);
    }

    private static void changeInteger(ReferenceTricks r) {
        r.i = 5;
        System.out.println("In changeInteger:" + r.i);
    }

    public int i;

}
