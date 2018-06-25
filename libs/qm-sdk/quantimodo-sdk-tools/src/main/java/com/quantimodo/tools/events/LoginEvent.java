package com.quantimodo.tools.events;

public class LoginEvent {
    public final String token;

    public LoginEvent(String token) {
        this.token = token;
    }
}
