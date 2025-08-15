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
import java.util.Comparator;
import java.util.function.IntUnaryOperator;

public class Level {
    private final int ROWS;
    private final int COLUMNS;
    private final Cell[][] gameField;
    private final ArrayList<Enemy> enemies = new ArrayList<>();

    private String notification = null;

    private boolean gameOver = false;

    public Level(int ROWS, int COLUMNS, int levelNum, Creature hero) {
        this.ROWS = ROWS;
        this.COLUMNS = COLUMNS;
        this.gameField = generateGameField(levelNum, hero);
    }

    private static class Room {
        final int centerX;
        final int centerY;
        final int rowOffset;
        final int columnOffset;
        final int width;
        final int height;

        Room(int x, int y, int ro, int co, int w, int h) {
            centerX = x;
            centerY = y;
            rowOffset = ro;
            columnOffset = co;
            width = w;
            height = h;
        }
    }

    private static class Edge {
        final int a;
        final int b;
        final double dist;

        Edge(int a, int b, double d) {
            this.a = a;
            this.b = b;
            this.dist = d;
        }
    }

    private Cell[][] generateGameField(int levelNum, Creature hero) {
        Cell[][] gameField = new Cell[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                gameField[i][j] = new Cell(new Tile(false, TileType.DARK));
            }
        }

        int maxRoomHeight = ROWS / 4;
        int maxRoomWidth = COLUMNS / 4;

        ArrayList<Room> rooms = generateRooms(maxRoomHeight, maxRoomWidth);
        ArrayList<Edge> edges = generateEdges(rooms);

        // --- Алгоритм Краскала ---
        int[] parent = new int[rooms.size()];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        java.util.function.IntUnaryOperator find = new java.util.function.IntUnaryOperator() {
            @Override
            public int applyAsInt(int x) {
                return parent[x] == x ? x : (parent[x] = applyAsInt(parent[x]));
            }
        };

        ArrayList<Edge> mst = generateMST(edges, find, parent);

        // --- Рисуем коридоры по MST ---
        for (Edge e : mst) {
            Room r1 = rooms.get(e.a);
            Room r2 = rooms.get(e.b);
            carveCorridor(gameField, new int[]{r1.centerX, r1.centerY}, new int[]{r2.centerX, r2.centerY});
        }

        int roomWithHeroRow = GameGenerator.getRandomInt(0, 2);
        int roomWithHeroColumn = GameGenerator.getRandomInt(0, 2);

        int roomWithDoorRow = GameGenerator.getRandomInt(0, 2);
        int roomWithDoorColumn = GameGenerator.getRandomInt(0, 2);

        for (int idx = 0; idx < rooms.size(); idx++) {
            Room room = rooms.get(idx);
            for (int i = 0; i < room.height; i++) {
                for (int j = 0; j < room.width; j++) {
                    int y = i + room.rowOffset;
                    int x = j + room.columnOffset;

                    Cell existingCell = gameField[y][x];
                    Cell roomCell;

                    if (i == 0 || j == 0 || i + 1 == room.height || j + 1 == room.width) {
                        if (existingCell.getBase().getTileType() == TileType.CORRIDOR) {
                            continue;
                        } else {
                            roomCell = new Cell(new Tile(false, TileType.WALL));
                        }
                    } else if (idx == roomWithDoorRow * 3 + roomWithDoorColumn &&
                            i == room.height / 3 && j == room.width / 3) {
                        roomCell = new Cell(new Tile(true, TileType.DOOR));
                    } else {
                        roomCell = new Cell(new Tile(true, TileType.FLOOR));
                    }

                    gameField[y][x] = roomCell;
                }
            }

            if (roomWithHeroRow * 3 + roomWithHeroColumn == idx) {
                MovablePosition heroPos = new MovablePosition(room.centerX, room.centerY);
                gameField[heroPos.getY()][heroPos.getX()].setCreature(hero);
                hero.setPos(heroPos);
            }
        }

        addEnemiesToGameField(levelNum, roomWithHeroRow, roomWithHeroColumn, maxRoomWidth, maxRoomHeight, gameField);
        addItemsToGameField(levelNum, roomWithHeroRow, roomWithHeroColumn, roomWithDoorRow, roomWithDoorColumn, maxRoomHeight, maxRoomWidth, gameField);

