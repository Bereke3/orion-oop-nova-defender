package it.unime.orion.world;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.errors.EntityLifecycleException;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class GameWorld {

    private static final Logger LOGGER = Logger.getLogger(GameWorld.class.getName());

    private final Pane root = new Pane();
    private final List<GameEntity> entities = new ArrayList<>();

    public Parent getRoot() {
        return root;
    }

    public List<GameEntity> getEntitiesView() {
        return Collections.unmodifiableList(entities);
    }

    public void addEntity(GameEntity entity) {
        Objects.requireNonNull(entity, "entity");
        // Keep the world list and JavaFX node tree in sync at all times.
        if (entities.contains(entity)) {
            throw new EntityLifecycleException("Entity already added to world: " + entity.getId());
        }
        if (entity.isAttachedTo(root)) {
            throw new EntityLifecycleException("Entity view is already attached to the world: " + entity.getId());
        }
        entities.add(entity);
        entity.attachTo(root);
    }

    public boolean removeEntity(GameEntity entity) {
        if (entity == null) {
            return false;
        }

        boolean removedEntity = entities.remove(entity);
        boolean removedView = entity.detachFrom(root);

        if (!removedEntity || !removedView) {
            LOGGER.warning("Attempted to remove an entity that is not fully registered in the world: " + entity.getId());
        }

        return removedEntity || removedView;
    }

    public void clearEntities() {
        entities.clear();
        root.getChildren().clear();
    }
}
