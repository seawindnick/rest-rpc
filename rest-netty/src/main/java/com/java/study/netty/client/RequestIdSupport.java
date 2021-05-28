package com.java.study.netty.client;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 21:50
 */
public class RequestIdSupport {
    private final static AtomicInteger nextRequest = new AtomicInteger(0);

    public static int next() {
        return nextRequest.getAndIncrement();
    }
}
