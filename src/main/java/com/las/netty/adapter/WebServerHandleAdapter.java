package com.las.netty.adapter;

import com.las.config.AppConfigs;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

public class WebServerHandleAdapter extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = Logger.getLogger(BotServerHandler.class);

    // 资源所在路径
    private static final String location;

    // 404文件页面地址
    private static final File NOT_FOUND;

    static {
        // 构建资源所在路径，此处参数可优化为使用配置文件传入
        location = AppConfigs.WEB_PATH;
        //location = "src/main/resources/static";
        // 构建404页面
        String path = location + "/page/404/404.html";
        NOT_FOUND = new File(path);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 获取URI
        String uri = request.uri();
        // 设置不支持favicon.ico文件
        if ("favicon.ico".equals(uri)) {
            return;
        }
        // 根据路径地址构建文件
        String path = location + uri;
        File html = new File(System.getProperty("user.dir"),path);
        logger.debug("web访问路径：" + html.getAbsolutePath());

        // 状态为1xx的话，继续请求
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        // 当文件不存在的时候，将资源指向NOT_FOUND
        if (!html.exists()) {
            html = NOT_FOUND;
        }

        RandomAccessFile file = new RandomAccessFile(html, "r");
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

        // 文件没有发现设置状态为404
        if (html == NOT_FOUND) {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
        }

        // 设置文件格式内容
        if (path.endsWith(".html")) {
            response.headers().set("Content-Type", "text/html; charset=UTF-8");
        } else if (path.endsWith(".js")) {
            response.headers().set("Content-Type", "application/x-javascript");
        } else if (path.endsWith(".css")) {
            response.headers().set("Content-Type", "text/css; charset=UTF-8");
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);

        if (keepAlive) {
            response.headers().set("Content-Length", file.length());
            response.headers().set("Connection", "keep-alive");
        }
        ctx.write(response);

        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }
        // 写入文件尾部
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
}
