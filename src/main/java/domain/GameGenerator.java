package domain;

import domain.cells.TileType;
import domain.creatures.Enemy;
import domain.creatures.EnemyType;
import domain.items.Item;
import domain.items.ItemSubtype;
import domain.items.ItemType;
import domain.positions.MovablePosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class GameGenerator {
    private static final ArrayList<Item> armors = new ArrayList<>(Arrays.asList(
            new Item(3, ItemType.ARMOR, ItemSubtype.PLATE_MAIL),
            new Item(4, ItemType.ARMOR, ItemSubtype.BANDED_MAIL),
            new Item(4, ItemType.ARMOR, ItemSubtype.SPLINT_MAIL),
            new Item(5, ItemType.ARMOR, ItemSubtype.CHAIN_MAIL),
            new Item(6, ItemType.ARMOR, ItemSubtype.SCALE_MAIL),
            new Item(7, ItemType.ARMOR, ItemSubtype.RING_MAIL),
            new Item(8, ItemType.ARMOR, ItemSubtype.STUDDED_LEATHER),
            new Item(10, ItemType.ARMOR, ItemSubtype.LEATHER_ARMOR)
    ));

    private static final ArrayList<Item> weapons = new ArrayList<>(Arrays.asList(
            new Item(1, ItemType.WEAPON, ItemSubtype.DAGGER),
            new Item(1, ItemType.WEAPON, ItemSubtype.MACE),
            new Item(1, ItemType.WEAPON, ItemSubtype.LONG_SWORD),
            new Item(1, ItemType.WEAPON, ItemSubtype.TWO_HANDED_SWORD)
    ));

    private static final ArrayList<Item> scrolls = new ArrayList<>(Arrays.asList(
            new Item(1, ItemType.SCROLL, ItemSubtype.SCROLL_OF_STRENGTH),
            new Item(1, ItemType.SCROLL, ItemSubtype.SCROLL_OF_VITALITY),
            new Item(1, ItemType.SCROLL, ItemSubtype.SCROLL_OF_DEXTERITY)
    ));

    private static final ArrayList<Item> foods = new ArrayList<>(Arrays.asList(
            new Item(2, ItemType.FOOD, ItemSubtype.RAISIN_BREAD),
            new Item(4, ItemType.FOOD, ItemSubtype.SLIME_MOLD),
            new Item(5, ItemType.FOOD, ItemSubtype.FRUIT),
            new Item(8, ItemType.FOOD, ItemSubtype.FOOD_RATION)
    ));

    private static final ArrayList<Item> potions = new ArrayList<>(Arrays.asList(
            new Item(3, ItemType.POTION, ItemSubtype.POTION_OF_STRENGTH),
            new Item(3, ItemType.POTION, ItemSubtype.POTION_OF_DEXTERITY),
            new Item(3, ItemType.POTION, ItemSubtype.POTION_OF_VITALITY)
    ));

    private static final ArrayList<Item> treasures = new ArrayList<>(Arrays.asList(
            new Item(ItemType.TREASURE, ItemSubtype.GOLD_PIECE),
            new Item(ItemType.TREASURE, ItemSubtype.GEM),
            new Item(ItemType.TREASURE, ItemSubtype.MAGIC_AMULET)
    ));

    /**
     * @return список первоначальных предметов в инвентаре
     */
    public static ArrayList<Item> getFirstInventoryItems() {
        return new ArrayList<>(Arrays.asList(
                new Item(foods.getFirst(), 3),
                new Item(potions.getFirst(), 1),
                new Item(scrolls.get(getRandomInt(0, scrolls.size() - 1)), 1)
        ));
    }

    private static final int mildHostility = 4;
    private static final int averageHostility = 6;
    private static final int highHostility = 10;

    private static final ArrayList<Enemy> enemies = new ArrayList<>(Arrays.asList(
            new Enemy(EnemyType.ZOMBIE, TileType.ZOMBIE, averageHostility),
            new Enemy(EnemyType.GHOST, TileType.GHOST, mildHostility),
            new Enemy(EnemyType.OGRE, TileType.OGRE, averageHostility),
            new Enemy(EnemyType.VAMPIRE, TileType.VAMPIRE, highHostility),
            new Enemy(EnemyType.SNAKE_MAGE, TileType.SNAKE_MAGE, highHostility)
    ));

    /**
     * Генерирует противника
     *
     * @param levelNum    номер уровня
     * @param maxLevelNum максимальный уровень
     * @param pos         позиция противника
     * @return противник
     */
    public static Enemy generateEnemy(int levelNum, int maxLevelNum, MovablePosition pos) {
        Enemy enemy;
        Item treasure;
        if (levelNum < maxLevelNum / 4) {
            enemy = new Enemy(enemies.getFirst());
            treasure = new Item(treasures.getFirst());
        } else if (levelNum < maxLevelNum / 2) {
            enemy = new Enemy(enemies.get(getRandomInt(0, 2))); //TODO
            treasure = new Item(treasures.get(1));
        } else {
            enemy = new Enemy(enemies.get(getRandomInt(0, enemies.size() - 1)));
            treasure = new Item(treasures.getLast());
        }
        int mildValue = 3, averageValue = 6, highValue = 9, highestValue = 12;
        int health, strength, agility;
        switch (enemy.getEnemyType()) {
            case ZOMBIE -> {
                health = getRandomInt(levelNum, levelNum + highValue);
                strength = getRandomInt(levelNum, levelNum + averageValue);
                agility = getRandomInt(levelNum, levelNum + mildValue);
            }
            case GHOST -> {
                health = getRandomInt(levelNum, levelNum + mildValue);
                strength = getRandomInt(levelNum, levelNum + mildValue);
                agility = getRandomInt(levelNum, levelNum + highValue);
            }
            case OGRE -> {
                health = getRandomInt(levelNum, levelNum + highestValue);
                strength = getRandomInt(levelNum, levelNum + highestValue);
                agility = getRandomInt(levelNum, levelNum + mildValue);
            }
            case VAMPIRE -> {
                health = getRandomInt(levelNum, levelNum + highValue);
                strength = getRandomInt(levelNum, levelNum + averageValue);
                agility = getRandomInt(levelNum, levelNum + highValue);
            }
            case SNAKE_MAGE -> {
                health = getRandomInt(levelNum, levelNum + mildValue);
                strength = getRandomInt(levelNum, levelNum + mildValue);
                agility = getRandomInt(levelNum, levelNum + highestValue);
            }
            default -> throw new IllegalArgumentException("No value added for EnemyType");
        }
        enemy.setRewardTreasure(treasure);
        enemy.setHealth(health);
        enemy.setAgility(agility);
        enemy.setStrength(strength);
        enemy.setPos(pos);

        return enemy;
    }

    /**
     * Генерирует предмет
     *
     * @param levelNum номер уровня
     * @return предмет
     */
    public static Item generateItem(int levelNum) {
        return switch (getRandomInt(1, 12)) {
            case 1, 2, 3, 4 -> generateFood();
            case 5, 6 -> generatePotion();
            case 7, 8 -> generateScroll();
            case 9, 10 -> generateArmor(levelNum);
            case 11, 12 -> generateWeapon(levelNum);
            default -> throw new IllegalStateException("Unexpected value: " + getRandomInt(1, 12));
        };
    }

    /**
     * Генерирует броню
     *
     * @param levelNum номер уровня
     * @return броня
     */
    public static Item generateArmor(int levelNum) {
        int maxValue = levelNum;
        if (levelNum >= armors.size()) {
            maxValue = armors.size() - 1;
        }
        return new Item(armors.get(getRandomInt(0, maxValue)));
    }

    /**
     * Генерирует оружие
     *
     * @param levelNum номер уровня
     * @return оружие
     */
    public static Item generateWeapon(int levelNum) {
        Item weapon = new Item(weapons.get(getRandomInt(0, weapons.size() - 1)));
        int minValue = 0;
        if (levelNum > 2) {
            minValue = levelNum - 2;
        }
        weapon.setValue(getRandomInt(minValue, levelNum + 2));
        return weapon;
    }

    /**
     * Генерирует свиток
     *
     * @return свиток
     */
    public static Item generateScroll() {
        Item scroll = new Item(scrolls.get(getRandomInt(0, scrolls.size() - 1)));
        scroll.setCount(getRandomInt(1, 3));
        return scroll;
    }

    /**
     * Генерирует зелье
     *
     * @return зелье
     */
    public static Item generatePotion() {
        Item potion = new Item(potions.get(getRandomInt(0, potions.size() - 1)));
        potion.setCount(getRandomInt(1, 3));
        return potion;
    }

    /**
     * Генерирует еду
     *
     * @return еда
     */
    public static Item generateFood() {
        Item food = new Item(foods.get(getRandomInt(0, foods.size() - 1)));
        food.setCount(getRandomInt(1, 3));
        return food;
    }

    /**
     * Генерирует рандомное число
     *
     * @param minValue минимальное возможное число
     * @param maxValue максимальное возможное число
     * @return рандомное число
     */
    public static int getRandomInt(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);
    }
}
