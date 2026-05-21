package it.unime.orion.game;

public final class ScoreCounter {

    private int points;

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("points must be >= 0");
        }
        this.points += points;
    }

    public void reset() {
        points = 0;
    }
}
