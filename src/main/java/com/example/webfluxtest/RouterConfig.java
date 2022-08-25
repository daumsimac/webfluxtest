package com.example.webfluxtest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@EnableWebFlux
public class RouterConfig {

    /**
     * 라우터 기본 방식
     */
    @Bean
    public RouterFunction<ServerResponse> routerExample(PostHandler postHandler){
        return RouterFunctions.route()
                .GET("/post/{id}", postHandler::getById)
                .POST("/post/", postHandler::create)
                .POST("/post/json", accept(MediaType.APPLICATION_JSON), postHandler::createFromJson)
                .build();
    }

    /**
     * nested router
     */
//    @Bean
//    public RouterFunction<ServerResponse> nestedRouter(PostHandler postHandler){
//        return RouterFunctions.route()
//                .path("/post/", builder -> builder
//                        .GET("/{id}", postHandler::getById)
//                        .POST("", postHandler::create)
//                        .POST("/json", accept(MediaType.APPLICATION_JSON), postHandler::createFromJson))
//        .build();
//    }
}
