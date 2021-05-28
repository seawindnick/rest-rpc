package com.java.study.netty.client;

import com.itranswarp.compiler.JavaStringCompiler;
import com.java.study.netty.transport.Transport;

import java.util.Map;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 23:23
 */
public class DynamicStubFactory implements StubFactory {

    /**
     * SerializeSupport.serialize(arg) 序列化使用的是String类型的序列化器
     */
    private final static String STUB_SOURCE_TEMPLATE =
            "package com.java.study.netty.client.stubs;\n" +
                    "import com.java.study.netty.serialize.SerializeSupport;\n" +
                    "\n" +
                    "public class %s extends AbstractStub implements %s {\n" +
                    "    @Override\n" +
                    "    public String %s(String arg) {\n" +
                    "        return SerializeSupport.parse(\n" +
                    "                invokeRemote(\n" +
                    "                        new RpcRequest(\n" +
                    "                                \"%s\",\n" +
                    "                                \"%s\",\n" +
                    "                                SerializeSupport.serialize(arg)\n" +
                    "                        )\n" +
                    "                )\n" +
                    "        );\n" +
                    "    }\n" +
                    "}";

    /**
     * 创建代理类信息
     * @param transport
     * @param serviceClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T createStub(Transport transport, Class<?> serviceClass) {

        //填充模版信息
        String stubSimpleName = serviceClass.getSimpleName() + "Stub";
        String classFullName = serviceClass.getName();
        String stubFullName = "com.java.study.netty.client.stubs." + stubSimpleName;
        String methodName = serviceClass.getMethods()[0].getName();//只适用于一个方法的接口

        String source = String.format(STUB_SOURCE_TEMPLATE, stubSimpleName, classFullName, methodName, classFullName, methodName);

        //编译源代码
        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
            //加载编译好的类
            Class<?> clazz = compiler.loadClass(stubFullName, results);

            //把transport赋值给桩
            ServiceStub subInstance = (ServiceStub) clazz.newInstance();
            subInstance.setTransport(transport);
            //返回桩信息
            return (T) subInstance;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }







    }
}
