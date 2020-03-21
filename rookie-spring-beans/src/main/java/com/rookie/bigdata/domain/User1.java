package com.rookie.bigdata.domain;

import org.springframework.beans.factory.BeanNameAware;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/21 23:51
 */
public class User1 implements BeanNameAware {
    private String id;
    private String name;
    private String address;
    @Override
    public void setBeanName(String name) {
        this.id=name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User1{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
