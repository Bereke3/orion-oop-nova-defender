package it.unime.orion.world;

import it.unime.orion.entities.GameEntity;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameWorld {

    private final Pane root = new Pane();
    private final List<GameEntity> entities = new ArrayList<>();

    public Pane getRoot() {
        return root;
    }

    public List<GameEntity> getEntitiesView() {
        return Collections.unmodifiableList(entities);
    }

    public void addEntity(GameEntity entity) {
        Objects.requireNonNull(entity, "entity");
        entities.add(entity);
        root.getChildren().add(entity.getView());
    }

    public void removeEntity(GameEntity entity) {
        if (entity == null) return;
        entities.remove(entity);
        root.getChildren().remove(entity.getView());
    }
}