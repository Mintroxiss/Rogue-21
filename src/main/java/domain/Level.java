package domain;

import domain.cells.Cell;
import domain.cells.Tile;
import domain.cells.TileType;
import domain.creatures.Creature;
import domain.creatures.Enemy;
import domain.creatures.EnemyType;
import domain.creatures.Hero;
import domain.items.Item;
import domain.items.ItemType;
import domain.positions.MovablePosition;

import java.util.ArrayList;

public class Level {
    private final int ROWS;
    private final int COLUMNS;
    private final Cell[][] gameField;
    private final ArrayList<Enemy> enemies = new ArrayList<>();

    private String notification = null;

    private boolean passed = false;

    public Level(int ROWS, int COLUMNS, int levelNum, Creature hero) {
        this.ROWS = ROWS;
        this.COLUMNS = COLUMNS;
        this.gameField = generateGameField(levelNum, hero);
    }

    /**
     * @param levelNum номер уровня
     * @param hero     герой
     * @return игровое поле
     */
    private Cell[][] generateGameField(int levelNum, Creature hero) { // TODO
        Cell[][] gameField = new Cell[ROWS][COLUMNS];
        hero.setPos(new MovablePosition(COLUMNS / 2, ROWS / 2));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
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
        gameField[20][20] = new Cell(new Tile(true, TileType.DOOR));

        gameField[4][11] = new Cell(new Tile(true, TileType.FLOOR), GameGenerator.generateItem(levelNum));
        gameField[4][12] = new Cell(new Tile(true, TileType.FLOOR), GameGenerator.generateItem(levelNum));
        gameField[4][13] = new Cell(new Tile(true, TileType.FLOOR), GameGenerator.generateItem(levelNum));

//        enemies.add(GameGenerator.generateEnemy(21, 21, new MovablePosition(COLUMNS - 1, 0)));
        enemies.add(GameGenerator.generateEnemy(21, 21, new MovablePosition(COLUMNS - 6, 0)));
//        gameField[0][COLUMNS - 1].setCreature(enemies.get(0));
        gameField[0][COLUMNS - 6].setCreature(enemies.getLast());
        for (int i = 0; i < 5; i++) {
            gameField[i][COLUMNS - 12] = new Cell(new Tile(false, TileType.WALL));
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
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Проверяет, находится ли герой у двери
     *
     * @param hero герой
     * @return true, если герой стоит на клетке с дверью
     */
    public boolean cellWithHeroHasDoor(Hero hero) {
        boolean res = false;
        Cell cell = gameField[hero.getPos().getY()][hero.getPos().getX()];
        if (cell.getBase().getTileType() == TileType.DOOR) {
            res = true;
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
            hero.decreasePotionDurations();
        }
        return res;
    }

    /**
     * Обрабатывает изменения на игровом поле при ходе героя
     *
     * @param hero герой
     * @return true, если герой выполнил действие
     */
    private boolean heroMove(Hero hero, int newX, int newY) {
        boolean res = false;

        MovablePosition pos = hero.getPos();
        if (newY < 0 || newY >= ROWS || newX < 0 || newX >= COLUMNS) {
            return false;
        }
        Cell newCell = gameField[newY][newX];
        Enemy enemyTarget = (Enemy) newCell.getCreature();
        boolean stunFl = false;
        if (enemyTarget != null) {
            int damage;
            if (enemyTarget.getEnemyType() == EnemyType.VAMPIRE && enemyTarget.getNumOfHitsReceived() == 0) {
                damage = 0;
                enemyTarget.increaseNumOfHitsReceived();
            } else {
                damage = hero.hitEnemy(enemyTarget.getAgility());
            }
            if (damage == 0) {
                notification = enemyTarget.getEnemyType().getName() + " dodged";
            } else {
                notification = enemyTarget.getEnemyType().getName() + " took " + damage + " damage";
                enemyTarget.increaseNumOfHitsReceived();
            }
            enemyTarget.setHealth(enemyTarget.getHealth() - damage);

            if (enemyTarget.isDied()) {
                newCell.setCreature(null);
                enemies.remove(enemyTarget);
                notification += ", the creature is died";
                if (newCell.getItem() == null) {
                    newCell.setItem(enemyTarget.getRewardTreasure());
                }
            }
            res = true;
        } else if (newCell.getBase().getWalkable()) {
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
            }
            if (!hero.getStunState()) {
                hero.setPos(new MovablePosition(newX, newY));
                newCell.setCreature(hero);
                oldCell.setCreature(null);
            } else {
                hero.changeStunState();
                stunFl = true;
            }

            res = true;
        }
        if (res) {
            for (Enemy enemy : enemies) {
                Integer damage = enemy.move(hero, gameField, ROWS, COLUMNS);
                if (damage != null) {
                    if (notification == null) {
                        notification = "";
                    } else {
                        notification += ", ";
                    }
                    if (damage == 0) {
                        notification += enemy.getEnemyType().getName() + " missed";
                    } else {
                        notification += enemy.getEnemyType().getName() + " dealt " + damage + " damage";
                    }
                    notification += hero.decreaseHealth(damage, enemy.getEnemyType(), stunFl);
                    hero.increaseNumOfHitsReceived();
                }
            }
        }
        return res;
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
