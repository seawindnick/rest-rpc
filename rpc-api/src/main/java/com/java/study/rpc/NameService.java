package com.java.study.rpc;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 10:04
 */
public interface NameService {


    /**
     * 所支持的所有协议
     *
     * @return
     */
    Collection<String> supportedSchemes();


    /**
     * 连接注册中心
     *
     * @param nameServiceUri
     */
    void connect(URI nameServiceUri);


    /**
     * 注册服务
     *
     * @param serviceName
     * @param uri
     */
    void registerService(String serviceName, URI uri) throws IOException;


    /**
     * 查询服务地址
     *
     * @param serviceName
     * @return
     */
    URI lookupService(String serviceName) throws IOException;
}
