package com.las.service.qqbot.netty.adapter;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.las.common.Constant;
import com.las.config.AppConfigs;
import com.las.enums.MsgCallBackEnum;
import com.las.utils.mirai.MiRaiUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * @author dullwolf
 */
public class BotServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private static Logger logger = Logger.getLogger(BotServerHandler.class);

    /**
     * 处理请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        FullHttpResponse response;
        if (fullHttpRequest.method() == HttpMethod.GET) {
            String data = "GET method over";
            ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
            response = responseOK(HttpResponseStatus.OK, buf);

        } else if (fullHttpRequest.method() == HttpMethod.POST) {
            String uri = fullHttpRequest.uri();
            logger.info(uri);
            if (uri.equals(AppConfigs.qqBotServer)) {
                Map<String, Object> params = getPostParamsFromChannel(fullHttpRequest);
                String content = JSONObject.toJSONString(params);
                logger.info(content);
                assert params != null;
                String type = params.get("type").toString();
                logger.info("事件类型是：" + type);
                String className = MsgCallBackEnum.getClassNameByEvent(type);
                if (null != className && !Constant.NONE.equals(className)) {
                    //不为空，说明找到了对应的处理类
                    logger.debug(className);
                    if (StrUtil.isBlank(AppConfigs.botQQ) && StrUtil.isBlank(AppConfigs.keyAuth)) {
                        logger.error("botQQ暂未初始化，无法执行bot事件");
                    } else {
                        MiRaiUtil.getInstance().initSession();
                        try {
                            Class<?> aClass = Class.forName(className);
                            Object obj = aClass.newInstance();
                            // 用反射机制拿handleMsg方法
                            Method handleMsg = aClass.getMethod("handleMsg", Map.class);
                            // 代理执行 obj.handleMsg()
                            handleMsg.invoke(obj, params);
                            // 用反射机制拿exec方法
                            Method exec = aClass.getMethod("exec");
                            if(exec != null){
                                exec.invoke(obj);
                                System.gc();
                            }
                        } catch (Exception e) {
                            logger.error("出错ERROR：" + e.getMessage(), e);
                        }
                    }
                }
            }

            String data = "POST method over";
            ByteBuf content = copiedBuffer(data, CharsetUtil.UTF_8);
            response = responseOK(HttpResponseStatus.OK, content);
        } else {
            response = responseOK(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
        // 发送响应
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        if (fullHttpRequest.method() == HttpMethod.GET) {
            // 处理get请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> paramList = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
            return params;
        } else {
            return null;
        }

    }


    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params;
        if (fullHttpRequest.method() == HttpMethod.POST) {
            // 处理POST请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if (strContentType.contains("x-www-form-urlencoded")) {
                params = getFormParams(fullHttpRequest);
            } else if (strContentType.contains("application/json")) {
                params = getJSONParams(fullHttpRequest);
            } else {
                return null;
            }
            return params;
        } else {
            return null;
        }
    }


    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();

        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), fullHttpRequest);
        List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();

        for (InterfaceHttpData data : postData) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                params.put(attribute.getName(), attribute.getValue());
            }
        }

        return params;
    }


    private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<>();
        ByteBuf content = fullHttpRequest.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String strContent = new String(reqContent);
        JSONObject jsonParams = JSONObject.parseObject(strContent);
        Set<String> keySet = jsonParams.keySet();
        for (String key : keySet) {
            params.put(key, jsonParams.get(key));
        }

        return params;
    }

    private FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set("Content-Type", "text/plain;charset=UTF-8");
        response.headers().set("Content_Length", response.content().readableBytes());
        return response;
    }

}
