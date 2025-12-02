package br.com.java_api.controller;

import jakarta.validation.constraints.NotBlank;

public class TaskRequest {
    @NotBlank(message = "O texto da tarefa não pode estar em branco.")
    private String text;

    // Getters e Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}