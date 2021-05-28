package com.java.study.netty.serialize.impl;

import com.java.study.netty.nameservice.Metadata;
import com.java.study.netty.serialize.Serializer;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-14 23:04
 */

/**
 * Size of the map                     2 bytes
 *      Map entry:
 *          Key string:
 *              Length:                2 bytes
 *              Serialized key bytes:  variable length
 *          Value list
 *              List size:              2 bytes
 *              item(URI):
 *                  Length:             2 bytes
 *                  serialized uri:     variable length
 *              item(URI):
 *              ...
 *      Map entry:
 *      ...
 *
 * @author LiYue
 * Date: 2019/9/20
 */
public class MetadataSerializer implements Serializer<Metadata> {
    @Override
    public int size(Metadata entry) {
        return Short.BYTES  + entry.entrySet().stream().mapToInt(this::entrySize).sum();
    }

    @Override
    public void serialize(Metadata entry, byte[] bytes, int offset, int length) {

        ByteBuffer buffer = ByteBuffer.wrap(bytes,offset,length);
        // map 的长度，即 service 与 List<URI> 映射关系的长度
        buffer.putShort(toShortSafely(entry.size()));

        entry.forEach((k,v)->{
            byte[] keyBytes = k.getBytes(StandardCharsets.UTF_8);
            // 服务名称长度
            buffer.putShort(toShortSafely(keyBytes.length));
            //服务名称信息
            buffer.put(keyBytes);

            //提供服务的 URI数量
            buffer.putShort(toShortSafely(v.size()));
            for (URI uri : v) {
                //单个URI信息
               byte[] uriBytes = uri.toASCIIString().getBytes(StandardCharsets.UTF_8);
               //单个URI长度
               buffer.putShort(toShortSafely(uriBytes.length));
               //单个URI具体信息
               buffer.put(uriBytes);
            }
        });

    }

    private short toShortSafely(int size) {
        assert size < Short.MAX_VALUE;
        return (short)size;
    }

    @Override
    public Metadata parse(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes,offset,length);

        Metadata metadata = new Metadata();
        //获取服务类别长度
        int sizeOfMap = buffer.getShort();

        for (int i = 0; i < sizeOfMap; i++) {
            // 获取服务名称长度
            int keyLength = buffer.getShort();
            byte[] keyBytes = new byte[keyLength];
            buffer.get(keyBytes);
            //获取提供的服务名称信息
            String key = new String(keyBytes,StandardCharsets.UTF_8);

            //服务提供者URI集合长度
            int uriListSize = buffer.getShort();
            List<URI> uriList = new ArrayList<>(uriListSize);
            for (int j = 0; j < uriListSize; j++) {
                //单个URI长度
                int uriLength = buffer.getShort();
                byte[] uriBytes = new byte[uriLength];
                buffer.get(uriBytes);
                //单个URI信息
                URI uri = URI.create(new String(uriBytes,StandardCharsets.UTF_8));
                uriList.add(uri);
            }
            metadata.put(key,uriList);
        }


        return metadata;
    }

    @Override
    public byte type() {
        return Types.TYPE_METADATA;
    }

    @Override
    public Class<Metadata> getSerializeClass() {
        return Metadata.class;
    }




    private int entrySize(Map.Entry<String, List<URI>> e){
        return Short.BYTES+
                e.getKey().getBytes().length+
                Short.BYTES+
                e.getValue().stream().mapToInt(uri->{
                    return Short.BYTES+uri.toASCIIString().getBytes(StandardCharsets.UTF_8).length;
                }).sum();

    }


}
