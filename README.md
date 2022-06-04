## 使用教程

### 1、新建Maven项目引入jar:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Adsicmes</groupId>
        <artifactId>LAS-Bot</artifactId>
        <version>v1.2</version>
    </dependency>
</dependencies>
```

若无法下载获取jar，可以先在pom.xml添加资源仓库，如下：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

引入成功后，写一个Main测试类
```java
@BotRun(superQQ = "1091569752", botQQ = "2547170055", keyAuth = "123456")
public class App {

    public static void main(String[] args) {
        LASBot.run(App.class);
    }

}
```

- superQQ就是超管QQ
- botQQ就是机器人QQ
- keyAuth是Mirai插件设置的密钥（后面文档会讲）

#### 启动报错问题
- 启动会报错的原因有可能没有配置数据库环境
- 在resources目录下新建env.ini文件，填写如下内容

```
[dbmysql]
driver = com.mysql.cj.jdbc.Driver
jdbc = jdbc:mysql://47.106.142.49:3306/lasbot?useUnicode=true&characterEncoding=utf8
user = root
passwd = dw123456
# 这里最好是填写自己的数据账号密码
# 没有租过服务器的同学，直接用我的也OK，我这服务器测试用的
# 当然，前提是你不介意你QQ用户数据暴露在我数据库里的话，就用吧
```

### 2、 启动Mirai插件:

```
待更新
```


