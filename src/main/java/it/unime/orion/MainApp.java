package it.unime.orion;

import it.unime.orion.engine.GameLoop;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.input.InputState;
import it.unime.orion.input.KeyboardInputHandler;
import it.unime.orion.world.GameWorld;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Window size
        double width = 900;
        double height = 600;

        // Game world + HUD layers
        GameWorld world = new GameWorld();
        world.getRoot().setStyle("-fx-background-color: black;");

        Pane hudLayer = new Pane();
        hudLayer.setPickOnBounds(false);

        StackPane root = new StackPane(world.getRoot(), hudLayer);

        // Scene (IMPORTANT: use root that contains both layers)
        Scene scene = new Scene(root, width, height);

        // Input
        InputState input = new InputState();
        KeyboardInputHandler inputHandler = new KeyboardInputHandler(input);
        inputHandler.attach(scene);

        // Player view (temporary rectangle)
        Rectangle shipView = new Rectangle(60, 40);
        shipView.setFill(Color.LIME);

        // Player composition
        PlayerStats stats = new PlayerStats(100);

        // Movement bounds
        double shipW = 60;
        double shipH = 40;

        double minX = 0;
        double maxX = width - shipW;

        double minY = height * 0.70;
        double maxY = height - shipH;

        PlayerMovement movement = new PlayerMovement(
                250,
                minX, maxX,
                minY, maxY
        );

        PlayerShip player = new PlayerShip(shipView, 420, 520, stats, movement);
        world.addEntity(player);

        // HUD elements
        Rectangle hpBarBg = new Rectangle(200, 14);
        hpBarBg.setFill(Color.DARKRED);

        Rectangle hpBar = new Rectangle(200, 14);
        hpBar.setFill(Color.LIMEGREEN);

        Text hpText = new Text();
        hpText.setFill(Color.WHITE);

        hpBarBg.setLayoutX(20);
        hpBarBg.setLayoutY(20);

        hpBar.setLayoutX(20);
        hpBar.setLayoutY(20);

        hpText.setLayoutX(20);
        hpText.setLayoutY(50);

        hudLayer.getChildren().addAll(hpBarBg, hpBar, hpText);

        // Damage events
        EventBus<DamageEvent> damageBus = new EventBus<>();
        damageBus.subscribe(ev -> {
            if (ev.getTargetId().equals(player.getId())) {
                player.takeDamage(ev.getAmount());
                System.out.println("Player HP: " + player.getStats().getHp());
            }
        });

        // Game loop
        GameLoop loop = new GameLoop(world);
        final boolean[] wasDamagePressed = {false};

        loop.setPreUpdate(dt -> {
            double speed = movement.getSpeed();
            double vx = 0;
            double vy = 0;

            if (input.left)  vx -= speed;
            if (input.right) vx += speed;
            if (input.up)    vy -= speed * 0.4;
            if (input.down)  vy += speed * 0.4;

            player.setVelocity(vx, vy);

            // One-shot damage on D press
            boolean pressed = input.debugDamage;
            if (pressed && !wasDamagePressed[0]) {
                damageBus.publish(new DamageEvent(player.getId(), 10));
            }
            wasDamagePressed[0] = pressed;

            // HUD update
            int hp = player.getStats().getHp();
            int maxHp = player.getStats().getMaxHp();

            hpText.setText("HP: " + hp + " / " + maxHp);

            double ratio = (maxHp == 0) ? 0 : (hp / (double) maxHp);
            hpBar.setWidth(200 * Math.max(0, Math.min(1, ratio)));
        });

        stage.setTitle("Nova Defender (ORION-OOP) - PlayerShip Test");
        stage.setScene(scene);
        stage.show();

        loop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}