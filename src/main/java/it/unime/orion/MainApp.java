package it.unime.orion;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.engine.GameLoop;
import it.unime.orion.game.GameController;
import it.unime.orion.input.InputState;
import it.unime.orion.input.KeyboardInputHandler;
import it.unime.orion.ui.GameOverlayView;
import it.unime.orion.ui.HudView;
import it.unime.orion.world.GameWorld;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private GameLoop loop;
    private GameController controller;

    @Override
    public void start(Stage stage) {
        double width = 900;
        double height = 600;

        GameWorld world = new GameWorld();
        world.getRoot().setStyle("-fx-background-color: transparent;");

        HudView hud = new HudView();
        GameOverlayView overlay = new GameOverlayView(width, height);
        var background = GameAssets.createBackgroundView(width, height);

        StackPane root = new StackPane(background, world.getRoot(), hud.getRoot(), overlay.getRoot());
        Scene scene = new Scene(root, width, height);

        InputState input = new InputState();
        KeyboardInputHandler inputHandler = new KeyboardInputHandler(input);
        inputHandler.attach(scene);

        controller = new GameController(width, height, world, hud, overlay, input);

        loop = new GameLoop(world);
        loop.setPreUpdate(controller::preUpdate);
        loop.setShouldAdvanceWorld(controller::shouldAdvanceWorld);
        loop.setPostUpdate(controller::postUpdate);

        stage.setOnCloseRequest(event -> {
            shutdownApplication();
            Platform.exit();
        });

        stage.setTitle("Nova Defender (ORION-OOP)");
        stage.setScene(scene);
        stage.show();
        root.requestFocus();

        loop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        shutdownApplication();
    }

    private void shutdownApplication() {
        if (controller != null) {
            controller.close();
        }
        if (loop != null) {
            loop.stop();
        }
    }
}
