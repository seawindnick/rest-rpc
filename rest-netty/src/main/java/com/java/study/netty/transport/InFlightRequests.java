package com.java.study.netty.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:36
 */
public class InFlightRequests implements Closeable {
    private final static long TIMEOUT_SEC = 10L;
    private final Semaphore semaphore = new Semaphore(10);
    private final Map<Integer, ResponseFuture> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        this.scheduledFuture =scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC,TimeUnit.SECONDS);
    }


    public void put(ResponseFuture responseFuture) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(TIMEOUT_SEC,TimeUnit.SECONDS)){
            futureMap.put(responseFuture.getRequestId(),responseFuture);
        }else {
            throw new TimeoutException();
        }
    }

    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }


    private void removeTimeoutFutures(){
        futureMap.entrySet().removeIf(entry->{
            if(System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1000000000L){
                semaphore.release();
                return true;
            }else {
                return false;
            }
        });

    }

    public ResponseFuture remove(int requestId){
        ResponseFuture responseFuture = futureMap.get(requestId);
        if (Objects.nonNull(responseFuture)){
            semaphore.release();
        }
        return responseFuture;
    }



}
