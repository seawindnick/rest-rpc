package com.java.study.netty.transport.command;

import java.util.HashMap;
import java.util.Map;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 23:08
 */
public enum Code {
    SUCCESS(0, "SUCCESS"),
    NO_PROVIDER(-2, "NO_PROVIDER"),
    UNKNOWN_ERROR(-1, "UNKNOWN_ERRORA");


    public final int code;

    public final String message;

    private static Map<Integer, Code> codes = new HashMap<>();

    static {
        for (Code value : Code.values()) {
            codes.put(value.code, value);
        }

    }

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Code valueOf(int code) {
        return codes.get(code);
    }

    public String getMessage(Object... args) {
        if (args.length < 1) {
            return message;
        }
        return String.format(message, args);
    }


}
