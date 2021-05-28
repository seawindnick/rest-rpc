package com.java.study.server.impl;

import com.java.study.api.HelloService;
import com.java.study.rpc.NameService;
import com.java.study.rpc.RpcAccessPoint;
import com.java.study.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 17:32
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        //创建一个注册地址，正常RPC服务就是获取注册列表
        File file = new File(tmpDirFile, "rpc_name_service.data");

        HelloService helloService = new HelloServiceimpl();
        // 获取远程调用封装对象实体信息
        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
             Closeable ignored = rpcAccessPoint.startServer()) {
            //通过远程对象，
            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            assert nameService != null;
            log.info("向rpcAcessPoint注册{}服务", serviceName);
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class
            );
            log.info("服务名:{},向NameService注册", serviceName);
            nameService.registerService(serviceName, uri);
            log.info("开始提供服务,按任何键退出");
            System.in.read();
            log.info("Bye!");
        }


    }


}
