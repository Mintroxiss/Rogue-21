package domain.entities.items;

public enum ItemSubtype {
    // WEAPON
    DAGGER("Dagger"), MACE("Mace"),
    LONG_SWORD("Long Sword"), TWO_HANDED_SWORD("Two-handed sword"),

    // ARMOR
    PLATE_MAIL("Plate mail"), BANDED_MAIL("Banded mail"),
    SPLINT_MAIL("Splint mail"), CHAIN_MAIL("Chain mail"),
    SCALE_MAIL("Scale mail"), RING_MAIL("Ring mail"),
    STUDDED_LEATHER("Studded leather"), LEATHER_ARMOR("Leather armor"),

    // SCROLL
    SCROLL_OF_DEXTERITY("Scroll of Dexterity"), SCROLL_OF_STRENGTH("Scroll of Strength"),
    SCROLL_OF_VITALITY("Scroll of Vitality"),

    // FOOD
    RAISIN_BREAD("Raisin Bread"), SLIME_MOLD("Slime Mold"),
    FRUIT("Fruit"), FOOD_RATION("Food Ration"),

    // POTION
    POTION_OF_DEXTERITY("Potion of Dexterity"), POTION_OF_STRENGTH("Potion of Strength"),
    POTION_OF_VITALITY("Potion of Vitality"),

    // TREASURE
    GOLD_PIECE("Golden piece"), GEM("Gem"),
    MAGIC_AMULET("Magic amulet"),

    TREASURES("Treasures");

    private final String name;

    ItemSubtype(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
