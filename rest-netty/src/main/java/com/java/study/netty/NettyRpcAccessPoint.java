package com.java.study.netty;

import com.java.study.netty.client.StubFactory;
import com.java.study.netty.server.ServiceProviderRegistry;
import com.java.study.netty.transport.RequestHandlerRegistry;
import com.java.study.netty.transport.Transport;
import com.java.study.netty.transport.TransportClient;
import com.java.study.netty.transport.TransportServer;
import com.java.study.rpc.RpcAccessPoint;
import com.java.study.rpc.spi.ServiceSupport;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 22:34
 */
public class NettyRpcAccessPoint implements RpcAccessPoint {

    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);
    //远程调用服务实例
    private TransportServer server = null;

    //获取 Netty远程调用信息
    private TransportClient client = ServiceSupport.load(TransportClient.class);
    private final Map<URI, Transport> clientMap = new ConcurrentHashMap<>();
    // 通过字节码编译代理类型的处理器
    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);

    //服务提供者注册器
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);


    //获取远程访问链接，如果不存在就创建
    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClass) {
        Transport transport = clientMap.computeIfAbsent(uri, this::createTransport);
        return stubFactory.createStub(transport,serviceClass);
    }


    //创建一个Netty访问链接
    private Transport createTransport(URI uri) {
        try {
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()), 30000L);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将服务提供者进行注册到容器中，并且返回远程调用的 IP+端口
     * @param service
     * @param serviceCLass
     * @param <T>
     * @return
     */
    @Override
    public synchronized  <T> URI addServiceProvider(T service, Class<T> serviceCLass) {
        serviceProviderRegistry.addServiceProvider(serviceCLass,service);
        return uri;
    }

    @Override
    public synchronized Closeable startServer() throws InterruptedException {
        if (server == null) {
            //加载远程调用服务信息
            server = ServiceSupport.load(TransportServer.class);

            //远程服务调用实例已经创建，初始化远程调用请求处理类信息（封装请求信息），初始化远程调用信息
            server.start(RequestHandlerRegistry.getInstance(),port);
        }
        return ()->{
            if (server != null) {
                server.stop();
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.stop();
        }
        client.close();
    }
}
