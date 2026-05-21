package it.unime.orion.errors;

public final class InvalidGameConfigurationException extends RuntimeException {

    public InvalidGameConfigurationException(String message) {
        super(message);
    }

    public InvalidGameConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
