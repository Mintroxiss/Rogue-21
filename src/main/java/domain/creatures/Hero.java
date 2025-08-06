package domain.creatures;

import domain.Inventory;
import domain.cells.TileType;
import domain.items.Item;
import domain.items.ItemType;
import domain.positions.MovablePosition;

import java.util.Objects;

public class Hero extends Creature {
    private Integer maxHealth = 12;
    private Item currentWeapon;

    private final Inventory inventory = new Inventory();
    private char[][] inventoryField;

    public void createInventoryField(ItemType type, int ROWS, int COLUMNS) {
        inventoryField = inventory.getInventoryField(type, ROWS, COLUMNS);
    }

    public Hero(MovablePosition pos, Item currentWeapon) {
        super(12, 5, 16, pos, TileType.HERO);
        this.currentWeapon = currentWeapon;
    }

    /**
     * Кладёт предмет в инвентарь героя
     *
     * @param item новый предмет в инвентарь
     * @return true, если инвентарь не переполнен
     */
    public boolean putItemIntoInventory(Item item) {
        return inventory.putItem(item);
    }

    public Item throwAwayItem(int number) {
        return inventory.takeItem(number, null, false);
    }

    public boolean useFood(int num) {
        boolean res = false;
        Item item = inventory.takeItem(num, ItemType.FOOD, true);
        if (item != null) {
            int points = item.getHealthBoost();
            if (getHealth() + points >= maxHealth) {
                setHealth(maxHealth);
            } else {
                setHealth(getHealth() + points);
            }
            res = true;
        }

        return res;
    }

    public boolean useScroll(int num) {
        boolean res = false;
        Item item = inventory.takeItem(num, ItemType.SCROLL, true);
        if (item != null) {
            switch (item.getSubtype()) {
                case SCROLL_OF_DEXTERITY -> {
                    int points = item.getAgilityBoost();
                    setAgility(getAgility() + points);
                }
                case SCROLL_OF_STRENGTH -> {
                    int points = item.getStrengthBoost();
                    setStrength(getStrength() + points);
                }
                case SCROLL_OF_VITALITY -> {
                    int points = item.getMaxHealthBoost();
                    setMaxHealth(getMaxHealth() + points);
                }
                default -> throw new IllegalArgumentException("Impossible subtype");
            };

            res = true;
        }

        return res;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void changeWeapon(Item weapon) {
        if (weapon.getType() == ItemType.WEAPON) {
            Item weaponCopy = currentWeapon;
            currentWeapon = weapon;
        }
    }

    public char[][] getInventoryField() {
        return inventoryField;
    }
}
