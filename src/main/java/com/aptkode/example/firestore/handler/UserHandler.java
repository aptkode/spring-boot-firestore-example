package com.aptkode.example.firestore.handler;

import com.aptkode.example.firestore.User;
import com.aptkode.example.firestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

@Component
public class UserHandler {

    @Autowired
    private UserRepository repository;

    @Nonnull
    public Mono<ServerResponse> getUsersByAge(ServerRequest request) {
        return request.queryParam("age")
                .map(Integer::valueOf)
                .map(age -> repository.findByAge(age))
                .map(users -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(users, User.class))
                .orElse(
                        ServerResponse
                                .badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .build()
                );
    }

    @Nonnull
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(User.class)
                .flatMap(user ->
                        ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(repository.save(user), User.class)
                );
    }
}
