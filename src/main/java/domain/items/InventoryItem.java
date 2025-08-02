package domain.items;

public class InventoryItem extends Item {
    private Integer count;

    public InventoryItem(
            ItemType type,
            ItemSubtype subtype,
            Integer healthBoost,
            Integer maxHealthBoost,
            Integer agilityBoost,
            Integer strengthBoost,
            Integer price,
            Integer count
    ) {
        super(type, subtype, healthBoost, maxHealthBoost, agilityBoost, strengthBoost, price);
        if (count > 0) {
            this.count = count;
        } else {
            throw new IllegalArgumentException("Count of items can not be <= 0");
        }
    }

    public InventoryItem(Item item, Integer count) {
        this(
                item.getType(),
                item.getSubtype(),
                item.getHealthBoost(),
                item.getMaxHealthBoost(),
                item.getAgilityBoost(),
                item.getStrengthBoost(),
                item.getPrice(),
                count
        );
    }

    public Integer getCount() {
        return count;
    }

    public void addCount() {
        count++;
    }

    public void decreaseCount() {
        count--;
    }
}
