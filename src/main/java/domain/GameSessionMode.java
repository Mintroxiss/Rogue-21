package domain;

public enum GameSessionMode {
    CONTINUE,
    ENTER_THE_NAME,
    GAME_FIELD,
    INVENTORY,
    SCORES;

    private InventoryMode inventoryMode = null;

    public void setInventoryMode(InventoryMode inventoryMode) {
        if (this == INVENTORY) {
            this.inventoryMode = inventoryMode;
        } else {
            this.inventoryMode = null;
        }
    }

    public InventoryMode getInventoryMode() {
        if (this == INVENTORY) {
            return inventoryMode;
        } else {
            return null;
        }
    }
}
