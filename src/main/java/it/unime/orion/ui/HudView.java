package it.unime.orion.ui;

import it.unime.orion.assets.GameAssets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class HudView {

    private static final double HP_BAR_WIDTH = 240;
    private static final double HP_BAR_HEIGHT = 16;
    private static final double BOSS_BAR_WIDTH = 260;
    private static final double BOSS_BAR_HEIGHT = 16;
    private static final double HEART_SIZE = 30;
    private static final double HEART_SPACING = 36;

    private final Pane root = new Pane();
    private final Pane heartsPane = new Pane();
    private final List<ImageView> heartViews = new ArrayList<>();

    private final Rectangle hpBarBg = new Rectangle(HP_BAR_WIDTH, HP_BAR_HEIGHT);
    private final Rectangle hpBar = new Rectangle(HP_BAR_WIDTH, HP_BAR_HEIGHT);
    private final Rectangle bossBarBg = new Rectangle(BOSS_BAR_WIDTH, BOSS_BAR_HEIGHT);
    private final Rectangle bossBar = new Rectangle(BOSS_BAR_WIDTH, BOSS_BAR_HEIGHT);

    private final Text hpText = new Text();
    private final Text scoreText = new Text();
    private final Text bossText = new Text();

    private int renderedMaxLives = -1;

    public HudView() {
        root.setPickOnBounds(false);
        root.setId("hud-root");
        hpText.setId("hud-hp-text");
        scoreText.setId("hud-score-text");
        bossText.setId("hud-boss-text");
        heartsPane.setId("hud-hearts");

        hpBarBg.setFill(Color.color(0.35, 0.08, 0.08, 0.85));
        hpBar.setFill(Color.LIMEGREEN);
        bossBarBg.setFill(Color.color(0.22, 0.04, 0.04, 0.85));
        bossBar.setFill(Color.ORANGERED);

        hpBarBg.setLayoutX(20);
        hpBarBg.setLayoutY(20);

        hpBar.setLayoutX(20);
        hpBar.setLayoutY(20);

        bossBarBg.setLayoutX(620);
        bossBarBg.setLayoutY(20);

        bossBar.setLayoutX(620);
        bossBar.setLayoutY(20);

        hpText.setFill(Color.WHITE);
        hpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        hpText.setLayoutX(20);
        hpText.setLayoutY(58);

        heartsPane.setLayoutX(20);
        heartsPane.setLayoutY(72);

        scoreText.setFill(Color.GOLD);
        scoreText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(128);

        bossText.setFill(Color.SALMON);
        bossText.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        bossText.setLayoutX(620);
        bossText.setLayoutY(58);

        hideBossHp();
        root.getChildren().addAll(hpBarBg, hpBar, bossBarBg, bossBar, hpText, heartsPane, scoreText, bossText);
    }

    public Pane getRoot() {
        return root;
    }

    public void updateHp(int hp, int maxHp) {
        hpText.setText("HP: " + hp + " / " + maxHp);

        double ratio = maxHp == 0 ? 0 : hp / (double) maxHp;
        hpBar.setWidth(HP_BAR_WIDTH * Math.max(0, Math.min(1, ratio)));
    }

    public void setScore(int score) {
        scoreText.setText("Score: " + score);
    }

    public void setLives(int currentLives, int maxLives) {
        if (maxLives != renderedMaxLives) {
            rebuildHearts(maxLives);
        }

        for (int i = 0; i < heartViews.size(); i++) {
            ImageView heartView = heartViews.get(i);
            boolean active = i < currentLives;
            heartView.setVisible(active);
            heartView.setManaged(active);
        }
    }

    public void showBossHp(int hp, int maxHp, String phaseName) {
        bossText.setText("Boss HP: " + hp + " / " + maxHp + " | " + phaseName);
        double ratio = maxHp == 0 ? 0 : hp / (double) maxHp;
        bossBar.setWidth(BOSS_BAR_WIDTH * Math.max(0, Math.min(1, ratio)));
        bossText.setVisible(true);
        bossBar.setVisible(true);
        bossBarBg.setVisible(true);
    }

    public void hideBossHp() {
        bossText.setVisible(false);
        bossBar.setVisible(false);
        bossBarBg.setVisible(false);
    }

    private void rebuildHearts(int maxLives) {
        heartsPane.getChildren().clear();
        heartViews.clear();

        for (int i = 0; i < maxLives; i++) {
            ImageView heartView = GameAssets.createHeartView(HEART_SIZE);
            heartView.setLayoutX(i * HEART_SPACING);
            heartView.setLayoutY(0);
            heartViews.add(heartView);
        }

        heartsPane.getChildren().addAll(heartViews);
        renderedMaxLives = maxLives;
    }

    String getDisplayedHp() {
        return hpText.getText();
    }

    String getDisplayedScore() {
        return scoreText.getText();
    }

    String getDisplayedBossText() {
        return bossText.getText();
    }

    double getDisplayedHpBarWidth() {
        return hpBar.getWidth();
    }

    double getDisplayedBossBarWidth() {
        return bossBar.getWidth();
    }

    boolean isBossHpVisible() {
        return bossText.isVisible() && bossBar.isVisible() && bossBarBg.isVisible();
    }

    int getVisibleHeartCount() {
        int visible = 0;
        for (ImageView heartView : heartViews) {
            if (heartView.isVisible()) {
                visible++;
            }
        }
        return visible;
    }

    int getRenderedHeartCount() {
        return heartViews.size();
    }
}
