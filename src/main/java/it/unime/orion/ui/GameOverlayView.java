package it.unime.orion.ui;

import it.unime.orion.game.GameState;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public final class GameOverlayView {

    private final Pane root = new Pane();
    private final Rectangle backdrop;
    private final Text titleText = new Text();
    private final Text detailText = new Text();
    private final Text hintText = new Text();

    public GameOverlayView(double width, double height) {
        backdrop = new Rectangle(width, height);
        backdrop.setFill(Color.rgb(0, 0, 0, 0.78));

        root.setMouseTransparent(true);
        root.setId("game-overlay");
        titleText.setId("overlay-title");
        detailText.setId("overlay-detail");
        hintText.setId("overlay-hint");

        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font(36));
        titleText.setWrappingWidth(width - 80);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setLayoutX(40);
        titleText.setLayoutY(220);

        detailText.setFill(Color.LIGHTGRAY);
        detailText.setFont(Font.font(20));
        detailText.setWrappingWidth(width - 80);
        detailText.setTextAlignment(TextAlignment.CENTER);
        detailText.setLayoutX(40);
        detailText.setLayoutY(285);

        hintText.setFill(Color.DEEPSKYBLUE);
        hintText.setFont(Font.font(18));
        hintText.setWrappingWidth(width - 80);
        hintText.setTextAlignment(TextAlignment.CENTER);
        hintText.setLayoutX(40);
        hintText.setLayoutY(340);

        root.getChildren().addAll(backdrop, titleText, detailText, hintText);
        root.setVisible(true);
    }

    public Parent getRoot() {
        return root;
    }

    public void update(GameState state, int score) {
        if (!state.showsOverlay()) {
            root.setVisible(false);
            return;
        }

        root.setVisible(true);

        switch (state) {
            case START_SCREEN -> {
                titleText.setText("Nova Defender");
                detailText.setText("Press ENTER to begin the full campaign.");
                hintText.setText("Controls: arrows move | SPACE fire | P pause | R restart");
            }
            case PAUSED -> {
                titleText.setText("Paused");
                detailText.setText("Current score: " + score);
                hintText.setText("Press P to resume the simulation.");
            }
            case GAME_OVER -> {
                titleText.setText("Game Over");
                detailText.setText("Final score: " + score);
                hintText.setText("Press ENTER or R to restart the battle.");
            }
            case VICTORY -> {
                titleText.setText("Victory");
                detailText.setText("Final score: " + score);
                hintText.setText("Press ENTER or R to play another run.");
            }
            case RUNNING -> throw new IllegalStateException("Running state should not show overlay");
        }
    }

    String getDisplayedTitle() {
        return titleText.getText();
    }

    String getDisplayedDetail() {
        return detailText.getText();
    }

    String getDisplayedHint() {
        return hintText.getText();
    }
}
