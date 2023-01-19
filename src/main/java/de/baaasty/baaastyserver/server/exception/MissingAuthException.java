package de.baaasty.baaastyserver.server.exception;

public class MissingAuthException extends Exception {
    public MissingAuthException(String message) {
        super(message);
    }
}
