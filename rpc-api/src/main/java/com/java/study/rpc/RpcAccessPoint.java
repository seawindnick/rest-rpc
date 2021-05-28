package com.java.study.rpc;

import com.java.study.rpc.spi.ServiceSupport;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 10:00
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * 获取客户端远程服务引用
     *
     * @param uri
     * @param serviceClass
     * @param <T>
     * @return
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);


    /**
     * 服务端注册服务的实现实例
     *
     * @param service
     * @param serviceCLass
     * @param <T>
     * @return
     */
    <T> URI addServiceProvider(T service, Class<T> serviceCLass);


    /**
     * 服务端启动RPC框架，监听接口，开始提供远程服务
     *
     * @return 服务实例，用于程序停止时安全关闭服务
     */
    Closeable startServer() throws InterruptedException;


    /**
     * 获取注册中心引用
     * 和注册中心建立连接
     * @param nameServiceUri 参数为住户册文件路径信息
     * @return
     */

    default NameService getNameService(URI nameServiceUri) {

        Collection<NameService> nameServices = ServiceSupport.loadAll(NameService.class);
        for (NameService nameService : nameServices) {
            if (nameService.supportedSchemes().contains(nameServiceUri.getScheme())) {
                //连接配置文件，有一个连接实现即可
                // TODO 为什么只选择第一个？
                nameService.connect(nameServiceUri);
                return nameService;
            }
        }
        return null;
    }

}
