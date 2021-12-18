package com.aptkode.example.firestore.repository;

import com.aptkode.example.firestore.User;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends FirestoreReactiveRepository<User> {

    Flux<User> findByAge(int age);

}
