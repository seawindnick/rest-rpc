package com.java.study.netty.client.stubs;

import lombok.Data;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 22:22
 */

@Data
public class RpcRequest {

    private final String interfaceName;
    private final String methodName;
    private final byte[] serializedArguments;

    public RpcRequest(String interfaceName, String methodName, byte[] serializedArguments) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.serializedArguments = serializedArguments;
    }


}
