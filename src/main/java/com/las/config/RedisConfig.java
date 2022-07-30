package com.las.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySource(value = "classpath:redis.properties")
public class RedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private String port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private String database;

    @Value("${redis.maxTotal}")
    private String maxTotal;

    @Value("${redis.maxIdle}")
    private String maxIdle;

    @Value("${redis.maxWaitMillis}")
    private String maxWaitMillis;


    @Bean("poolConfig")
    public JedisPoolConfig getPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(maxTotal));
        config.setMaxIdle(Integer.parseInt(maxIdle));
        config.setMaxWaitMillis(Long.parseLong(maxWaitMillis));
        return config;
    }


    @Bean("redisConnectionFactory")
    public JedisConnectionFactory getConnectionFactory(@Qualifier("poolConfig") JedisPoolConfig config) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPassword(password);
        factory.setPort(Integer.parseInt(port));
        factory.setPoolConfig(config);
        factory.setDatabase(Integer.parseInt(database));
        return factory;
    }


//    @Bean("redisTemplate")
//    public RedisTemplate getRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory){
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new JdkSerializationRedisSerializer());
//
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
//
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }


}