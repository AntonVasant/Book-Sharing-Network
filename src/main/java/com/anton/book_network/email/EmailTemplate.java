package com.anton.book_network.email;

public enum EmailTemplate {
    ACTIVATE_ACCOUNT("activate account");

    private final String name;
    EmailTemplate(String name){
        this.name = name;
    }
}
