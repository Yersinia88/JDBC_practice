package org.example;

public class DatabaseException extends Exception{
    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public DatabaseException(String message) {
        super(message);
    }
}
