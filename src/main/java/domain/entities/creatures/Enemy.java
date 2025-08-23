package domain.entities.creatures;

import domain.entities.EntityGenerator;
import domain.cells.Cell;
import domain.cells.TileType;
import domain.entities.items.Item;
import domain.positions.MovablePosition;

public class Enemy extends Creature {
    private EnemyType enemyType;
    private Integer hostility;
    private Item rewardTreasure;

    public Enemy() {}

    public Enemy(
            EnemyType enemyType,
            TileType tileType,
            int hostility,
            int health,
            int agility,
            int strength,
            MovablePosition pos,
            Item rewardTreasure
    ) {
        super(health, agility, strength, pos, tileType);
        this.enemyType = enemyType;
        this.hostility = hostility;
        this.rewardTreasure = rewardTreasure;
    }

    public Enemy(EnemyType enemyType, TileType tileType, int hostility) {
        this(enemyType, tileType, hostility, 5, 5, 5, null, null);
    }

    public Enemy(Enemy enemy) {
        this(
                enemy.getEnemyType(),
                enemy.getTile().getTileType(),
                enemy.getHostility(),
                enemy.getHealth(),
                enemy.getAgility(),
                enemy.getStrength(),
                enemy.getPos(),
                enemy.getRewardTreasure()
        );
    }

    /**
     * Рассчитывает урон противника
     *
     * @param heroAgility ловкость героя
     * @return количество урона
     */
    public int hitHero(int heroAgility) {
        int agilityDiff = agility - heroAgility;
        int chanceToHit = 50 + agilityDiff * 5;
        chanceToHit = Math.max(5, Math.min(95, chanceToHit));
        if (EntityGenerator.getRandomInt(1, 100) <= chanceToHit) {
            return strength / EntityGenerator.getRandomInt(1, 4);
        } else {
            return 0;
        }
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Integer getHostility() {
        return hostility;
    }

    public Item getRewardTreasure() {
        return rewardTreasure;
    }

    public void setRewardTreasure(Item rewardTreasure) {
        this.rewardTreasure = rewardTreasure;
    }

    /**
     * Обрабатывает логику действия противника
     *
     * @param hero герой
     * @param gameField игровое поле
     * @param ROWS      количество строк поля
     * @param COLUMNS   количество столбцов поля
     * @return возвращает урон по герою, null если не бьёт
     */
    public Integer move(Hero hero, Cell[][] gameField, int ROWS, int COLUMNS) {
        int oldX = getPos().getX();
        int oldY = getPos().getY();
        Cell enemyCell = gameField[oldY][oldX];
        int distanceX = oldX - hero.getPos().getX();
        int distanceY = oldY - hero.getPos().getY();
        MovablePosition newPos = new MovablePosition(oldX, oldY);
        if (Math.abs(distanceX) < hostility + 2 && Math.abs(distanceY) < hostility + 2
                && hasNotObstacleBetweenEnemyAndHero(newPos, distanceX, distanceY, gameField)) {
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                if (distanceX > 0 && gameField[newPos.getY()][newPos.getX() - 1].getBase().isWalkable()) {
                    newPos.moveLeft();
                } else if (distanceX < 0 && gameField[newPos.getY()][newPos.getX() + 1].getBase().isWalkable()) {
                    newPos.moveRight();
                }
            } else {
                if (distanceY > 0 && gameField[newPos.getY() - 1][newPos.getX()].getBase().isWalkable()) {
                    newPos.moveUp();
                } else if (distanceY < 0 && gameField[newPos.getY() + 1][newPos.getX()].getBase().isWalkable()) {
                    newPos.moveDown();
                }
            }
        } else {
            walk(newPos, oldX, oldY, gameField, ROWS, COLUMNS);
        }
        Integer damage = null;
        Enemy enemy = (Enemy) enemyCell.getCreature();
        if (!stunState) {
            if (newPos.getX() == hero.getPos().getX() && newPos.getY() == hero.getPos().getY()) {
                damage = hitHero(hero.takeTotalAgility());
                if (enemyType == EnemyType.OGRE && !stunState) {
                    changeStunState();
                }
            } else if (gameField[newPos.getY()][newPos.getX()].getCreature() == null) {
                enemyCell.setCreature(null);
                enemy.setPos(newPos);
                gameField[newPos.getY()][newPos.getX()].setCreature(enemy);
            }
        } else {
            changeStunState();
        }
        return damage;
    }

    /**
     * Проверяет, есть ли препятствие между героем и противником
     *
     * @param enemyPos  позиция противника
     * @param distanceX дистанция до героя по OX
     * @param distanceY дистанция до героя по OY
     * @param gameField игровое поле
     * @return true, если отсутствуют препятствия
     */
    private boolean hasNotObstacleBetweenEnemyAndHero(
            MovablePosition enemyPos,
            int distanceX,
            int distanceY,
            Cell[][] gameField
    ) {
        int ex = enemyPos.getX();
        int ey = enemyPos.getY();
        int heroX = ex - distanceX;
        int heroY = ey - distanceY;

        int absDx = Math.abs(distanceX);
        int absDy = Math.abs(distanceY);

        // Проверяем вдоль ведущей оси
        if (absDx >= absDy) { // движемся по X
            int stepX = Integer.signum(heroX - ex);
            for (int s = 1; s < absDx; s++) {
                int cx = ex + s * stepX;
                if (cx < 0 || cx >= gameField[0].length) return false;
                if (!gameField[ey][cx].getBase().isWalkable()) return false;
            }
        } else { // движемся по Y
            int stepY = Integer.signum(heroY - ey);
            for (int s = 1; s < absDy; s++) {
                int cy = ey + s * stepY;
                if (cy < 0 || cy >= gameField.length) return false;
                if (!gameField[cy][ex].getBase().isWalkable()) return false;
            }
        }
        return true;
    }

