package com.springframework.core.test;

import org.junit.Test;

/**
 * 回顾
 * @Author ms.liu
 * ~~Email liuguangj@dingtalk.com
 * @Date 2018-02-19 下午6:51
 */
public class Review {



    @Test
    public void binarySerach() {

        Integer[] orders = {1, 2, 3, 5, 6, 15, 19, 25};

        Integer index = 0;

        Integer last = orders.length - 1;

        Integer num = 19;

        while (index <= last) {
            Integer mid = (index + last) /2;

            if (num > orders[mid]) {
                index = mid + 1;
            } else if (num < orders[mid]) {
                last = mid - 1;
            } else if (num == orders[mid]) {
                System.out.println(orders[mid] + "  " + mid);
                break;
            }else {
                System.out.println("-1");
                break;
            }

        }

    }

}
