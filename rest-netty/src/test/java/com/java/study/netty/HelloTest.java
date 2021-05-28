package com.java.study.netty;

import java.util.ServiceLoader;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-16 21:49
 */
public class HelloTest {
    public static void main(String[] args) {
        ServiceLoader<IService> serviceLoader  =  ServiceLoader.load(IService.class);
        for (IService iService : serviceLoader) {
            iService.sayHello();
        }
    }
}
