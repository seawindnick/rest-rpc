package com.java.study.netty.client;

import com.java.study.netty.transport.Transport;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 21:51
 */
public interface ServiceStub {
    void setTransport(Transport transport);
}
