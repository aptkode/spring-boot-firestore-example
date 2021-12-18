package com.aptkode.example.firestore;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SpringBootApplication
public class FirestoreApplication {

    private final static Logger logger = LoggerFactory.getLogger(FirestoreApplication.class);

    @Autowired
    private Firestore firestore;

    public static void main(String[] args) {
        SpringApplication.run(FirestoreApplication.class, args);
        WebClient.create("http://localhost:8080");
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
//            logger.info("{} app initialized.", firestore.getOptions().getProjectId());
//            writeAndReadDocument();
//            User user = readDocument();
//            logger.info("user name: {} age: {}", user.getName(), user.getAge());
//            updateDocument();
//            user = readDocument();
//            logger.info("user name: {} age: {}", user.getName(), user.getAge());
//            deleteDocument();
//            insertUsers();
//            logger.info("male users: {}", getUsersByGender(Gender.MALE));
//            insertUsersBatch();
//            getUsersByGenderAsync(Gender.MALE, users -> logger.info("male users: {}", users));
//            getUsersByInterests(Arrays.asList("ESports", "Swimming"),
//                    users -> logger.info("users who likes Swimming or ESports: {}", users));
//            getUsersByAges(Arrays.asList(18, 25),
//                    users -> logger.info("users who's age is 18 or 25: {}", users));
//            getUsersInAgeRange(17, 20,
//                    users -> logger.info("users who's age is between 17 and 20 inclusive: {}", users));
//            updateAddresses();
//            getUsersLiveInCity("San Francisco",
//                    users -> logger.info("users lives in San Francisco: {}", users));
        };
    }

    private void writeAndReadDocument() throws ExecutionException, InterruptedException {
        // Add document with id "tom" using a custom User class to users collection
        User tom = new User("Tom", 18, Gender.MALE, Arrays.asList("ESports", "Swimming"));

        // set asynchronously create/update document reference users/tom
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
                .set(new User("tom", 19, Gender.MALE, Arrays.asList("ESports", "Swimming")));
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

    private void insertUsers() throws ExecutionException, InterruptedException {
        List<ApiFuture<WriteResult>> futures = new ArrayList<>();
        CollectionReference users = this.firestore.collection("users");
        futures.add(users.document("tom").set(
                new User("tom", 18, Gender.MALE, Arrays.asList("ESports", "Swimming"))
        ));
        futures.add(users.document("stella").set(
                new User("stella", 25, Gender.MALE, Arrays.asList("Embroidery", "Cooking", "Swimming"))
        ));
        futures.add(users.document("john").set(
                new User("john", 28, Gender.MALE, Arrays.asList("Programming", "Cricket"))
        ));
        // blocking get
        List<WriteResult> writeResults = ApiFutures.allAsList(futures).get();
        writeResults.forEach(r -> logger.info("Updated time: {}", r.getUpdateTime()));
    }

    private void insertUsersBatch() throws ExecutionException, InterruptedException {
        WriteBatch batch = this.firestore.batch();
        CollectionReference users = this.firestore.collection("users");
        batch.set(users.document("tom"),
                new User("tom", 18, Gender.MALE, Arrays.asList("ESports", "Swimming")));
        batch.set(users.document("stella"),
                new User("stella", 25, Gender.MALE, Arrays.asList("Embroidery", "Cooking", "Swimming")));
        batch.set(users.document("john"),
                new User("john", 28, Gender.MALE, Arrays.asList("Programming", "Cricket")));
        ApiFuture<List<WriteResult>> commit = batch.commit();
        // blocking get
        commit.get().forEach(r -> logger.info("Updated time: {}", r.getUpdateTime()));
    }

    private List<User> getUsersByGender(Gender gender) throws ExecutionException, InterruptedException {
        CollectionReference users = this.firestore.collection("users");
        ApiFuture<QuerySnapshot> future = users.whereEqualTo("gender", gender.name()).get();
        // blocking get
        QuerySnapshot queryDocumentSnapshots = future.get();
        return queryDocumentSnapshots.getDocuments()
                .stream()
                .map(d -> d.toObject(User.class))
                .collect(Collectors.toList());
    }

    private void getUsersByGenderAsync(Gender gender, Consumer<List<User>> consumer) {
        CollectionReference users = this.firestore.collection("users");
        users.whereEqualTo("gender", gender.name()).addSnapshotListener((value, error) -> {
            if (value != null) {
                List<User> results = value.getDocuments()
                        .stream()
                        .map(d -> d.toObject(User.class))
                        .collect(Collectors.toList());
                consumer.accept(results);
            }
        });
    }

    private void getUsersByAges(List<Integer> ages, Consumer<List<User>> consumer) {
        CollectionReference users = this.firestore.collection("users");
        users.whereIn("age", ages).addSnapshotListener((value, error) -> {
            if (value != null) {
                List<User> results = value.getDocuments()
                        .stream()
                        .map(d -> d.toObject(User.class))
                        .collect(Collectors.toList());
                consumer.accept(results);
            }
        });
    }

    private void getUsersByInterests(List<String> interests, Consumer<List<User>> consumer) {
        CollectionReference users = this.firestore.collection("users");
        users.whereArrayContainsAny("interests", interests).addSnapshotListener((value, error) -> {
            if (value != null) {
                List<User> results = value.getDocuments()
                        .stream()
                        .map(d -> d.toObject(User.class))
                        .collect(Collectors.toList());
                consumer.accept(results);
            }
        });
    }

    private void getUsersInAgeRange(int minAge, int maxAge, Consumer<List<User>> consumer) {
        CollectionReference users = this.firestore.collection("users");
        users.whereGreaterThanOrEqualTo("age", minAge)
                .whereLessThanOrEqualTo("age", maxAge)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<User> results = value.getDocuments()
                                .stream()
                                .map(d -> d.toObject(User.class))
                                .collect(Collectors.toList());
                        consumer.accept(results);
                    }
                });
    }

    private void updateAddresses() throws ExecutionException, InterruptedException {
        CollectionReference users = this.firestore.collection("users");
        List<ApiFuture<WriteResult>> futures = new ArrayList<>();
        futures.add(users.document("tom")
                .collection("addresses").document(Address.Type.RESIDENCE.name())
                .set(new Address("USA", "Los Angeles", "CA", "York Street", Address.Type.RESIDENCE)));
        futures.add(users.document("john")
                .collection("addresses").document(Address.Type.RESIDENCE.name())
                .set(new Address("USA", "San Francisco", "CA", "San Street", Address.Type.RESIDENCE)));
        ApiFutures.allAsList(futures).get();
    }

    private void getUsersLiveInCity(String city, Consumer<List<User>> consumer) {
        Query users = this.firestore.collectionGroup("addresses")
                .whereEqualTo("city", city);
        users.addSnapshotListener((value, error) -> {
            if (value != null) {
                List<User> results = value.getDocuments()
                        .stream()
                        .map(d -> d.toObject(User.class))
                        .collect(Collectors.toList());
                consumer.accept(results);
            }
            if(error != null){
                logger.error("failed", error);
            }
        });
    }

}

