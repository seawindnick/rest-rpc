package com.java.study.netty.serialize.impl;

import com.java.study.netty.client.stubs.RpcRequest;
import com.java.study.netty.serialize.Serializer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-14 23:23
 */
public class RpcRequestSerializer implements Serializer<RpcRequest> {
    @Override
    public int size(RpcRequest request) {

        /**
         *  接口名
         *  方法名
         *  请求参数长度
         */
        return Integer.BYTES + request.getInterfaceName().getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + request.getMethodName().getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + request.getSerializedArguments().length;
    }

    @Override
    public void serialize(RpcRequest request, byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes,offset,length);
        byte[] tmpBytes = request.getInterfaceName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        tmpBytes = request.getMethodName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);


        tmpBytes = request.getSerializedArguments();
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);
    }

    @Override
    public RpcRequest parse(byte[] bytes, int offset, int length) {

        ByteBuffer buffer = ByteBuffer.wrap(bytes,offset,length);
        int len = buffer.getInt();
        byte[] tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String interfaceName = new String(tmpBytes,StandardCharsets.UTF_8);


        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String methodName = new String(tmpBytes,StandardCharsets.UTF_8);

        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        byte[] serializedArgs = tmpBytes;
        return new RpcRequest(interfaceName,methodName,serializedArgs);
    }

    @Override
    public byte type() {
        return Types.TYPE_RPC_REQUEST;
    }

    @Override
    public Class<RpcRequest> getSerializeClass() {
        return RpcRequest.class;
    }
}