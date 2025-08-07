package domain;

import domain.cells.Cell;
import domain.cells.Tile;
import domain.cells.TileType;
import domain.creatures.Creature;
import domain.creatures.Hero;
import domain.items.Item;
import domain.items.ItemSubtype;
import domain.items.ItemType;
import domain.positions.MovablePosition;

public class Level {
    private final int ROW_NUM;
    private final int COLUMN_NUM;
    private final Cell[][] gameField;

    private String notification = null;

    private boolean passed = false;

    public Level(int ROWS, int COLUMNS, int levelNum, Creature hero) {
        this.ROW_NUM = ROWS;
        this.COLUMN_NUM = COLUMNS;
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
        for (int i = 0; i < 6; i++) {
            gameField[12][i + 12] = new Cell(new Tile(false, TileType.WALL));
        }
        gameField[2][4].setItem(new Item(ItemType.TREASURE, ItemSubtype.GOLD_PIECE, 1));
        gameField[2][5].setItem(new Item(ItemType.TREASURE, ItemSubtype.GEM, 1));
        gameField[2][6].setItem(new Item(ItemType.TREASURE, ItemSubtype.MAGIC_AMULET, 1));

        return gameField;
    }

    public boolean swapEquippedWeaponForLyingWeapon(Hero hero) {
        boolean res = false;
        Cell cell = gameField[hero.getPos().getY()][hero.getPos().getX()];
        if (cell.getItem() != null) {
            Item item = cell.getItem();
            if (item.getType() == ItemType.WEAPON) {
                cell.setItem(hero.getEquippedWeapon());
                hero.setEquippedWeapon(item);
                notification = "+" + item.getStrengthBoost() + " " + item.getSubtype().getName() + " equipped";
                res = true;
            }
        }
        return res;
    }

    public boolean swapEquippedArmorForLyingArmor(Hero hero) {
        boolean res = false;
        Cell cell = gameField[hero.getPos().getY()][hero.getPos().getX()];
        if (cell.getItem() != null) {
            Item item = cell.getItem();
            if (item.getType() == ItemType.ARMOR) {
                cell.setItem(hero.getEquippedArmor());
                hero.setEquippedArmor(item);
                notification = "+" + item.getAgilityBoost() + " " + item.getSubtype().getName() + " equipped";
                res = true;
            }
        }
        return res;
    }

    public boolean heroMoveUp(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX(), pos.getY() - 1);
        if (res) {
            hero.getPos().moveUp();
            hero.decreasePotionDurations();
        }
        return res;
    }

    public boolean heroMoveDown(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX(), pos.getY() + 1);
        if (res) {
            hero.getPos().moveDown();
            hero.decreasePotionDurations();
        }
        return res;
    }

    public boolean heroMoveLeft(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX() - 1, pos.getY());
        if (res) {
            hero.getPos().moveLeft();
            hero.decreasePotionDurations();
        }
        return res;
    }

    public boolean heroMoveRight(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX() + 1, pos.getY());
        if (res) {
            hero.getPos().moveRight();
            hero.decreasePotionDurations();
        }
        return res;
    }

    private boolean heroMove(Hero hero, int newX, int newY) {
        MovablePosition pos = hero.getPos();
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
            Item item = newCell.getItem();
            if (item != null) {
                if (hero.putItemIntoInventory(newCell.getItem())) {
                    notification = "Picked up " + item.getCount() + " " + item.getSubtype().getName();
                    newCell.setItem(null);
                } else if (item.getType() == ItemType.ARMOR) {
                    Item heroArmor = hero.getEquippedArmor();
                    notification = "Do you want to change +" + heroArmor.getAgilityBoost() + " " +
                            heroArmor.getSubtype().getName() + " to +" + item.getAgilityBoost() + " " +
                            item.getSubtype().getName() + "?";
                } else if (item.getType() == ItemType.WEAPON) {
                    Item heroWeapon = hero.getEquippedWeapon();
                    notification = "Do you want to change +" + heroWeapon.getStrengthBoost() + " " +
                            heroWeapon.getSubtype().getName() + " to +" + item.getStrengthBoost() + " " +
                            item.getSubtype().getName() + "?";
                } else {
                    notification = "Inventory is full";
                }
                // TODO Логика подбора предмета
            }
            newCell.setCreature(hero);
            oldCell.setCreature(null);
            return true;
        }
        return false;
    }

    public boolean heroThrowAwayEquippedWeapon(Hero hero) {
        boolean res = false;
        Cell cellWithHero = gameField[hero.getPos().getY()][hero.getPos().getX()];
        Item item = hero.throwAwayEquippedWeapon();
        if (cellWithHero.getItem() == null) {
            if (item != null) {
                cellWithHero.setItem(item);
                res = true;
            }
        } else {
            hero.setEquippedWeapon(item);
        }
        return res;
    }

    public boolean heroThrowAwayEquippedArmor(Hero hero) {
        boolean res = false;
        Cell cellWithHero = gameField[hero.getPos().getY()][hero.getPos().getX()];
        Item item = hero.throwAwayEquippedArmor();
        if (cellWithHero.getItem() == null) {
            if (item != null) {
                cellWithHero.setItem(item);
                res = true;
            }
        } else {
            hero.setEquippedArmor(item);
        }
        return res;
    }

    public boolean heroThrowAwayItem(Hero hero, int num) {
        boolean res = false;
        Cell cellWithHero = gameField[hero.getPos().getY()][hero.getPos().getX()];
        Item item = hero.throwAwayItem(num);
        if (cellWithHero.getItem() == null) {
            if (item != null) {
                cellWithHero.setItem(item);
                res = true;
            }
        } else {
            hero.putItemIntoInventory(item);
        }
        return res;
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

    public String getNotification() {
        String res = notification;
        notification = null;
        return res;
    }

    public boolean cellWithHeroHasItem(MovablePosition pos) {
        return gameField[pos.getY()][pos.getX()].getItem() != null;
    }
}
