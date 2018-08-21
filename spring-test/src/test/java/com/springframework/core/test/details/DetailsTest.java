package com.springframework.core.test.details;

import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * @Author ms.liu
 * ~~Email 18310693623@163.com
 * @Date 2018-06-01 下午10:35
 */
public class DetailsTest {

    @Test
    public void test1() {

        //System.out.println(StringUtils.replace("string,string","s", "a"));

        //System.out.println("".replace("", ""));

//        System.out.println(replace("string,string","s", "a"));

        StringUtils.cleanPath("string,string");
    }




    /**
     * 不用正则表达式完成 字符串的替换
     * @param content
     * @param old
     * @param newStr
     * @return
     */
    public String replace(String content, String old, String newStr) {
        int pos = 0;

        int rLength = newStr.length();

        int index = content.indexOf(old);

        StringBuilder sb = new StringBuilder();

        while (index >= 0) {
            sb.append(content.substring(pos, index));
            sb.append(newStr);
            pos = index + rLength;
            index = content.indexOf(old, pos);
        }
        sb.append(content.substring(pos));


        return sb.toString();
    }
}
