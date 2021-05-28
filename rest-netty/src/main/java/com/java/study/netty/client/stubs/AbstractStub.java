package com.java.study.netty.client.stubs;

import com.java.study.netty.client.RequestIdSupport;
import com.java.study.netty.client.ServiceStub;
import com.java.study.netty.client.ServiceTypes;
import com.java.study.netty.serialize.SerializeSupport;
import com.java.study.netty.transport.command.Code;
import com.java.study.netty.transport.command.Command;
import com.java.study.netty.transport.Transport;

import java.util.concurrent.ExecutionException;

/**
 * <Description>
 * 桩
 *
 * @author hushiye
 * @since 2020-12-13 21:50
 */
public abstract class AbstractStub implements ServiceStub {
    protected Transport transport;

    /**
     * 请求代理实现
     * @param rpcRequest
     * @return
     */
    protected byte[] invokeRemote(RpcRequest rpcRequest) {
        //使用RPC协议
        Command.Header header = new Command.Header(RequestIdSupport.next(), 1, ServiceTypes.TYPE_RPC_REQUEST);
        byte[] payload = SerializeSupport.serialize(rpcRequest);

        Command requestCommand = new Command(header, payload);
        try {
            Command responseCommand = transport.send(requestCommand).get();
            Command.ResponseHeader responseHeader = (Command.ResponseHeader) responseCommand.getHeader();

            if (responseHeader.getCode() == Code.SUCCESS.code) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
