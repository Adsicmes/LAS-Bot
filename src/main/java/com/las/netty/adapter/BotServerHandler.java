package com.las.netty.adapter;


import com.alibaba.fastjson.JSONObject;
import com.las.config.AppConfigs;
import com.las.enums.MsgCallBackEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.buffer.Unpooled.copiedBuffer;

/*
 * 自定义处理的handler
 */
public class BotServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private static Logger logger = Logger.getLogger(BotServerHandler.class);

    /*
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
            if(uri.equals(AppConfigs.QQ_BOT_SERVER)){
                Map<String, Object> params = getPostParamsFromChannel(fullHttpRequest);
                String content = JSONObject.toJSONString(params);
                logger.info(content);
                assert params != null;
                String type = params.get("type").toString();
                logger.info("事件类型是：" + type);
                String className = MsgCallBackEnum.getClassNameByEvent(type);
                if(null != className){
                    //不为空，说明找到了对应的处理类
                    logger.info(className);// 假设我的bot收到了一条好友消息
                    // com.las.strategy.handle.FriendMsgHandler
                    try {
                        Class<?> aClass = Class.forName(className);
                        Object obj = aClass.newInstance();
                        // 用反射机制拿handleMsg方法
                        Method handleMsg = aClass.getMethod("handleMsg", Map.class);
                        // 代理执行 obj.handleMsg()
                        handleMsg.invoke(obj, params);
                        // 用反射机制拿exec方法
                        Method exec = aClass.getMethod("exec");
                        exec.invoke(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
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

    /*
     * 获取GET方式传递的参数
     */
    private Map<String, Object> getGetParamsFromChannel(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<String, Object>();

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

    /*
     * 获取POST方式传递的参数
     */
    private Map<String, Object> getPostParamsFromChannel(FullHttpRequest fullHttpRequest) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fullHttpRequest.method() == HttpMethod.POST) {
            // 处理POST请求
            String strContentType = fullHttpRequest.headers().get("Content-Type").trim();
            if (strContentType.contains("x-www-form-urlencoded")) {
                params = getFormParams(fullHttpRequest);
            } else if (strContentType.contains("application/json")) {
                try {
                    params = getJSONParams(fullHttpRequest);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            } else {
                return null;
            }
            return params;
        } else {
            return null;
        }
    }

    /*
     * 解析from表单数据（Content-Type = x-www-form-urlencoded）
     */
    private Map<String, Object> getFormParams(FullHttpRequest fullHttpRequest) {
        Map<String, Object> params = new HashMap<String, Object>();

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

    /*
     * 解析json数据（Content-Type = application/json）
     */
    private Map<String, Object> getJSONParams(FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {
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
