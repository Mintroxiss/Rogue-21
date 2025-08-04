package domain;

import domain.items.Item;
import domain.items.ItemSubtype;

import java.util.ArrayList;
import java.util.Iterator;

public class Inventory {
    private final ArrayList<Item> items;

    public Inventory(ArrayList<Item> items) {
        this.items = items;
    }

    public Item takeItem(ItemSubtype subtype) {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getSubtype() == subtype) {
                if (item.getCount() > 0) {
                    item.setCount(item.getCount() - 1);
                    return item;
                } else {
                    iterator.remove();
                }
            }
        }
        return null;
    }


    /**
     * Кладёт новый предмет в инвентрарь
     * @param newItem предмет, складываемый в инвентарь
     */
    public void putItem(Item newItem) {
        for (Item item : items) {
            if (item.equals(newItem)) {
                item.setCount(item.getCount() + newItem.getCount());
                return;
            }
        }
        items.add(newItem);
    }
}
