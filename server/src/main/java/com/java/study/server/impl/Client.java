package com.java.study.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.java.study.api.HelloService;
import io.netty.handler.codec.json.JsonObjectDecoder;
import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2/2/21 10:32
 */
public class Client {

    @Reference
    private static HelloService helloService;


    public static void main(String[] args) throws IOException {
        String mess = helloService.hello("世界");

        System.out.println("服务器：" + mess);


        String className = HelloService.class.getName();
        Method[] methods = HelloService.class.getMethods();

//        for (Method method : methods) {
//            String uri = "127.0.0.1";
//            int port = 8888;
//            String methodName = method.getName();
//
//            Socket s = new Socket(uri, port);
//            //构建IO
//            InputStream is = s.getInputStream();
//            OutputStream os = s.getOutputStream();
//
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//
//            RouteInvokeParam routeInvokeParam = new RouteInvokeParam(className, methodName, params);
//            //向服务器端发送一条消息
//            bw.write(JSONObject.toJSONString(routeInvokeParam));
//            bw.flush();
//            //读取服务器返回的消息
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            return br.readLine();
//
//        }


//    }

    }

    //
//    private static String remoteRequest(Object[] params) throws IOException {
//
//        String uri = "127.0.0.1";
//        int port = 8888;
//        String className = HelloService.class.getName();
//        String methodName = "hello";
//        Socket s = new Socket(uri, port);
//        //构建IO
//        InputStream is = s.getInputStream();
//        OutputStream os = s.getOutputStream();
//
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//
//        RouteInvokeParam routeInvokeParam = new RouteInvokeParam(className, methodName, params);
//        //向服务器端发送一条消息
//        bw.write(JSONObject.toJSONString(routeInvokeParam));
//        bw.flush();
//        //读取服务器返回的消息
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        return br.readLine();
//    }
//
    @Data
    @AllArgsConstructor
    public static class RouteInvokeParam {
        private String className;
        private String methodName;
        private Object[] params;
    }
}