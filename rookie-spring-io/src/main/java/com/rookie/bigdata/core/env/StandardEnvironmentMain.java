package com.rookie.bigdata.core.env;

import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Set;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/25 14:54
 */
public class StandardEnvironmentMain {
    public static void main(String[] args) {

        StandardEnvironment standardEnvironment=new StandardEnvironment();
        System.out.println(standardEnvironment);


        Map<String,Object> map=(Map) System.getProperties();

        for(Map.Entry<String, Object> entry : map.entrySet()){
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            System.out.println(mapKey+":"+mapValue.toString());
        }
    }

}
