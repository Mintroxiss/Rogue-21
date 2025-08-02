package domain;

import domain.items.InventoryItem;
import domain.items.Item;
import domain.items.ItemSubtype;

import java.util.ArrayList;
import java.util.Iterator;

public class Inventory {
    private final ArrayList<InventoryItem> items;

    public Inventory(ArrayList<InventoryItem> items) {
        this.items = items;
    }

    public Item takeItem(ItemSubtype subtype) {
        Iterator<InventoryItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            InventoryItem item = iterator.next();
            if (item.getSubtype() == subtype) {
                if (item.getCount() > 0) {
                    item.decreaseCount();
                    return item;
                } else {
                    iterator.remove();
                }
            }
        }
        return null;
    }

    public void putItem(Item newItem) {
        for (InventoryItem item : items) {
            if (item.equals(newItem)) {
                item.addCount();
                return;
            }
        }
        items.add(new InventoryItem(newItem, 1));
    }
}
