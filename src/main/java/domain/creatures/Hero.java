package domain.creatures;

import domain.GameGenerator;
import domain.Inventory;
import domain.cells.TileType;
import domain.items.Item;
import domain.items.ItemType;

public class Hero extends Creature {
    private Integer maxHealth = 12;

    private int strengthPotionBoost = 0;
    private int agilityPotionBoost = 0;
    private int maxHealthPotionBoost = 0;

    private int strengthPotionDuration = 0;
    private int agilityPotionDuration = 0;
    private int maxHealthPotionDuration = 0;

    private final Inventory inventory = new Inventory();

    public Hero() {
        super(12, 5, 16, null, TileType.HERO);  // TODO
    }

    /**
     * Рассчитывает урон героя
     *
     * @return количество урона
     */
    public int hitEnemy(int enemyAgility) {
        int agilityDiff = this.getTotalAgility() - enemyAgility;
        int chanceToHit = 50 + agilityDiff * 5;
        chanceToHit = Math.max(5, Math.min(95, chanceToHit));
        if (GameGenerator.getRandomInt(1, 100) <= chanceToHit) {
            int damage = getTotalStrength();
            Item weapon = inventory.getEquippedWeapon();
            if (weapon != null) {
                damage += switch (weapon.getSubtype()) {
                    case DAGGER -> rollDice(1, 6);
                    case MACE -> rollDice(2, 4);
                    case TWO_HANDED_SWORD -> rollDice(4, 4);
                    case LONG_SWORD -> rollDice(3, 4);
                    default -> throw new IllegalArgumentException("Unacceptable type of weapon");
                };
            }
            return damage / GameGenerator.getRandomInt(1, 4);
        } else {
            return 0;
        }
    }

    public String decreaseHealth(int damage, EnemyType enemyType, boolean stunFl) {
        String str = "";
        health -= damage;
        if (damage != 0) {
            if (enemyType == EnemyType.VAMPIRE) {
                maxHealth -= 1;
                if (health > maxHealth) {
                    maxHealth -= 1;
                }
                str = ", sucked the life out";
            } else if (enemyType == EnemyType.SNAKE_MAGE && !stunState && !stunFl) {
                str = ", applied the stun effect";
                changeStunState();
            }
        }

        return str;
    }

    /**
     * Уменьшает время действия зелий на героя
     */
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
        if (!isMaxHealthPotionEffect() && maxHealth < health) {
            health = maxHealth;
        }
    }

    /**
     * Ищет зелье в инвентаре и применяет эффекты на героя
     *
     * @param num порядковый номер элемента в инвентаре
     * @return true, если зелье найдено в инвентаре и применены его эффекты
     */
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

    public Integer getGold() {
        return inventory.getGold();
    }

    /**
     * @return суммарная сила героя
     */
    public int getTotalStrength() {
        int res = strength;
        Item weapon = inventory.getEquippedWeapon();
        if (weapon != null) {
            res += weapon.getStrengthBoost();
        }
        if (isStrengthPotionEffect()) {
            res += strengthPotionBoost;
        }
        return res;
    }

    /**
     * @return сумарная ловкость героя
     */
    public int getTotalAgility() {
        int res = agility;
        Item armor = inventory.getEquippedArmor();
        if (armor != null) {
            res += armor.getAgilityBoost();
        }
        if (isAgilityPotionEffect()) {
            res += agilityPotionBoost;
        }
        return res;
    }

    /**
     * @return суммарное максимальное ХП героя
     */
    public int getTotalMaxHealth() {
        int res = maxHealth;
        if (isMaxHealthPotionEffect()) {
            res += maxHealthPotionBoost;
        }
        return res;
    }

    /**
     * Создает поле с информацией о предметах из инвентаря
     *
     * @param type    тип предметов (null, если все)
     * @param ROWS    количество строк в массиве
     * @param COLUMNS количество столбцов в массиве
     */
    public void createInventoryField(ItemType type, int ROWS, int COLUMNS) {
        inventory.createInventoryField(type, ROWS, COLUMNS);
    }

    /**
     * Экипирует новое оружие героя из инвентаря, старое помещает в него
     *
     * @param num номер нового оружия в инвентаре
     * @return true, если получилось экипировать оружие
     */
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

    /**
     * Выбрасывает предмет из инвентаря героя
     *
     * @param number номер предмета в инвентаре
     * @return true, если предмет выброшен
     */
    public Item throwAwayItem(int number) {
        return inventory.takeItem(number, null, false);
    }

    /**
     * Выбрасывает экипированное оружие
     *
     * @return разэкипированное оружие
     */
    public Item throwAwayEquippedWeapon() {
        Item item = inventory.getEquippedWeapon();
        inventory.setEquippedWeapon(null);
        return item;
    }


    /**
     * @param item новое экипированное оружие героя
     */
    public void setEquippedWeapon(Item item) {
        inventory.setEquippedWeapon(item);
    }


    /**
     * @param item новая экипированная броня героя
     */
    public void setEquippedArmor(Item item) {
        inventory.setEquippedArmor(item);
    }


    /**
     * @return экипированное оружие героя
     */
    public Item getEquippedWeapon() {
        return inventory.getEquippedWeapon();
    }

    /**
     * @return экипированная броня героя
     */
    public Item getEquippedArmor() {
        return inventory.getEquippedArmor();
    }

    /**
     * Выбрасывает экипированную броню из инвентаря
     *
     * @return разэкипированная броня
     */
    public Item throwAwayEquippedArmor() {
        Item item = inventory.getEquippedArmor();
        inventory.setEquippedArmor(null);
        return item;
    }


    /**
     * Ищет выбранную еду из инвентаря, после чего применяет её эффекты на героя
     *
     * @param num порядковый номер еды в инвентаре
     * @return true, если предмет найден и эффекты применены
     */
    public boolean useFood(int num) {
        boolean res = false;
        Item item = inventory.takeItem(num, ItemType.FOOD, true);
        if (item != null) {
            int points = item.getHealthBoost();
            health = Math.min(health + points, getTotalMaxHealth());
            res = true;
        }

        return res;
    }

    /**
     * Ищет выбранный свиток из инвентаря, после чего применяет его эффекты на героя
     *
     * @param num порядковый номер свитка в инвентаре
     * @return true, если предмет найден и эффекты применены
     */
    public boolean useScroll(int num) {
        boolean res = false;
        Item item = inventory.takeItem(num, ItemType.SCROLL, true);
        if (item != null) {
            switch (item.getSubtype()) {
                case SCROLL_OF_DEXTERITY -> {
                    int points = item.getAgilityBoost();
                    agility += points;
                }
                case SCROLL_OF_STRENGTH -> {
                    int points = item.getStrengthBoost();
                    strength += points;
                }
                case SCROLL_OF_VITALITY -> {
                    int points = item.getMaxHealthBoost();
                    maxHealth += points;
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
