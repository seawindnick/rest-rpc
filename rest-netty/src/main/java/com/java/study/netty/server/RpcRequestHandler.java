package com.java.study.netty.server;

import com.java.study.netty.client.stubs.RpcRequest;
import com.java.study.netty.serialize.SerializeSupport;
import com.java.study.netty.transport.RequestHandler;
import com.java.study.netty.transport.command.Code;
import com.java.study.netty.transport.command.Command;
import com.java.study.rpc.spi.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-14 23:34
 */
@Slf4j
@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private Map<String, Object> serviceProviders = new HashMap<>();

    @Override
    public Command handle(Command command) {
        // 获取请求头信息
        Command.Header header = command.getHeader();
        //获取对应的请求体信息 第一个字节仍然是编码格式
        RpcRequest rpcRequest = SerializeSupport.parse(command.getPayload());

        try {
            //根据请求的接口类名获取对应的服务提供者
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if (serviceProvider != null) {
                //获取请求参数信息，第一个字节是编码格式 TODO 为什么定义了两遍编码格式？
                String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                //通过反射调用实例接口
                String result = (String) method.invoke(serviceProvider, arg);
                // 组装返回信息
                Command.ResponseHeader responseHeader = new Command.ResponseHeader(type(), header.getVersion(), header.getRequestId());
                return new Command(responseHeader, SerializeSupport.serialize(result));
            }

            // 如果没找到，返回NO_PROVIDER错误响应。
            log.warn("No service Provider of {}#{}(String)!", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return new Command(new Command.ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.code, "No provider!"), new byte[0]);
        } catch (Throwable t) {
            // 发生异常，返回UNKNOWN_ERROR错误响应。
            log.warn("Exception: ", t);
            return new Command(new Command.ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.code, t.getMessage()), new byte[0]);
        }
    }

    /**
     * String 协议格式
     * @return
     */
    @Override
    public int type() {
        return 0;
    }

    /**
     * 将服务提供者的类型和对应的实例信息存储到容器中
     * @param serviceClass
     * @param serviceProvider
     * @param <T>
     */
    @Override
    public synchronized <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        log.info("Add service: {}, provider: {}.",
                serviceClass.getCanonicalName(),
                serviceProvider.getClass().getCanonicalName());
    }


}
