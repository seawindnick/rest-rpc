package com.java.study.netty.serialize;

import com.java.study.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 10:18
 */
@Slf4j
public class SerializeSupport {

    /**
     * 用于存储序列化对象类型，序列化实现集合
     */
    private static Map<Class<?>, Serializer<?>> serializerMap = new HashMap<>();

    /**
     * 用来存储序列化实现类型，序列化对象类型集合
     */
    private static Map<Byte, Class<?>> typeMap = new HashMap<>();

    static {

        for (Serializer serializer : ServiceSupport.loadAll(Serializer.class)) {
            registerType(serializer.type(), serializer.getSerializeClass(), serializer);
            log.info("Found serializer, class: {}, type: {}.",
                    serializer.getSerializeClass().getCanonicalName(),
                    serializer.type());
        }
    }


    private static byte parseEntryType(byte[] buffer) {
        return buffer[0];
    }

    private static <E> void registerType(byte type, Class<E> serializeClass, Serializer<E> serializer) {
        serializerMap.put(serializeClass, serializer);
        typeMap.put(type, serializeClass);
    }


    /**
     * 反序列化
     *
     * @param buffer
     * @param <E>
     * @return
     */
    public static <E> E parse(byte[] buffer) {
        return parse(buffer, 0, buffer.length);
    }

    private static <E> E parse(byte[] buffer, int offset, int length) {
        //获取协议类型
        byte type = parseEntryType(buffer);
        //根据协议类型获取协议对应的实例信息
        Class<E> eClass = (Class<E>) typeMap.get(type);
        if (eClass == null) {
            throw new SerializeException(String.format("Unknown entry type: %d!", type));
        }
        return parse(buffer, offset + 1, length - 1, eClass);

    }

    private static <E> E parse(byte[] buffer, int offest, int length, Class<E> eClass) {
        Object entry = serializerMap.get(eClass).parse(buffer, offest, length);
        if (eClass.isAssignableFrom(entry.getClass())) {
            return (E) entry;
        }
        throw new SerializeException("Type mismatch!");
    }


    /**
     * 序列化
     *
     * @param entry
     * @param <E>
     * @return
     */
    public static <E> byte[] serialize(E entry) {
        Serializer<E> serializer = (Serializer<E>) serializerMap.get(entry.getClass());
        if (serializer == null) {
            throw new SerializeException(String.format("Unknown entry class type: %s", entry.getClass().toString()));
        }
        byte[] bytes = new byte[serializer.size(entry) + 1];
        //使用一个字节存储序列化类型数据
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }
}
