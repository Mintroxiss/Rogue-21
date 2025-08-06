package domain;

import domain.items.Item;
import domain.items.ItemSubtype;
import domain.items.ItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Inventory {
    private final int MAX_SIZE = 9;
    private final ArrayList<Item> items = initItems();

    public boolean isNotFull() {
        return items.size() <= MAX_SIZE;
    }


    /**
     * Возвращает требуемый предмет из инвентаря
     *
     * @param number индекс предмета в инвенторе
     * @param type тип обрабатываемых значений списка, null приемллем
     * @param isOnlyOne достать один предмет или все?
     * @return предмет, если он найден, иначе null
     * @throws IndexOutOfBoundsException инвентрарь переполнен
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
        for (Item item : items) {
            if (item.equals(newItem)) {
                item.setCount(item.getCount() + newItem.getCount());
                return true;
            }
        }
        if (items.size() + 1 > MAX_SIZE) {
            return false;
        }
        items.add(newItem);
        return true;
    }

    public char[][] getInventoryField(ItemType type, int ROWS, int COLUMNS) {
        char[][] inventoryField = new char[ROWS][COLUMNS];
        ArrayList<Item> items = new ArrayList<>();
        for (Item item : this.items) {
            if (type == null || type == item.getType()) {
                items.add(item);
            }
        }
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                inventoryField[i][j] = ' ';
            }
        }
        int num = 1;
        for (int index = 0; index < items.size(); index++) {
            int row = index * 2;
            if (row >= ROWS) break;
            Item item = items.get(index);
            String description = item.getDescription();
            String prefix = " " + num + ") ";
            for (int j = 0; j < prefix.length() && j < COLUMNS; j++) {
                inventoryField[row][j] = prefix.charAt(j);
            }
            for (int j = 0; j < description.length(); j++) {
                int col = prefix.length() + j;
                if (col < COLUMNS) {
                    inventoryField[row][col] = description.charAt(j);
                } else {
                    break;
                }
            }
            num++;
        }

        return inventoryField;
    }

    private ArrayList<Item> initItems() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(ItemType.FOOD, ItemSubtype.RAISIN_BREAD, 2));
        items.add(new Item(ItemType.SCROLL, ItemSubtype.SCROLL_OF_DEXTERITY, 5));
        items.add(new Item(ItemType.POTION, ItemSubtype.POTION_OF_VITALITY, 7));
        items.add(new Item(ItemType.FOOD, ItemSubtype.SLIME_MOLD, 2));
        items.add(new Item(ItemType.SCROLL, ItemSubtype.SCROLL_OF_VITALITY, 5));
        items.add(new Item(ItemType.POTION, ItemSubtype.POTION_OF_STRENGTH, 7));
        items.add(new Item(ItemType.FOOD, ItemSubtype.FOOD_RATION, 2));
        items.add(new Item(ItemType.SCROLL, ItemSubtype.SCROLL_OF_STRENGTH, 5));
        items.add(new Item(ItemType.POTION, ItemSubtype.POTION_OF_DEXTERITY, 7));
        return items;
    }
}
