package domain;

import domain.creatures.Creature;
import domain.creatures.Hero;
import domain.positions.MovablePosition;

public class Level {
    private final int ROW_NUM;
    private final int COLUMN_NUM;
    private Cell[][] gameField;

    private boolean passed = false;

    public Level(int rowNum, int columnNum, int levelNum, Creature hero) {
        this.ROW_NUM = rowNum;
        this.COLUMN_NUM = columnNum;
        this.gameField = generateGameField(levelNum, hero);
    }

    private Cell[][] generateGameField(int levelNum, Creature hero) { // TODO
        Cell[][] gameField = new Cell[ROW_NUM][COLUMN_NUM];
        for (int i = 0; i < ROW_NUM; i++) {
            for (int j = 0; j < COLUMN_NUM; j++) {
                Creature creature = null;
                if (hero.getPos().getY() == i && hero.getPos().getX() == j) {
                    creature = hero;
                }
                gameField[i][j] = new Cell(new Tile(true, TileType.FLOOR), creature);
            }
        }

        return gameField;
    }

    public boolean heroMoveUp(Creature hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(pos, pos.getX(), pos.getY() - 1);
        if (res) {
            hero.getPos().moveUp();
        }
        return res;
    }

    public boolean heroMoveDown(Creature hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(pos, pos.getX(), pos.getY() + 1);
        if (res) {
            hero.getPos().moveDown();
        }
        return res;
    }

    public boolean heroMoveLeft(Creature hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(pos, pos.getX() - 1, pos.getY());
        if (res) {
            hero.getPos().moveLeft();
        }
        return res;
    }

    public boolean heroMoveRight(Creature hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(pos, pos.getX() + 1, pos.getY());
        if (res) {
            hero.getPos().moveRight();
        }
        return res;
    }

    private boolean heroMove(MovablePosition pos, int newX, int newY) {
        if (newY < 0 || newY >= ROW_NUM || newX < 0 || newX >= COLUMN_NUM) {
            return false;
        }
        Cell newCell = gameField[newY][newX];
        if (newCell.getCreature() != null) {
            // TODO Логика атаки противника героем
            return false;
        }
        if (newCell.getBase().getWalkable()) {
            Cell oldCell = gameField[pos.getY()][pos.getX()];
            Hero hero = (Hero) oldCell.getCreature();
            if (newCell.getItem() != null) {
                // TODO Логика подбора предмета
            }
            newCell.setCreature(hero);
            oldCell.setCreature(null);
            return true;
        }
        return false;
    }

    /**
     * Возвращает игровое поле с типами плитки
     *
     * @return игровое поле
     */
    public TileType[][] getTileTypeGameField() {
        int rowNum = this.gameField.length;
        int columnNum = this.gameField[0].length;
        TileType[][] gameField = new TileType[rowNum][columnNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < columnNum; j++) {
                gameField[i][j] = this.gameField[i][j].getTopTileType();
            }
        }
        return gameField;
    }

    public boolean isPassed() {
        return passed;
    }
}
