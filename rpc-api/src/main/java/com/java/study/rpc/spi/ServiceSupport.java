package com.java.study.rpc.spi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <Description>
 * SPI类加载器
 *
 * @author hushiye
 * @since 2020-12-13 17:11
 */
public class ServiceSupport {


    private final static Map<String, Object> singletonServices = new HashMap<>();


    /**
     * 类声明周期
     * 加载，连接，初始化，使用，卸载
     *
     * JVM使用ClassLoader将类加载进内存
     *
     * ServiceLoader 简单的服务提供者加载设施
     *
     * 通过在资源目录 META-INF/services 中放置提供者配置文件，用来标识服务提供者。文件名称是服务类型的完全规定二进制名称
     *
     * 文件包含一个具体提供者类的完全限定二进制名称列表，每行一个
     *
     * 以延迟方式查找和实例化提供者，根据需要进行，服务加载器维护到目前为止已经加载的提供者缓存，调用iterator返回迭代器
     * 按照实例化顺序生成缓存的所有元素，然后以延迟的方式查找和实例化所有剩余的提供者，依次将每个提供者添加进缓存
     *
     *
     *
     * 1.ServiceLoader和ClassLoader一样，能装载文件
     *
     * ServiceLoader装载的是一系列有某种共同特征的实现类，而ClassLoader是一个万能加载器
     * serviceLoader装载时需要特殊配置
     * ServiceLoader实现Iterator接口
     *
     */

    /**
     *
     * 根据定义接口定义，获取第一个实现类
     *
     * TODO 为什么是第一个？
     * @param service
     * @param <S>
     * @return
     */
    public synchronized static <S> S load(Class<S> service) {
        //获取已经实现的声明类信息
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst().orElseThrow(ServiceLoadException::new);
    }


    public static synchronized <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter).collect(Collectors.toList());
    }


    private static <S> S singletonFilter(S service) {
        /*
        * 获取RPC处理对象信息
        * */
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            //如果不存在，进行填充，如果存在，使用旧对象
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
