package fr.kata.spring.demo.controller;

public enum ResponseEnum {
    BAD_REQUEST("File doesn't exist"), ALREADY_PROCESSED("File already processed"), OK("Success");

    private String message;

    ResponseEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
