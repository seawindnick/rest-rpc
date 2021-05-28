package com.java.study.netty.serialize;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 11:04
 */
public interface Serializer<T> {

    /**
     * 计算对象序列化后的长度，主要用于申请存放系列化数据的字节数组
     *
     * @param entry
     * @return
     */
    int size(T entry);

    /**
     * 序列化对象，将给定的对象系列化成字节数组
     *
     * @param entry  待序列化的对象
     * @param bytes  存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化之后的长度
     */
    void serialize(T entry, byte[] bytes, int offset, int length);


    /**
     * 反序列化对象
     *
     * @param bytes  存放序列化数据的字节数组
     * @param offset 数组的偏移量，从这个位置开始写入序列化数据
     * @param length 对象序列化之后的长度
     * @return 反序列化之后的对象
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 使用一个字节标识对象的类型，每种类型数据应该具有不同的类型值
     */
    byte type();

    /**
     * 返回序列化对象类型的Class对象
     */
    Class<T> getSerializeClass();

}