        return gameField;
    }

    /**
     * Прорубает коридоры на игровом поле
     *
     * @param gameField игровое поле
     * @param start     начальные координаты
     * @param end       конечные координаты
     */
    private void carveCorridor(Cell[][] gameField, int[] start, int[] end) {
        int x1 = start[0], y1 = start[1], x2 = end[0], y2 = end[1];

        int x = x1;
        while (x != x2) {
            gameField[y1][x] = new Cell(new Tile(true, TileType.CORRIDOR));
            x += Integer.compare(x2, x);
        }

        int y = y1;
        while (y != y2) {
            gameField[y][x2] = new Cell(new Tile(true, TileType.CORRIDOR));
            y += Integer.compare(y2, y);
        }
    }


    private static ArrayList<Edge> generateMST(ArrayList<Edge> edges, IntUnaryOperator find, int[] parent) {
        ArrayList<Edge> mst = new ArrayList<>();
        for (Edge e : edges) {
            int pa = find.applyAsInt(e.a);
            int pb = find.applyAsInt(e.b);
            if (pa != pb) {
                parent[pa] = pb;
                mst.add(e);
            }
        }
        return mst;
    }

    private ArrayList<Edge> generateEdges(ArrayList<Room> rooms) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                double dist = Math.hypot(rooms.get(i).centerX - rooms.get(j).centerX,
                        rooms.get(i).centerY - rooms.get(j).centerY);
                edges.add(new Edge(i, j, dist));
            }
        }
        edges.sort(Comparator.comparingDouble(e -> e.dist));
        return edges;
    }

    private ArrayList<Room> generateRooms(int maxRoomHeight, int maxRoomWidth) {
        ArrayList<Room> rooms = new ArrayList<>();
        for (int h = 0; h < 3; h++) {
            for (int w = 0; w < 3; w++) {
                int roomHeight = GameGenerator.getRandomInt((int) (maxRoomHeight / 1.5), maxRoomHeight);
                int roomWidth = GameGenerator.getRandomInt((int) (maxRoomWidth / 1.5), maxRoomWidth);
                int rowOffset = h * ROWS / 3 + maxRoomHeight / 6;
                int columnOffset = w * COLUMNS / 3 + maxRoomWidth / 6;

                int centerX = columnOffset + roomWidth / 2;
                int centerY = rowOffset + roomHeight / 2;
                rooms.add(new Room(centerX, centerY, rowOffset, columnOffset, roomWidth, roomHeight));
            }
        }
        return rooms;
    }

    private void addItemsToGameField(
            int levelNum,
            int roomWithHeroRow,
            int roomWithHeroColumn,
            int roomWithDoorRow,
            int roomWithDoorColumn,
            int maxRoomHeight,
            int maxRoomWidth,
            Cell[][] gameField
    ) {
        int numOfItems = GameGenerator.getRandomInt(3, 9);
        while (numOfItems > 0) {
            int roomWithItemRow = GameGenerator.getRandomInt(0, 2);
            int roomWithItemColumn = GameGenerator.getRandomInt(0, 2);
            if (roomWithItemRow == roomWithHeroRow && roomWithItemColumn == roomWithHeroColumn ||
                    roomWithItemRow == roomWithDoorRow && roomWithItemColumn == roomWithDoorColumn) {
                continue;
            }
            int row = maxRoomHeight / 3 + roomWithItemRow * ROWS / 3 + maxRoomHeight / 6 + GameGenerator.getRandomInt(-1, 1);
            int column = maxRoomWidth / 3 + roomWithItemColumn * COLUMNS / 3 + maxRoomWidth / 6 + GameGenerator.getRandomInt(-6, 6);
            if (gameField[row][column].getItem() != null) {
                continue;
            }
            gameField[row][column].setItem(GameGenerator.generateItem(levelNum));

            numOfItems--;
        }
    }

    private void addEnemiesToGameField(
            int levelNum,
            int roomWithHeroRow,
            int roomWithHeroColumn,
            int maxRoomWidth,
            int maxRoomHeight,
            Cell[][] gameField
    ) {
        int numOfEnemies;
        if (levelNum < 5) {
            numOfEnemies = GameGenerator.getRandomInt(2, 4);
        } else if (levelNum < 10) {
            numOfEnemies = GameGenerator.getRandomInt(4, 7);
        } else {
            numOfEnemies = GameGenerator.getRandomInt(7, 9);
        }
        while (numOfEnemies > 0) {
            int roomWithEnemyRow = GameGenerator.getRandomInt(0, 2);
            int roomWithEnemyColumn = GameGenerator.getRandomInt(0, 2);
            if (roomWithEnemyRow == roomWithHeroRow && roomWithEnemyColumn == roomWithHeroColumn) {
                continue;
            }
            Enemy enemy = GameGenerator.generateEnemy(
                    levelNum,
                    21,
                    new MovablePosition(
                            maxRoomWidth / 3 + roomWithEnemyColumn * COLUMNS / 3 + maxRoomWidth / 6,
                            maxRoomHeight / 3 + roomWithEnemyRow * ROWS / 3 + maxRoomHeight / 6
                    )
            );
            if (gameField[enemy.getPos().getY()][enemy.getPos().getX()].getCreature() != null) {
                continue;
            }
            enemies.add(enemy);
            gameField[enemy.getPos().getY()][enemy.getPos().getX()].setCreature(enemy);
            numOfEnemies--;
        }
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
                    if (hero.isDied()) {
                        gameOver = true;
                    }
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

    public String getNotification() {
        String res = notification;
        notification = null;
        return res;
    }

    public boolean cellWithHeroHasItem(MovablePosition pos) {
        return gameField[pos.getY()][pos.getX()].getItem() != null;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
