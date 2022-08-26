package com.las.cq.boot;

import com.las.cq.robot.ApiHandler;
import com.las.cq.robot.CoolQFactory;
import com.las.cq.robot.EventHandler;
import com.las.cq.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Component
public class CQBean {
    @Autowired
    CQProperties cqProperties;

    @Autowired
    EventProperties eventProperties;

    @Autowired
    CoolQFactory coolQFactory;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler createWebSocketHandler(ApiHandler apiHandler, EventHandler eventHandler) {
        return new WebSocketHandler(eventProperties, coolQFactory, apiHandler, eventHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiHandler createApiHandler() {
        return new ApiHandler(cqProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventHandler createEventHandler() {
        return new EventHandler(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // ws 传输数据的时候，数据过大有时候会接收不到，所以在此处设置bufferSize
        container.setMaxTextMessageBufferSize(cqProperties.getMaxTextMessageBufferSize());
        container.setMaxBinaryMessageBufferSize(cqProperties.getMaxBinaryMessageBufferSize());
        container.setMaxSessionIdleTimeout(cqProperties.getMaxSessionIdleTimeout());
        return container;
    }
}
