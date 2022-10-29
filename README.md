# 教程文档

- 【推荐】此项目使用github上某个大佬开发的框架 [spring-CQ](https://github.com/lz1998/Spring-CQ)

- 由于酷Q已停运，请使用 [go-cqhttp](https://github.com/Mrs4s/go-cqhttp) 等替代酷Q。

- 基于 SpringBoot、反向websocket 的 QQ 机器人框架

- 这个README主要讲了大概的使用方法，详细API和EVENT看下面两个链接

- 详细API文档:https://github.com/lz1998/Spring-CQ/blob/demo/API.md

- 详细Event文档：https://github.com/lz1998/Spring-CQ/blob/demo/Event.md

- 更多demo可以参考我个人使用的机器人项目[dwBot](https://gitee.com/dullwolf/dwBot)


## 导入maven依赖
```xml
        <repositories>
            <repository>
                <id>jitpack.io</id>
                <url>https://jitpack.io</url>
            </repository>
        </repositories>
    
    
        <dependencies>
            <dependency>
                <groupId>com.github.Adsicmes</groupId>
                <artifactId>LAS-Bot</artifactId>
                <version>v2.0.13</version>
            </dependency>
        </dependencies>
```


## 编写插件

1. 编写XXXPlugin，继承CQPlugin  
    ```java

   @Component
   public class DemoPlugin extends CQPlugin {
       /**
        * 收到私聊消息时会调用这个方法
        *
        * @param cq    机器人对象，用于调用API，例如发送私聊消息 sendPrivateMsg
        * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
        * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
        */
       @Override
       public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
           // 获取 发送者QQ 和 消息内容
           long userId = event.getUserId();
           String msg = event.getMessage();
   
           if (msg.equals("hi")) {
               // 调用API发送hello
               cq.sendPrivateMsg(userId, "hello", false);
   
               // 不执行下一个插件
               return MESSAGE_BLOCK;
           }
           // 继续执行下一个插件
           return MESSAGE_IGNORE;
       }
   
   
       /**
        * 收到群消息时会调用这个方法
        *
        * @param cq    机器人对象，用于调用API，例如发送群消息 sendGroupMsg
        * @param event 事件对象，用于获取消息内容、群号、发送者QQ等
        * @return 是否继续调用下一个插件，IGNORE表示继续，BLOCK表示不继续
        */
       @Override
       public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
           // 获取 消息内容 群号 发送者QQ
           String msg = event.getMessage();
           long groupId = event.getGroupId();
           long userId = event.getUserId();
   
           if (msg.equals("hello")) {
               // 回复内容为 at发送者 + hi
               String result = CQCode.at(userId) + "hi";
   
               // 调用API发送消息
               cq.sendGroupMsg(groupId, result, false);
   
               // 不执行下一个插件
               return MESSAGE_BLOCK;
           }
   
           // 继续执行下一个插件
           return MESSAGE_IGNORE;
       }
   }
    ```

2. 配置resources/application.yml
    ```yml
    server:
      port: 8081 # 下面的cqhttp都是8081端口，可以自己改
    
    spring:
      cq:
        # 在这里配置各个功能执行顺序
        # 如果前一个功能返回MESSAGE_BLOCK，下一个功能不会被执行
        # 如果前一个功能返回MESSAGE_IGNORE，会继续执行下一个功能
        plugin-list:
          - com.example.demo.plugin.DemoPlugin
          - com.example.demo.plugin.TestPlugin
          - com.example.demo.plugin.HelloPlugin
    ```



    
## 测试应用
1. 运行SpringApplication的main方法


## Windows运行go-http
1. 准备阶段
    - 步骤一：下载[go-http](https://github.com/Mrs4s/go-cqhttp/releases)
    - 步骤二：启动程序按引导配置
        3. 创建config.yml文件
            ```ini
            # go-cqhttp 默认配置文件
            
            account: # 账号相关
              uin: 2547170055 # QQ账号
              password: '' # 密码为空时使用扫码登录
              encrypt: false  # 是否开启密码加密
              status: 0      # 在线状态 请参考 https://docs.go-cqhttp.org/guide/config.html#在线状态
              relogin: # 重连设置
                delay: 3   # 首次重连延迟, 单位秒
                interval: 3   # 重连间隔
                max-times: 0  # 最大重连次数, 0为无限制
            
              # 是否使用服务器下发的新地址进行重连
              # 注意, 此设置可能导致在海外服务器上连接情况更差
              use-sso-address: true
              # 是否允许发送临时会话消息
              allow-temp-session: false
            
            heartbeat:
              # 心跳频率, 单位秒
              # -1 为关闭心跳
              interval: 5
            
            message:
              # 上报数据类型
              # 可选: string,array
              post-format: string
              # 是否忽略无效的CQ码, 如果为假将原样发送
              ignore-invalid-cqcode: false
              # 是否强制分片发送消息
              # 分片发送将会带来更快的速度
              # 但是兼容性会有些问题
              force-fragment: false
              # 是否将url分片发送
              fix-url: false
              # 下载图片等请求网络代理
              proxy-rewrite: ''
              # 是否上报自身消息
              report-self-message: false
              # 移除服务端的Reply附带的At
              remove-reply-at: false
              # 为Reply附加更多信息
              extra-reply-data: false
              # 跳过 Mime 扫描, 忽略错误数据
              skip-mime-scan: false
            
            output:
              # 日志等级 trace,debug,info,warn,error
              log-level: warn
              # 日志时效 单位天. 超过这个时间之前的日志将会被自动删除. 设置为 0 表示永久保留.
              log-aging: 15
              # 是否在每次启动时强制创建全新的文件储存日志. 为 false 的情况下将会在上次启动时创建的日志文件续写
              log-force-new: true
              # 是否启用日志颜色
              log-colorful: true
              # 是否启用 DEBUG
              debug: false # 开启调试模式
            
            # 默认中间件锚点
            default-middlewares: &default
              # 访问密钥, 强烈推荐在公网的服务器设置
              access-token: ''
              # 事件过滤器文件目录
              filter: ''
              # API限速设置
              # 该设置为全局生效
              # 原 cqhttp 虽然启用了 rate_limit 后缀, 但是基本没插件适配
              # 目前该限速设置为令牌桶算法, 请参考:
              # https://baike.baidu.com/item/%E4%BB%A4%E7%89%8C%E6%A1%B6%E7%AE%97%E6%B3%95/6597000?fr=aladdin
              rate-limit:
                enabled: false # 是否启用限速
                frequency: 1  # 令牌回复频率, 单位秒
                bucket: 1     # 令牌桶大小
            
            database: # 数据库相关设置
              leveldb:
                # 是否启用内置leveldb数据库
                # 启用将会增加10-20MB的内存占用和一定的磁盘空间
                # 关闭将无法使用 撤回 回复 get_msg 等上下文相关功能
                enable: true
            
              # 媒体文件缓存， 删除此项则使用缓存文件(旧版行为)
              cache:
                image: data/image.db
                video: data/video.db
            
            # 连接服务列表
            servers:
              # 添加方式，同一连接方式可添加多个，具体配置说明请查看文档
              #- http: # http 通信
              #- ws:   # 正向 Websocket
              #- ws-reverse: # 反向 Websocket
              #- pprof: #性能分析服务器
              # 反向WS设置
              - ws-reverse:
                  # 反向WS Universal 地址
                  # 注意 设置了此项地址后下面两项将会被忽略
                  universal: ws://127.0.0.1:8081/ws/cq/
                  # 反向WS API 地址
                  api: ws://127.0.0.1:8081
                  # 反向WS Event 地址
                  event: /ws/cq/
                  # 重连间隔 单位毫秒
                  reconnect-interval: 3000
                  middlewares:
                    <<: *default # 引用默认中间件

            ```
2. 运行 go-cqhttp.bat 登录QQ账号 




