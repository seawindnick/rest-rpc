package com.java.study.netty.nameservice;

import com.java.study.netty.serialize.SerializeSupport;
import com.java.study.rpc.NameService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <Description>
 * 加载配置文件的实现类
 * @author hushiye
 * @since 2020-12-14 22:44
 */
@Slf4j
public class LocalFileNameService implements NameService {

    private static final Collection<String> schemes = Collections.singleton("file");

    private File file;

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    //文件路径信息

    /**
     * 和注册中心建立连接
     * 对于使用文件存储注册信息而言，建立连接就是拿到文件的引用即可
     * @param nameServiceUri
     */
    @Override
    public void connect(URI nameServiceUri) {
        if (schemes.contains(nameServiceUri.getScheme())) {
            file = new File(nameServiceUri);
        } else {
            throw new RuntimeException("Unsupported scheme!");
        }
    }

    //注册服务
    @Override
    public synchronized void registerService(String serviceName, URI uri) throws IOException {
        log.info("Register service: {}, uri: {}.", serviceName, uri);
        //RandomAccessFile 自由读取文件信息，可根据指针位置读取
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel();
             FileLock fileLock = fileChannel.lock()) {


            try {
                //获取文件长度
                int fileLength = (int) raf.length();
                //存储元数据信息
                Metadata metadata;
                byte[] bytes;
                if (fileLength > 0) {
                    bytes = new byte[(int) raf.length()];
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    while (buffer.hasRemaining()) {
                        fileChannel.read(buffer);
                    }
                    //将文件信息内容写入元数据，将文件中信息解析
                    metadata = SerializeSupport.parse(bytes);
                } else {
                    metadata = new Metadata();
                }

                // computeIfAbsent从MAP中获取信息，如果信息不存在，进行填充
                List<URI> uris = metadata.computeIfAbsent(serviceName, k -> new ArrayList<>());
                if (!uris.contains(uri)) {
                    uris.add(uri);
                }

                // dcom.java.study.api.HelloServicerpc://localhost:9999
                //将元数据进行序列化
                bytes = SerializeSupport.serialize(metadata);

                //写入文件信息 TODO 从文件中获取的数据，和通过解析后再次写入的数据不一致？ 会新注册服务信息，新注册之后，新老数据不一致
                fileChannel.truncate(bytes.length);
                fileChannel.position(0L);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);

            } finally {
                fileLock.close();
            }


        }


    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        Metadata metadata;
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = raf.getChannel();
            FileLock fileLock = fileChannel.lock()) {

            try{
                byte[] bytes = new byte[(int)raf.length()];
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()){
                    fileChannel.read(buffer);
                }
                metadata = bytes.length == 0 ? new Metadata() : SerializeSupport.parse(bytes);
            }finally {
                fileLock.release();
            }
        }

        List<URI> uris = metadata.get(serviceName);
        if(Objects.isNull(uris) || uris.isEmpty()){
            return null;
        }

        return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
    }
}
