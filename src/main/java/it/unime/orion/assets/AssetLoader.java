package it.unime.orion.assets;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Objects;

public final class AssetLoader {

    public Image loadImage(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");

        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new MissingAssetException("Resource not found: " + resourcePath);
        }
        return new Image(is);
    }
}