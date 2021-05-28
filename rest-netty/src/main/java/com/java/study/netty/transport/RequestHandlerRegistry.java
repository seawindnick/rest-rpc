
package com.java.study.netty.transport;

import com.java.study.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:29
 */
@Slf4j
public class RequestHandlerRegistry {

    private Map<Integer, RequestHandler> handlerHashMap = new HashMap<>();
    private static RequestHandlerRegistry instance = null;

    public static RequestHandlerRegistry getInstance(){
        if (Objects.isNull(instance)){
            instance = new RequestHandlerRegistry();
        }
        return instance;
    }

    private RequestHandlerRegistry(){
        //加载所有的请求处理器
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            //根据处理类的类型和实例缓存
            handlerHashMap.put(requestHandler.type(),requestHandler);
            log.info("Load request handler, type: {}, class: {}.", requestHandler.type(), requestHandler.getClass().getCanonicalName());

        }
    }

    public RequestHandler get(int type){
        return handlerHashMap.get(type);
    }




}