    /**
     * Изменяет значения X и Y в объекте новой позиции
     *
     * @param newPos    новая позиция
     * @param oldX      старое положение по OX
     * @param oldY      старое положение по OY
     * @param gameField игровое поле
     * @param ROWS      количество строк поля
     * @param COLUMNS   количество столбцов поля
     */
    private void walk(MovablePosition newPos, int oldX, int oldY, Cell[][] gameField, int ROWS, int COLUMNS) {
        switch (enemyType) {
            case ZOMBIE -> {
                boolean fl = true;
                while (fl) {
                    int newX = oldX;
                    int newY = oldY;
                    switch (EntityGenerator.getRandomInt(0, 6)) {
                        case 3 -> newX += 1;
                        case 4 -> newX -= 1;
                        case 5 -> newY += 1;
                        case 6 -> newY -= 1;
                    }
                    if (newX >= 0 && newX < COLUMNS && newY >= 0 && newY < ROWS && gameField[newY][newX].getBase().isWalkable()) {
                        newPos.setX(newX);
                        newPos.setY(newY);
                        fl = false;
                    }
                }
            }
            case OGRE -> {
                boolean fl = true;
                while (fl) {
                    int newX = oldX;
                    int newY = oldY;
                    int midX = oldX;
                    int midY = oldY;
                    switch (EntityGenerator.getRandomInt(0, 10)) {
                        case 4 -> {
                            midX = oldX + 1;
                            newX = oldX + 2;
                        }
                        case 5 -> {
                            midX = oldX - 1;
                            newX = oldX - 2;
                        }
                        case 6 -> {
                            midY = oldY + 1;
                            newY = oldY + 2;
                        }
                        case 7 -> {
                            midY = oldY - 1;
                            newY = oldY - 2;
                        }
                    }
                    if (newX >= 0 && newX < COLUMNS && newY >= 0 && newY < ROWS && midX >= 0 && midX < COLUMNS && midY >= 0 && midY < ROWS) {
                        if (gameField[midY][midX].getBase().isWalkable() && gameField[newY][newX].getBase().isWalkable()) {

                            newPos.setX(newX);
                            newPos.setY(newY);
                            fl = false;
                        }
                    }
                }
            }
            case GHOST -> {
                boolean fl = true;
                while (fl) {
                    if (EntityGenerator.getRandomDouble() < 0.6) {
                        newPos.setX(oldX);
                        newPos.setY(oldY);
                        break; // сразу выходим
                    }
                    // Смещения от -3 до 3
                    int offsetX = EntityGenerator.getRandomInt(-3, 3);
                    int offsetY = EntityGenerator.getRandomInt(-3, 3);
                    // Пропускаем вариант "остаться на месте"
                    if (offsetX == 0 && offsetY == 0) continue;
                    int newX = oldX + offsetX;
                    int newY = oldY + offsetY;
                    // Проверка границ
                    if (newX < 0 || newX >= COLUMNS || newY < 0 || newY >= ROWS) continue;
                    // Проверка пути без стен
                    if (ghostPathIsClear(oldX, oldY, newX, newY, gameField)) {
                        newPos.setX(newX);
                        newPos.setY(newY);
                        fl = false;
                    }
                }
            }
            case SNAKE_MAGE -> {
                boolean fl = true;
                while (fl) {
                    int newX = oldX;
                    int newY = oldY;
                    switch (EntityGenerator.getRandomInt(0, 6)) {
                        case 3 -> {
                            newX += 1;
                            newY += 1;
                        }
                        case 4 -> {
                            newX -= 1;
                            newY += 1;
                        }
                        case 5 -> {
                            newX += 1;
                            newY -= 1;
                        }
                        case 6 -> {
                            newX -= 1;
                            newY -= 1;
                        }
                    }
                    if (newX < 0 || newX >= COLUMNS || newY < 0 || newY >= ROWS) continue;
                    if (gameField[oldY][newX].getBase().isWalkable() &&
                            gameField[newY][oldX].getBase().isWalkable() &&
                            gameField[newY][newX].getBase().isWalkable()) {
                        newPos.setX(newX);
                        newPos.setY(newY);
                        fl = false;
                    }
                }
            }
            case VAMPIRE -> {
                boolean fl = true;
                while (fl) {
                    int newX = oldX;
                    int newY = oldY;
                    switch (EntityGenerator.getRandomInt(0, 8)) {
                        case 5 -> newX += 1;
                        case 6 -> newX -= 1;
                        case 7 -> newY += 1;
                        case 8 -> newY -= 1;
                    }
                    if (newX >= 0 && newX < COLUMNS && newY >= 0 && newY < ROWS && gameField[newY][newX].getBase().isWalkable()) {
                        newPos.setX(newX);
                        newPos.setY(newY);
                        fl = false;
                    }
                }
            }
            default -> throw new IllegalArgumentException("Enemy type is unacceptable: " + enemyType);
        }
    }

    /**
     * Проверяет, может ли призрак телепортироваться из начальной позиции в конечную
     *
     * @param startX    первоначальное значение X
     * @param startY    первоначальное значение Y
     * @param endX      конечное значение X
     * @param endY      конечное значение Y
     * @param gameField игровое поле
     * @return true, если перемещение возможно
     */
    private boolean ghostPathIsClear(int startX, int startY, int endX, int endY, Cell[][] gameField) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        int x = startX;
        int y = startY;

        while (true) {
            if (!gameField[y][x].getBase().isWalkable()) return false;
            if (x == endX && y == endY) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return true;
    }

    public void setEnemyType(EnemyType enemyType) {
        this.enemyType = enemyType;
    }

    public void setHostility(Integer hostility) {
        this.hostility = hostility;
    }
}
