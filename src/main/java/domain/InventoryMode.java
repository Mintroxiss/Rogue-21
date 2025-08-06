package domain;

public enum InventoryMode {
    USE_FOOD("use item"),
    USE_POTION("use potion"),
    USE_SCROLL("use scroll"),
    PUT_ITEM("put item"),
    SHOW_ITEMS("show items"),
    THROW_AWAY_ITEM("throw away item"),
    EQUIP_WEAPON("equip weapon"),
    NOTHING("nothing");

    private final String action;

    InventoryMode(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
