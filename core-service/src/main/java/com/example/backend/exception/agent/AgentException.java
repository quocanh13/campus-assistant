package com.example.backend.exception.agent;

public class AgentException extends RuntimeException {
    private final String code;
    private final String message;

    public AgentException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
