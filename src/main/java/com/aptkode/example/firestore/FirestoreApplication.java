package com.aptkode.example.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class FirestoreApplication {

    private final static Logger logger = LoggerFactory.getLogger(FirestoreApplication.class);

    @Autowired
    private Firestore firestore;

    public static void main(String[] args) {
        SpringApplication.run(FirestoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.info("{} app initialized.", firestore.getOptions().getProjectId());
            writeAndReadDocument();
            User user = readDocument();
            logger.info("user name: {} age: {}", user.getName(), user.getAge());
            updateDocument();
            user = readDocument();
            logger.info("user name: {} age: {}", user.getName(), user.getAge());
            deleteDocument();
        };
    }

    private void writeAndReadDocument() throws ExecutionException, InterruptedException {
        // Add document with id "tom" using a custom User class to users collection
        User tom = new User("Tom", 18);

        // set asynchrnously create/update document reference users/tom
        // users is the collection and tom is the document id
        ApiFuture<WriteResult> apiFuture = this.firestore.document("users/tom").set(tom);

        // .get() blocks on response
        WriteResult writeResult = apiFuture.get();

        logger.info("Update time: {}", writeResult.getUpdateTime());
    }

    private User readDocument() throws ExecutionException, InterruptedException {
        // you could reference document by this.firestore.collection("users").document("tom") as well
        ApiFuture<DocumentSnapshot> apiFuture = this.firestore.document("users/tom").get();

        // .get() blocks on response
        DocumentSnapshot documentSnapshot = apiFuture.get();

        return documentSnapshot.toObject(User.class);
    }

    private void updateDocument() throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> apiFuture = this.firestore.document("users/tom")
                .set(new User("tom", 19));
        WriteResult writeResult = apiFuture.get();
        logger.info("Update time: {}", writeResult.getUpdateTime());
    }

    private void deleteDocument() throws ExecutionException, InterruptedException {
        // document deletion does not delete its sub collections
        // see https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
        ApiFuture<WriteResult> apiFuture = this.firestore.document("users/tom").delete();
        WriteResult writeResult = apiFuture.get();
        logger.info("Update time: {}", writeResult.getUpdateTime());
    }

}

