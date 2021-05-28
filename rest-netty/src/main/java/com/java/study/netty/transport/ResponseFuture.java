package com.java.study.netty.transport;

import com.java.study.netty.transport.command.Command;

import java.util.concurrent.CompletableFuture;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:38
 */
public class ResponseFuture {
    private final int requestId;
    private final CompletableFuture<Command> future;
    private final long timestamp;

    public ResponseFuture(int requestId, CompletableFuture<Command> future) {
        this.requestId = requestId;
        this.future = future;
        this.timestamp = System.nanoTime();
    }

    public int getRequestId() {
        return requestId;
    }

    public CompletableFuture<Command> getFuture() {
        return future;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
