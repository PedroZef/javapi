package br.com.java_api.controller;

public class Task {

    private final long id;
    private String text;

    public Task(long id, String text) {
        this.id = id;
        this.text = text;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    // Setter
    public void setText(String text) {
        this.text = text;
    }
}