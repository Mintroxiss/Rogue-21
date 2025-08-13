package domain;

public enum InventoryMode {
    USE_FOOD("Eat food"),
    USE_POTION("Use potion"),
    USE_SCROLL("Use scroll"),
    SHOW_ITEMS("Inventory"),
    THROW_AWAY_ITEM("Throw away"),
    EQUIP_WEAPON("Equip weapon"),
    EQUIP_ARMOR("Equip armor");

    private final String action;

    InventoryMode(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
