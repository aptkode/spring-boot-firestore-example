package com.aptkode.example.firestore;

public class Address {
    public enum Type{
        BILLING, RESIDENCE
    }
    private String country;
    private String city;
    private String state;
    private String street;
    private Type type;

    public Address() {
        // required by firestore
    }

    public Address(String country, String city, String state, String street, Type type) {
        this.country = country;
        this.city = city;
        this.state = state;
        this.street = street;
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
