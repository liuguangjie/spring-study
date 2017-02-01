package com.springframework.core.test;

import org.junit.Test;

/**
 * Created by free on 16-12-5.
 *
 * 一般的常用的练习
 */
public class OtherTest {

    @Test
    public void testShanjiao(){

        System.out.println("heoo");
        /** 控制输出*号    */
        int h=1;
        for (int i=0;i<7;i++){
            /** 先打空格 */
            for (int j=0;j<6-i;j++){
                System.out.print(" ");
            }
            /** 在计算 符号 */
            // 1 3 5 7 9
            for(int k=0;k<h;k++){
                System.out.print("*");
            }
            h=h+2;
            System.out.println();
        }

    }






/**
      *
     ***
    *****
   *******
  *********
 ***********
*************

*/
}
