package domain;

import domain.cells.Cell;
import domain.cells.Tile;
import domain.cells.TileType;
import domain.creatures.Creature;
import domain.creatures.Hero;
import domain.items.Item;
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

    /**
     * @param levelNum номер уровня
     * @param hero     герой
     * @return игровое поле
     */
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


        return gameField;
    }

    /**
     * Кладёт оружие с поля в инвентарь героя, предыдущее оружие выбрасывает на место подбираемого
     *
     * @param hero герой
     * @return получилось ли поменять оружие?
     */
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

    /**
     * Кладёт броню с поля в инвентарь героя, предыдущую броню выбрасывает на место подбираемой
     *
     * @param hero герой
     * @return получилось ли поменять броню?
     */
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

    /**
     * Смещает героя вверх
     *
     * @param hero герой
     * @return true, если герой сместился
     */
    public boolean heroMoveUp(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX(), pos.getY() - 1);
        if (res) {
            hero.getPos().moveUp();
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Смещает героя вниз
     *
     * @param hero герой
     * @return true, если герой сместился
     */
    public boolean heroMoveDown(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX(), pos.getY() + 1);
        if (res) {
            hero.getPos().moveDown();
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Смещает героя влево
     *
     * @param hero герой
     * @return true, если герой сместился
     */
    public boolean heroMoveLeft(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX() - 1, pos.getY());
        if (res) {
            hero.getPos().moveLeft();
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Смещает героя вправо
     *
     * @param hero герой
     * @return true, если герой сместился
     */
    public boolean heroMoveRight(Hero hero) {
        MovablePosition pos = hero.getPos();
        boolean res = heroMove(hero, pos.getX() + 1, pos.getY());
        if (res) {
            hero.getPos().moveRight();
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Смещает героя
     *
     * @param hero герой
     * @return true, если герой сместился
     */
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

    /**
     * Выбрасывает экипированное оружие
     *
     * @param hero герой
     * @return true, если получилось выкинуть
     */
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

    /**
     * Выбрасывает экипированную броню
     *
     * @param hero герой
     * @return true, если получилось выкинуть
     */
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

    /**
     * Выбрасывает предмет из инвентаря
     *
     * @param hero герой
     * @param num  номер предмета в инвентаре
     * @return true, если получилось выкинуть
     */
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
