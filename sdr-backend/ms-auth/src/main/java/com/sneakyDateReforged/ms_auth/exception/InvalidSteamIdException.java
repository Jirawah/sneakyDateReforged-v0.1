package com.sneakyDateReforged.ms_auth.exception;

public class InvalidSteamIdException extends RuntimeException {
    public InvalidSteamIdException(String message) {
        super(message);
    }
}