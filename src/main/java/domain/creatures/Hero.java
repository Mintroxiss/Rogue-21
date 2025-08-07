package domain.creatures;

import domain.Inventory;
import domain.cells.TileType;
import domain.items.Item;
import domain.items.ItemType;
import domain.positions.MovablePosition;

public class Hero extends Creature {
    private Integer maxHealth = 12;

    private final Inventory inventory;

    private int strengthPotionBoost = 0;
    private int agilityPotionBoost = 0;
    private int maxHealthPotionBoost = 0;

    private int strengthPotionDuration = 0;
    private int agilityPotionDuration = 0;
    private int maxHealthPotionDuration = 0;

    public Hero(MovablePosition pos, Item equippedWeapon, Item equippedArmor) {
        super(12, 5, 16, pos, TileType.HERO);
        this.inventory = new Inventory(equippedWeapon, equippedArmor);
    }

    public void decreasePotionDurations() {
        if (isStrengthPotionEffect()) {
            strengthPotionDuration--;
        }
        if (isAgilityPotionEffect()) {
            agilityPotionDuration--;
        }
        if (isMaxHealthPotionEffect()) {
            maxHealthPotionDuration--;
        }
        if (!isMaxHealthPotionEffect() && maxHealth < getHealth()) {
            setHealth(maxHealth);
        }
    }

    public boolean usePotion(int num) {
        boolean res = false;
        Item potion = inventory.takeItem(num, ItemType.POTION, true);
        if (potion != null) {
            int duration = 50;
            switch (potion.getSubtype()) {
                case POTION_OF_STRENGTH -> {
                    if (!isStrengthPotionEffect()) {
                        strengthPotionBoost = potion.getStrengthBoost();
                        strengthPotionDuration = duration;
                        res = true;
                    }
                }
                case POTION_OF_VITALITY -> {
                    if (!isMaxHealthPotionEffect()) {
                        maxHealthPotionBoost = potion.getMaxHealthBoost();
                        maxHealthPotionDuration = duration;
                        res = true;
                    }
                }
                case POTION_OF_DEXTERITY -> {
                    if (!isAgilityPotionEffect()) {
                        agilityPotionBoost = potion.getAgilityBoost();
                        agilityPotionDuration = duration;
                        res = true;
                    }
                }
            }
            if (!res) {
                inventory.putItem(potion);
            }
        }

        return res;
    }

    public int getTotalStrength() {
        int res = getStrength();
        Item weapon = inventory.getEquippedWeapon();
        if (weapon != null) {
            res += weapon.getStrengthBoost();
        }
        if (isStrengthPotionEffect()) {
            res += strengthPotionBoost;
        }
        return res;
    }

    public int getTotalAgility() {
        int res = getAgility();
        Item armor = inventory.getEquippedArmor();
        if (armor != null) {
            res += armor.getAgilityBoost();
        }
        if (isAgilityPotionEffect()) {
            res += agilityPotionBoost;
        }
        return res;
    }

    public int getTotalMaxHealth() {
        int res = maxHealth;
        if (isMaxHealthPotionEffect()) {
            res += maxHealthPotionBoost;
        }
        return res;
    }

    public void createInventoryField(ItemType type, int ROWS, int COLUMNS) {
        inventory.createInventoryField(type, ROWS, COLUMNS);
    }

    public boolean equipWeaponFromInventory(int num) {
        Item newWeapon = inventory.takeItem(num, ItemType.WEAPON, true);
        if (newWeapon == null) {
            return false;
        }
        inventory.putItem(inventory.getEquippedWeapon());
        inventory.setEquippedWeapon(newWeapon);
        return true;
    }

    public boolean equipArmorFromInventory(int num) {
        Item newArmor = inventory.takeItem(num, ItemType.ARMOR, true);
        if (newArmor == null) {
            return false;
        }
        inventory.putItem(inventory.getEquippedArmor());
        inventory.setEquippedArmor(newArmor);
        return true;
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

    public Item throwAwayEquippedWeapon() {
        Item item = inventory.getEquippedWeapon();
        inventory.setEquippedWeapon(null);
        return item;
    }

    public void setEquippedWeapon(Item item) {
        inventory.setEquippedWeapon(item);
    }

    public void setEquippedArmor(Item item) {
        inventory.setEquippedArmor(item);
    }

    public Item getEquippedWeapon() {
        return inventory.getEquippedWeapon();
    }

    public Item getEquippedArmor() {
        return inventory.getEquippedArmor();
    }

    public Item throwAwayEquippedArmor() {
        Item item = inventory.getEquippedArmor();
        inventory.setEquippedArmor(null);
        return item;
    }

    public boolean useFood(int num) {
        boolean res = false;
        Item item = inventory.takeItem(num, ItemType.FOOD, true);
        if (item != null) {
            int points = item.getHealthBoost();
            setHealth(Math.min(getHealth() + points, getTotalMaxHealth()));
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
            }

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

    public char[][] getInventoryField() {
        return inventory.getInventoryField();
    }

    public boolean isStrengthPotionEffect() {
        return strengthPotionDuration > 0;
    }

    public boolean isAgilityPotionEffect() {
        return agilityPotionDuration > 0;
    }

    public boolean isMaxHealthPotionEffect() {
        return maxHealthPotionDuration > 0;
    }
}
