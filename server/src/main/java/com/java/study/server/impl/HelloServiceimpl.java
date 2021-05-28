package com.java.study.server.impl;

import com.java.study.api.HelloService;


/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 10:12
 */
public class HelloServiceimpl implements HelloService {
    @Override
    public String hello(String name) {
        System.out.println("HelloServiceImpl收到," + name);
        String ret = "Hello, " + name;
        System.out.println("HelloServiceImpl返回: " + ret + ".");





        return ret;



    }
}
