package domain;

import domain.creatures.Hero;
import domain.positions.MovablePosition;

public class GameSession {
    public final int ROW_NUM = 20;
    public final int COLUMN_NUM = 80;
    private Integer levelNum = 1;

    private final Hero hero = new Hero(new MovablePosition(COLUMN_NUM / 2, ROW_NUM / 2), null);
    private Level level = new Level(ROW_NUM, COLUMN_NUM, levelNum, hero);

    private boolean gameFieldUpdating = false;

    public TileType[][] getGameField() {
        gameFieldUpdating = false;
        return level.getTileTypeGameField();
    }

    public Boolean isNotGameOver() {
        return true;
    }

    public void gameTick(Character command) {
        executeCommand(command);
    }

    private void executeCommand(Character command) {
        gameFieldUpdating = switch (command) {
            case 'w' -> level.heroMoveUp(hero);
            case 'd' -> level.heroMoveRight(hero);
            case 's' -> level.heroMoveDown(hero);
            case 'a' -> level.heroMoveLeft(hero);
            default -> false;
        };
    }

    public boolean isGameFieldUpdating() {
        return gameFieldUpdating;
    }

    private void nextLevel() {
        levelNum++;
        level = new Level(ROW_NUM, COLUMN_NUM, levelNum, hero);
    }
}
