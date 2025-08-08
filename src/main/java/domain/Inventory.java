package domain;

import domain.items.Item;
import domain.items.ItemSubtype;
import domain.items.ItemType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Inventory {
    private final ArrayList<Item> items = GameGenerator.getFirstInventoryItems();
    private Item equippedWeapon;
    private Item equippedArmor;

    private char[][] inventoryField;

    public Inventory(Item equippedWeapon, Item equippedArmor) {
        this.equippedArmor = equippedArmor;
        this.equippedWeapon = equippedWeapon;
    }

    public char[][] getInventoryField() {
        return inventoryField;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedWeapon(Item equippedWeapon) {
        if (equippedWeapon == null || equippedWeapon.getType() == ItemType.WEAPON) {
            this.equippedWeapon = equippedWeapon;
        } else {
            throw new IllegalArgumentException("Item has not type WEAPON");
        }

    }

    public void setEquippedArmor(Item equippedArmor) {
        if (equippedArmor == null || equippedArmor.getType() == ItemType.ARMOR) {
            this.equippedArmor = equippedArmor;
        } else {
            throw new IllegalArgumentException("Item has not type ARMOR");
        }
    }

    /**
     * Возвращает требуемый предмет из инвентаря
     *
     * @param number    индекс предмета в инвентаре
     * @param type      тип обрабатываемых значений списка (null - все)
     * @param isOnlyOne достать один предмет или все?
     * @return предмет, если он найден, иначе null
     * @throws IndexOutOfBoundsException инвентарь переполнен
     */
    public Item takeItem(int number, ItemType type, boolean isOnlyOne) {
        ArrayList<Item> items;
        if (type == null) {
            items = this.items;
        } else {
            items = this.items.stream()
                    .filter(item -> item.getType() == type)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        for (Item item : items) {
            if (items.indexOf(item) == number - 1) {
                Item takeItem;
                if (isOnlyOne) {
                    takeItem = new Item(item, 1);
                    if (item.getCount() - 1 > 0) {
                        item.setCount(item.getCount() - 1);
                    } else {
                        this.items.remove(item);
                    }
                } else {
                    takeItem = item;
                    this.items.remove(item);
                }
                return takeItem;
            }
        }
        return null;
    }

    /**
     * Кладёт новый предмет в инвентрарь
     *
     * @param newItem предмет, складываемый в инвентарь
     * @return true, если инвентарь не переполнен
     */
    public boolean putItem(Item newItem) {
        boolean res = false;
        if (newItem != null) {
            int max_size = 9;
            if (newItem.getType() == ItemType.WEAPON) {
                if (equippedWeapon == null) {
                    equippedWeapon = newItem;
                    res = true;
                } else if (items.size() + 1 <= max_size) {
                    items.add(newItem);
                    res = true;
                }
            } else if (newItem.getType() == ItemType.ARMOR) {
                if (equippedArmor == null) {
                    equippedArmor = newItem;
                    res = true;
                } else if (items.size() + 1 <= max_size) {
                    items.add(newItem);
                    res = true;
                }
            } else if (newItem.getType() == ItemType.TREASURE) {
                Item treasuresItem = null;
                for (Item item : items) {
                    if (item.getSubtype() == ItemSubtype.TREASURES) {
                        treasuresItem = item;
                        break;
                    }
                }
                if (treasuresItem != null) {
                    treasuresItem.setPrice(treasuresItem.getPrice() + newItem.getPrice());
                    res = true;
                } else if (items.size() + 1 <= max_size) {
                    treasuresItem = new Item(ItemType.TREASURE, ItemSubtype.TREASURES, 1);
                    treasuresItem.setPrice(newItem.getPrice());
                    items.add(treasuresItem);
                    res = true;
                }

            } else {
                boolean found = false;
                for (Item item : items) {
                    if (item.equals(newItem)) {
                        item.setCount(item.getCount() + newItem.getCount());
                        res = true;
                        found = true;
                        break;
                    }
                }
                if (!found && items.size() + 1 <= max_size) {
                    items.add(newItem);
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * @return получить стоимость сокровищ в инвентаре
     */
    public int getGold() {
        Item treasures = null;
        for (Item item : items) {
            if (item.getSubtype() == ItemSubtype.TREASURES) {
                treasures = item;
                break;
            }
        }
        int res = 0;
        if (treasures != null) {
            res = treasures.getPrice();
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
        inventoryField = new char[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                inventoryField[i][j] = ' ';
            }
        }
        int row = 0;
        if (equippedArmor != null && (type == null || type == ItemType.ARMOR)) {
            int rowNum = row;
            String str = " -) " + equippedArmor.getDescription() + " (equipped)";
            for (int i = 0; i < str.length(); i++) {
                inventoryField[rowNum][i] = str.charAt(i);
            }
            row++;
        }

        if (equippedWeapon != null && (type == null || type == ItemType.WEAPON)) {
            int rowNum = row * 2;
            String str = " =) " + equippedWeapon.getDescription() + " (equipped)";
            for (int i = 0; i < str.length(); i++) {
                inventoryField[rowNum][i] = str.charAt(i);
            }
            row++;
        }

        ArrayList<Item> items = new ArrayList<>();
        for (Item item : this.items) {
            if (type == null || type == item.getType()) {
                items.add(item);
            }
        }
        int num = 1;
        for (Item value : items) {
            int rowNum = row * 2;

            if (rowNum >= ROWS) break;
            String description = value.getDescription();
            String prefix = " " + num + ") ";
            for (int j = 0; j < prefix.length() && j < COLUMNS; j++) {
                inventoryField[rowNum][j] = prefix.charAt(j);
            }
            for (int j = 0; j < description.length(); j++) {
                int col = prefix.length() + j;
                if (col < COLUMNS) {
                    inventoryField[rowNum][col] = description.charAt(j);
                } else {
                    break;
                }
            }
            num++;
            row++;
        }
    }
}
