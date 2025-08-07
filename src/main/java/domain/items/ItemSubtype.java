package domain.items;

import java.util.concurrent.ThreadLocalRandom;

public enum ItemSubtype {
    // WEAPON
    DAGGER("Dagger", generateWeapon()), MACE("Mace", generateWeapon()),
    LONG_SWORD("Long Sword", generateWeapon()), TWO_HANDED_SWORD("Two-handed sword", generateWeapon()),

    // ARMOR
    PLATE_MAIL("Plate mail", 3), BANDED_MAIL("Banded mail", 4),
    SPLINT_MAIL("Splint mail", 4), CHAIN_MAIL("Chain mail", 5),
    SCALE_MAIL("Scale mail", 6), RING_MAIL("Ring mail", 7),
    STUDDED_LEATHER("Studded leather", 8), LEATHER_ARMOR("Leather armor", 10),

    // SCROLL
    SCROLL_OF_DEXTERITY("Scroll of Dexterity", 1), SCROLL_OF_STRENGTH("Scroll of Strength", 1),
    SCROLL_OF_VITALITY("Scroll of Vitality", 1),

    // FOOD
    RAISIN_BREAD("Raisin Bread", 2), SLIME_MOLD("Slime Mold", 4),
    FRUIT("Fruit", 5), FOOD_RATION("Food Ration", 8),

    // POTION
    POTION_OF_DEXTERITY("Potion of Dexterity", 3), POTION_OF_STRENGTH("Potion of Strength", 3),
    POTION_OF_VITALITY("Potion of Vitality", 3),

    // TREASURE
    GOLD_PIECE("Golden piece", generateTreasure(100)), GEM("Gem", generateTreasure(500)),
    MAGIC_AMULET("Magic amulet", generateTreasure(1000)),

    TREASURES("Treasures", 0);

    private final String name;
    private int value;

    ItemSubtype(String name, int value) {
        this.name = name;
        this.value = value;
    }

    private static int generateTreasure(int maxValue) {
        return maxValue / ThreadLocalRandom.current().nextInt(1, 4);
    }

    private static int generateWeapon() {
        return ThreadLocalRandom.current().nextInt(0, 4);
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
