package com.java.study.netty.transport;

import com.java.study.netty.transport.command.Command;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-14 23:34
 */
public interface RequestHandler {

    /**
     * 处理请求
     * @param command
     * @return
     */
    Command handle(Command command);

    /**
     * 请求支持类型
     * @return
     */
    int type();
}
