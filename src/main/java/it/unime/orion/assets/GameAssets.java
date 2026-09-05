package it.unime.orion.assets;

import it.unime.orion.entities.boss.BossPhase;
import it.unime.orion.powerups.PowerUpType;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;

import java.util.HashMap;
import java.util.Map;

public final class GameAssets {

    public static final double PLAYER_WIDTH = 84;
    public static final double PLAYER_HEIGHT = 112;

    public static final double SWARM_WIDTH = 72;
    public static final double SWARM_HEIGHT = 50;

    public static final double SHOOTER_WIDTH = 94;
    public static final double SHOOTER_HEIGHT = 66;

    public static final double HEAVY_WIDTH = 124;
    public static final double HEAVY_HEIGHT = 90;

    public static final double BOSS_WIDTH = 300;
    public static final double BOSS_HEIGHT = 240;

    public static final double POWER_UP_SIZE = 40;
    public static final double PLAYER_BULLET_WIDTH = 14;
    public static final double PLAYER_BULLET_HEIGHT = 42;

    private static final String ROOT = "/images/ProjectImage/";

    private static final String PLAYER_SHIP = ROOT + "player_ship.png";
    private static final String ENEMY_SWARM = ROOT + "enemy_swarm.png";
    private static final String ENEMY_SHOOTER = ROOT + "enemy_shooter.png";
    private static final String ENEMY_HEAVY = ROOT + "enemy_heavy.png";
    private static final String DEFAULT_BOSS_KEY = "boss_a";
    private static final String BULLET_PLAYER_BASIC = ROOT + "bullet_player_basic.png";
    private static final String BULLET_PLAYER_DOUBLE = ROOT + "bullet_player_double.png";
    private static final String BULLET_ENEMY_RED = ROOT + "bullet_enemy_red.png";
    private static final String BULLET_ENEMY_CRIMSON = ROOT + "bullet_enemy_crimson.png";
    private static final String BULLET_ENEMY_PINK = ROOT + "bullet_enemy_pink.png";
    private static final String POWERUP_HEAL = ROOT + "powerup_heal.png";
    private static final String POWERUP_SHIELD = ROOT + "powerup_shield.png";
    private static final String POWERUP_WEAPON_UPGRADE = ROOT + "powerup_weapon_upgrade.png";
    private static final String BACKGROUND_SPACE = ROOT + "background_space.png";
    private static final String HEART = ROOT + "heart.png";

    private static final AssetLoader LOADER = new AssetLoader();
    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final Map<String, Rectangle2D> VIEWPORT_CACHE = new HashMap<>();

    private GameAssets() {
    }

    public static ImageView createPlayerView() {
        return createSpriteView(PLAYER_SHIP, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public static ImageView createSwarmEnemyView() {
        return createSpriteView(ENEMY_SWARM, SWARM_WIDTH, SWARM_HEIGHT);
    }

    public static ImageView createShooterEnemyView() {
        return createSpriteView(ENEMY_SHOOTER, SHOOTER_WIDTH, SHOOTER_HEIGHT);
    }

    public static ImageView createHeavyEnemyView() {
        return createSpriteView(ENEMY_HEAVY, HEAVY_WIDTH, HEAVY_HEIGHT);
    }

    public static ImageView createBossView(String bossAssetKey, BossPhase phase) {
        return createSpriteView(getBossAssetPath(bossAssetKey, phase), BOSS_WIDTH, BOSS_HEIGHT);
    }

    public static ImageView createBasicPlayerBulletView() {
        return createSpriteView(BULLET_PLAYER_BASIC, PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT);
    }

    public static ImageView createDoublePlayerBulletView() {
        return createSpriteView(BULLET_PLAYER_DOUBLE, PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT);
    }

    public static String getEnemyRedBulletAssetPath() {
        return BULLET_ENEMY_RED;
    }

    public static String getEnemyCrimsonBulletAssetPath() {
        return BULLET_ENEMY_CRIMSON;
    }

    public static String getEnemyPinkBulletAssetPath() {
        return BULLET_ENEMY_PINK;
    }

    public static ImageView createPowerUpView(PowerUpType type) {
        String path = switch (type) {
            case HEAL -> POWERUP_HEAL;
            case SHIELD -> POWERUP_SHIELD;
            case WEAPON_UPGRADE -> POWERUP_WEAPON_UPGRADE;
            case EXTRA_LIFE -> HEART;
        };
        return createSpriteView(path, POWER_UP_SIZE, POWER_UP_SIZE);
    }

    public static ImageView createBackgroundView(double width, double height) {
        return createSpriteView(BACKGROUND_SPACE, width, height);
    }

    public static ImageView createHeartView(double size) {
        return createSpriteView(HEART, size, size);
    }

    private static String getBossAssetPath(String bossAssetKey, BossPhase phase) {
        String resolvedKey = bossAssetKey == null || bossAssetKey.isBlank() ? DEFAULT_BOSS_KEY : bossAssetKey;
        String requestedPath = buildBossAssetPath(resolvedKey, phase);

        try {
            loadImage(requestedPath);
            return requestedPath;
        } catch (IllegalArgumentException missingRequestedAsset) {
            String fallbackPath = buildBossAssetPath(DEFAULT_BOSS_KEY, phase);
            loadImage(fallbackPath);
            return fallbackPath;
        }
    }

    private static String buildBossAssetPath(String bossAssetKey, BossPhase phase) {
        if (bossAssetKey.contains("_phase")) {
            return ROOT + bossAssetKey + ".png";
        }

        String phaseSuffix = switch (phase) {
            case PHASE_ONE -> "";
            case PHASE_TWO -> "_phase2";
            case PHASE_THREE -> "_phase3";
        };
        return ROOT + bossAssetKey + phaseSuffix + ".png";
    }

    public static ImageView createSpriteView(String path, double width, double height) {
        Image image = loadImage(path);
        ImageView view = new ImageView(image);
        if (shouldCropTransparentMargins(path)) {
            view.setViewport(resolveOpaqueViewport(path, image));
        }
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setMouseTransparent(true);
        return view;
    }

    private static Image loadImage(String path) {
        return CACHE.computeIfAbsent(path, LOADER::loadImage);
    }

    private static boolean shouldCropTransparentMargins(String path) {
        return BULLET_PLAYER_BASIC.equals(path)
                || BULLET_PLAYER_DOUBLE.equals(path)
                || BULLET_ENEMY_RED.equals(path)
                || BULLET_ENEMY_CRIMSON.equals(path)
                || BULLET_ENEMY_PINK.equals(path);
    }

    private static Rectangle2D resolveOpaqueViewport(String path, Image image) {
        return VIEWPORT_CACHE.computeIfAbsent(path, ignored -> computeOpaqueViewport(image));
    }

    private static Rectangle2D computeOpaqueViewport(Image image) {
        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return new Rectangle2D(0, 0, image.getWidth(), image.getHeight());
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (((reader.getArgb(x, y) >>> 24) & 0xFF) == 0) {
                    continue;
                }
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        if (maxX < minX || maxY < minY) {
            return new Rectangle2D(0, 0, image.getWidth(), image.getHeight());
        }

        return new Rectangle2D(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }
}
