package com.java.study.netty.server;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-14 23:33
 */
public interface ServiceProviderRegistry {

    <T> void addServiceProvider(Class<? extends T> serviceClass,T serviceProvider);
}
