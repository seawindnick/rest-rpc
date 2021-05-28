package com.java.study.netty.transport;


import com.java.study.netty.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 12:51
 */
public interface Transport {

    /**
     * 发送请求指令
     *
     * @param request 请求指令
     * @return
     */
    CompletableFuture<Command> send(Command request);
}
