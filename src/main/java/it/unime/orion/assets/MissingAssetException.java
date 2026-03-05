package it.unime.orion.assets;

public class MissingAssetException extends RuntimeException {
    public MissingAssetException(String message) {
        super(message);
    }
}