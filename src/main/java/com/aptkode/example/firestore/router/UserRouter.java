package com.aptkode.example.firestore.router;

import com.aptkode.example.firestore.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class UserRouter {

    @Autowired
    private UserHandler userHandler;

    @Bean
    RouterFunction<ServerResponse> userRoute(){
        return RouterFunctions
                .route(RequestPredicates.GET("/users"), userHandler::getUsersByAge)
                .andRoute(RequestPredicates.POST("/user"), userHandler::save);
    }

}
