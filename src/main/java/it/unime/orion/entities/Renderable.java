package it.unime.orion.entities;

import javafx.scene.layout.Pane;

public interface Renderable {
    void attachTo(Pane parent);
    boolean detachFrom(Pane parent);
    void bringToFront();
}
