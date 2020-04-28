package com.rookie.bigdata.util;

import org.springframework.util.AntPathMatcher;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/25 13:49
 */
public class AntPathMatcherMain {

    public static void main(String[] args) {

        AntPathMatcher antPathMatcher=new AntPathMatcher("A");
        System.out.println(antPathMatcher.match("A",null)); //返回false
        System.out.println(antPathMatcher.match("A","apath")); //返回false
        System.out.println(antPathMatcher.match("A","Apath")); //返回false


    }

}
