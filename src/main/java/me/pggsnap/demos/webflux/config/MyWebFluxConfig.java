package me.pggsnap.demos.webflux.config;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author pggsnap
 * @date 2020/4/20
 */
@Configuration
public class MyWebFluxConfig {
    @Autowired
    private WebFluxConfigurer webFluxConfigurer;

    @Autowired
    private ResourceProperties resourceProperties;

    @Autowired
    private WebFluxProperties webFluxProperties;

    @Autowired
    private ListableBeanFactory beanFactory;

//    @Autowired
//    private ObjectProvider<HandlerMethodArgumentResolver> resolvers;
//
//    @Autowired
//    private ObjectProvider<WebFluxRegistrations> webFluxRegistrations;
//
//    @Autowired
//    private WebFluxConfigurationSupport webFluxConfigurationSupport;
//
//    @Autowired
//    private ApplicationContextInitializer applicationContextInitializer;
}
