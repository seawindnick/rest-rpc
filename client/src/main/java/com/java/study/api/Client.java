package com.java.study.api;

import com.java.study.rpc.NameService;
import com.java.study.rpc.RpcAccessPoint;
import com.java.study.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:51
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "rpc_name_service.data");
        String name = "Master MQ";

        try (RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            URI uri = nameService.lookupService(serviceName);
            //使用RPC进行远程调用
            HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
            String response = helloService.hello(name);
            log.info("收到响应: {}.", response);
        }

    }

}
