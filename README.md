## 使用教程

### 1、新建Maven项目引入jar:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Adsicmes</groupId>
        <artifactId>LAS-Bot</artifactId>
        <version>v1.6</version>
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
        Bot.run(App.class);
    }

}
```

- superQQ就是超管QQ
- botQQ就是机器人QQ
- keyAuth是Mirai插件设置的密钥（后面文档会讲）

还需要在resources目录下新建spring.xml文件，填写如下内容
```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
     http://www.springframework.org/schema/beans/spring-beans-4.0.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">


    <context:component-scan base-package="com.las,com.你的项目" />

    <context:annotation-config/>


</beans>
```

#### 启动报错问题
- 请检查你maven项目有没有创建com.xxx的包路径
- 请检查你项目中有没有配置resources目录，并且新增数据库环境ini文件
- 在resources目录下新建env.ini文件，填写如下内容

```
[dbmysql]
driver = com.mysql.cj.jdbc.Driver
jdbc = jdbc:mysql://47.106.142.49:3306/lasbot?useUnicode=true&characterEncoding=utf8
user = root
passwd = dw123456
# 这里最好是填写自己的数据账号密码
# 没有租过服务器的同学，直接用我的也OK，我这服务器测试用的
# 当然，前提是你不介意你QQ用户数据暴露在我数据库里的话，就用吧~
# 如果是自己创建的数据库，要注意先创建一个lasbot库先哦~ sql脚本在源码resources目录下有
```

### 2、 启动Mirai插件:

```
待更新
```


