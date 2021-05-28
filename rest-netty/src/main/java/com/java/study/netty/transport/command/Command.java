package com.java.study.netty.transport.command;

import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 12:52
 */
@Data
public class Command {

    /**
     * 请求命令头
     */
    protected Header header;

    /**
     * 要传输的数据
     */
    private byte[] payload;

    public Command(Header header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    @Data
    public static class Header {
        /**
         * 标识请求ID，用于请求和响应配对。双工方式异步收发数据
         */
        private int requestId;
        /**
         * 命令版本号
         * 为了向下兼容
         * 接收方在接收命令之后需要检查版本号，如果接收方可以处理这个版本的命令就正常处理
         * 否则拒绝请求
         * 代表命令版本号或者协议版本号，不等于程序版本号
         */
        private int version;

        /**
         * 命令类型，为了让接收方识别收到的是什么命令，方便路由到处理类中
         */
        private int type;

        public Header(int requestId, int version, int type) {
            this.requestId = requestId;
            this.version = version;
            this.type = type;
        }

        public int length() {
            return Integer.BYTES + Integer.BYTES + Integer.BYTES;
        }
    }

    @Data
    public static class ResponseHeader extends Header {
        /**
         * 表示响应状态 0 是成功
         * 其他类型表示各种错误
         */
        private int code;

        /**
         * 错误信息
         */
        private String error;

        public ResponseHeader(int type, int version, int requestId, Throwable throwable) {
            this(type, version, requestId, Code.UNKNOWN_ERROR.code, throwable.getMessage());
        }

        public ResponseHeader(int type, int version, int requestId) {
            this(type, version, requestId, Code.SUCCESS.code, null);
        }

        public ResponseHeader(int type, int version, int requestId, int code, String error) {
            super(type, version, requestId);
            this.code = code;
            this.error = error;
        }

        @Override
        public int length() {
            return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                    Integer.BYTES +
                    (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
        }
    }
}

