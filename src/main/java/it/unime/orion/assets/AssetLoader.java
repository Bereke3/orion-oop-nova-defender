package it.unime.orion.assets;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public final class AssetLoader {

    private static final Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());

    public Image loadImage(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");

        InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
        if (resourceStream == null) {
            LOGGER.severe("Missing resource requested by the game: " + resourcePath);
            throw new MissingAssetException("Resource not found: " + resourcePath);
        }
        try (InputStream input = resourceStream) {
            return new Image(new ByteArrayInputStream(input.readAllBytes()));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load resource: " + resourcePath, exception);
        }
    }
}
