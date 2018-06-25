package com.quantimodo.tools.events;

public class TokenEvent {
    final public boolean shouldWait;

    public TokenEvent(boolean shouldWait) {
        this.shouldWait = shouldWait;
    }
}
