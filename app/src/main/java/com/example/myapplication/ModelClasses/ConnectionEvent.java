package com.example.myapplication.ModelClasses;

public class ConnectionEvent {
    private final boolean isConnected;

    public ConnectionEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
