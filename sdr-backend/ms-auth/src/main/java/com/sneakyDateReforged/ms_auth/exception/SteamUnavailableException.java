package com.sneakyDateReforged.ms_auth.exception;

public class SteamUnavailableException extends RuntimeException {
    public SteamUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SteamUnavailableException(String message) {
        super(message);
    }
}
